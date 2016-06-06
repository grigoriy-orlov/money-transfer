package ru.ares4322.moneytransfer.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ares4322.moneytransfer.Account;
import ru.ares4322.moneytransfer.ErrorCode;
import ru.ares4322.moneytransfer.Status;
import ru.ares4322.moneytransfer.Transfer;
import ru.ares4322.moneytransfer.account.AccountDao;
import ru.ares4322.moneytransfer.crosscurrencyrates.CrossCurrencyRatesDao;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

//TODO add integration test for this service + Dao
//TODO if this class logic will be not change often add unit test for this class
//TODO lock timeout move to external config
class OnlineTransferServiceImpl implements OnlineTransferService {

    private static final Logger log = LoggerFactory.getLogger(OnlineTransferServiceImpl.class);

    //TODO move to external config
    private static final int RETRY_ATTEMPTS = 3;

    private final TransferDao transferDao;
    private final AccountDao accountDao;
    private final CrossCurrencyRatesDao crossCurrencyRatesDao;

    @Inject
    public OnlineTransferServiceImpl(TransferDao transferDao,
                                     AccountDao accountDao,
                                     CrossCurrencyRatesDao crossCurrencyRatesDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.crossCurrencyRatesDao = crossCurrencyRatesDao;
    }

    @Override
    public Transfer createOrGetTransfer(long clientId, long clientTransferId, Transfer transferParam) {
        Objects.requireNonNull(transferParam, "transfer param must be not null;");

        log.debug("start to create or get transfer: clientTransferId: {}, clientId: {}, transfer: {}", clientTransferId,
                  clientId, transferParam);

        Transfer transfer = new Transfer.Builder(transferParam).setStatus(Status.CREATED)
                                                               .setErrorCode(ErrorCode.OK)
                                                               .build();
        TransferId transferId = new TransferId(clientId, clientTransferId);

        Optional<Transfer> transferFromDb = transferDao.putIfAbsent(transferId, transfer);
        if (transferFromDb.isPresent() && !Status.CREATED.equals(transferFromDb.get().status)) {
            log.debug("transfer create or get ok (return already created and processed transfer); transfer: {}",
                      transferFromDb);
            return transferFromDb.get();
        }

        long destinationAccountId = transfer.destinationAccountId;
        long sourceAccountId = transfer.sourceAccountId;
        BigDecimal amount = transfer.amount;

        if (amount.compareTo(BigDecimal.ZERO) != 1) {
            log.warn("amount less or equals 0 (finish transfer processing); transfer id: {}", transferId);
            transferDao.updateStatus(transferId, Status.FINISHED_ERROR);
            transferDao.updateErrorCode(transferId, ErrorCode.WRONG_AMOUNT);
            return transferDao.get(transferId)
                              .orElseThrow(() -> new IllegalStateException(
                                  "transfer is absent after creation; id: " + transferId));
        }
        if (destinationAccountId == sourceAccountId) {
            log.warn("destinationAccountId equals sourceAccountId (finish transfer processing); transfer id: {}",
                     transferId);
            transferDao.updateStatus(transferId, Status.FINISHED_ERROR);
            transferDao.updateErrorCode(transferId, ErrorCode.SAME_ACCOUNT);
            return transferDao.get(transferId)
                              .orElseThrow(() -> new IllegalStateException(
                                  "transfer is absent after creation; id: " + transferId));
        }

        //TODO if we will add 'delete for account resource then we have to move this 2 checks to locks in while loop
        // below
        //TODO add while loop for some retry attempts like below
        try {
            accountDao.lockAccountForRead(destinationAccountId, 1, TimeUnit.SECONDS);
            Optional<Account> destinationAccountOpt = accountDao.get(destinationAccountId);
            if (!destinationAccountOpt.isPresent()) {
                log.warn("destination account not exists (finish transfer processing); transfer id: {}", transferId);
                transferDao.updateStatus(transferId, Status.FINISHED_ERROR);
                transferDao.updateErrorCode(transferId, ErrorCode.ACCOUNT_NOT_EXISTS);
                return transferDao.get(transferId)
                                  .orElseThrow(() -> new IllegalStateException(
                                      "transfer is absent after creation; id: " + transferId));
            }
        } catch (InterruptedException e) {
            log.error(String.format("destination account read lock interrupted error; destinationAccountId: %s",
                                    destinationAccountId), e);
            transferDao.updateStatus(transferId, Status.FINISHED_ERROR);
            transferDao.updateErrorCode(transferId, ErrorCode.CONTENTION_ERROR);
            return transferDao.get(transferId)
                              .orElseThrow(() -> new IllegalStateException(
                                  "transfer is absent after creation; id: " + transferId));
        } finally {
            accountDao.unlockAccountForRead(destinationAccountId);
        }

        try {
            accountDao.lockAccountForRead(sourceAccountId, 1, TimeUnit.SECONDS);
            Optional<Account> sourceAccountOpt = accountDao.get(sourceAccountId);
            if (!sourceAccountOpt.isPresent()) {
                log.warn("source account not exists (finish transfer processing); transfer id: {}", transferId);
                transferDao.updateStatus(transferId, Status.FINISHED_ERROR);
                transferDao.updateErrorCode(transferId, ErrorCode.ACCOUNT_NOT_EXISTS);
                return transferDao.get(transferId)
                                  .orElseThrow(() -> new IllegalStateException(
                                      "transfer is absent after creation; id: " + transferId));
            }
        } catch (InterruptedException e) {
            log.error(String.format("source account read lock interrupted error; sourceAccountId: %s",
                                    sourceAccountId), e);
            transferDao.updateStatus(transferId, Status.FINISHED_ERROR);
            transferDao.updateErrorCode(transferId, ErrorCode.CONTENTION_ERROR);
            return transferDao.get(transferId)
                              .orElseThrow(() -> new IllegalStateException(
                                  "transfer is absent after creation; id: " + transferId));
        } finally {
            accountDao.unlockAccountForRead(sourceAccountId);
        }

        int retryAttempts = RETRY_ATTEMPTS;
        while (--retryAttempts > 0) {
            try {
                if (sourceAccountId < destinationAccountId) { //need for dead-lock prevention
                    boolean isSourceAccountLocked = accountDao.lockAccountForWrite(sourceAccountId, 1,
                                                                                   TimeUnit.SECONDS);
                    if (!isSourceAccountLocked) {
                        log.warn("can't lock source account (try once again); sourceAccountId: {}", sourceAccountId);
                        continue;
                    }
                    boolean isDestinationAccountLocked = accountDao.lockAccountForWrite(destinationAccountId, 1,
                                                                                        TimeUnit.SECONDS);
                    if (!isDestinationAccountLocked) {
                        log.warn("can't lock source account (try once again); destinationAccountId: {}",
                                 destinationAccountId);
                        accountDao.unlockAccountForWrite(sourceAccountId);
                        continue;
                    }
                } else {
                    boolean isDestinationAccountLocked = accountDao.lockAccountForWrite(destinationAccountId, 1,
                                                                                        TimeUnit.SECONDS);
                    if (!isDestinationAccountLocked) {
                        log.warn("can't lock source account (try once again); destinationAccountId: {}",
                                 destinationAccountId);
                        continue;
                    }
                    boolean isSourceAccountLocked = accountDao.lockAccountForWrite(sourceAccountId, 1,
                                                                                   TimeUnit.SECONDS);
                    if (!isSourceAccountLocked) {
                        log.warn("can't lock source account (try once again); sourceAccountId: {}", sourceAccountId);
                        continue;
                    }
                }
                Account sourceAccount = accountDao.get(sourceAccountId)
                                                  .get();
                Account destinationAccount = accountDao.get(destinationAccountId)
                                                       .get();

                transferDao.lockTransferForWrite(transferId, 1, TimeUnit.SECONDS);
                transferDao.updateStatus(transferId, Status.PROCESSED);

                BigDecimal sourceAccountNewAmount = sourceAccount.amount.subtract(amount);
                if (sourceAccountNewAmount.compareTo(BigDecimal.ZERO) != 1) {
                    transferDao.updateStatus(transferId, Status.FINISHED_ERROR);
                    transferDao.updateErrorCode(transferId, ErrorCode.NOT_ENOUGH_MONEY);
                    return transferDao.get(transferId)
                                      .orElseThrow(() -> new IllegalStateException(
                                          "transfer is absent after creation; id: " + transferId));
                } else {
                    BigDecimal destinationAccountNewAmount = destinationAccount.amount.add(amount.multiply(
                        crossCurrencyRatesDao.get(sourceAccount.currency, destinationAccount.currency)));

                    accountDao.updateAmount(sourceAccountId, sourceAccountNewAmount);
                    accountDao.updateAmount(destinationAccountId, destinationAccountNewAmount);

                    transferDao.updateStatus(transferId, Status.FINISHED_OK);
                    transferDao.updateErrorCode(transferId, ErrorCode.OK);
                    break;
                }
            } catch (InterruptedException e) {
                log.error("account lock trying error during transfer processing (try lock once again); ", e);
            } catch (Exception e) {
                log.error("transfer processing unknown error; ", e);
            } finally {
                transferDao.unlockTransferForWrite(transferId);
                if (sourceAccountId < destinationAccountId) {
                    accountDao.unlockAccountForWrite(destinationAccountId);
                    accountDao.unlockAccountForWrite(sourceAccountId);
                } else {
                    accountDao.unlockAccountForWrite(sourceAccountId);
                    accountDao.unlockAccountForWrite(destinationAccountId);
                }
            }
        }
        if (retryAttempts == 0) {
            log.warn("transfer processing attempts limit exceed; transfer id: {}", transferId);
            transferDao.updateStatus(transferId, Status.FINISHED_ERROR);
            transferDao.updateErrorCode(transferId, ErrorCode.CONTENTION_ERROR);
            return transferDao.get(transferId)
                              .orElseThrow(() -> new IllegalStateException(
                                  "transfer is absent after creation; id: " + transferId));
        }
        log.debug("transfer create or get ok; transfer: {}", transfer);
        return transfer;
    }

    @Override
    public Optional<Transfer> getTransfer(long clientId, long clientTransferId) {
        log.debug("start to get transfer: clientTransferId: {}, clientId: {}", clientTransferId, clientId);
        Optional<Transfer> transfer = transferDao.get(new TransferId(clientId, clientTransferId));
        log.debug("transfer create or get ok; transfer: {}", transfer);
        return transfer;
    }
}
