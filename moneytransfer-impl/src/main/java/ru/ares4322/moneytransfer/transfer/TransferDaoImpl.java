package ru.ares4322.moneytransfer.transfer;

import net.jcip.annotations.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ares4322.moneytransfer.ErrorCode;
import ru.ares4322.moneytransfer.Status;
import ru.ares4322.moneytransfer.Transfer;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//TODO add unit test for in-memory and integration test for remote DB implementations
//TODO create logging for all methods
//TODO add javadoc for lock/unlock methods
/**
 * For other methods for thread safety use lockXXX/unlockXXX methods.
 */
@NotThreadSafe
class TransferDaoImpl implements TransferDao {

    private static final Logger log = LoggerFactory.getLogger(TransferDaoImpl.class);

    private final ConcurrentMap<TransferId, Transfer> clientTransferIdToTransfer = new ConcurrentHashMap<>();
    private final ConcurrentMap<TransferId, ReadWriteLock> clientTransferIdToLock = new ConcurrentHashMap<>();

    @Override
    public Optional<Transfer> putIfAbsent(TransferId transferId, Transfer transfer) {
        Objects.requireNonNull(transferId, "transferId param must be not null");
        Objects.requireNonNull(transfer, "transfer param must be not null");

        log.debug("start to add or get transfer; transferId: {}, transfer: {}", transferId, transfer);
        Transfer prevTransfer = clientTransferIdToTransfer.putIfAbsent(transferId, transfer);
        if (prevTransfer == null) {
            clientTransferIdToLock.putIfAbsent(transferId, new ReentrantReadWriteLock(true));
            log.debug("new transfer created; transferId: {}, transfer: {}", transferId, transfer);
            return Optional.empty();
        }
        log.debug("return existing transfer; transferId: {}, transfer: {}", transferId, prevTransfer);
        return Optional.of(prevTransfer);
    }

    @Override
    public Optional<Transfer> get(TransferId transferId) {
        Objects.requireNonNull(transferId, "transferId param must be not null");

        log.debug("start to get transfer; transferId: {}", transferId);
        Optional<Transfer> transfer = Optional.ofNullable(clientTransferIdToTransfer.get(transferId));
        log.debug("getting transfer is ok; transfer: {}", transfer);
        return transfer;
    }

    @Override
    public Optional<Transfer> updateStatus(TransferId transferId, Status status) {
        Objects.requireNonNull(transferId, "transferId param must be not null");
        Objects.requireNonNull(status, "status param must be not null");

        log.debug("start to update transfer status; transferId: {}, status: {}", transferId, status);
        Optional<Transfer> updatedTransfer = Optional.ofNullable(clientTransferIdToTransfer.get(transferId))
                                                     .map(t -> new Transfer.Builder(t).setStatus(status)
                                                                                      .build());
        if (!updatedTransfer.isPresent()) {
            log.debug("transfer status update is ok (updated transfer is absent); transferId: {}, status: {}",
                      transferId, status);
        } else {
            clientTransferIdToTransfer.put(transferId, updatedTransfer.get());
            log.debug("transfer status update is ok; transferId: {}, status: {}, updated transfer: {}", transferId,
                      status, updatedTransfer);
        }
        return updatedTransfer;
    }

    @Override
    public Optional<Transfer> updateErrorCode(TransferId transferId, ErrorCode errorCode) {
        Objects.requireNonNull(transferId, "transferId param must be not null");
        Objects.requireNonNull(errorCode, "errorCode param must be not null");

        log.debug("start to update transfer error code; transferId: {}, error code: {}", transferId, errorCode);
        Optional<Transfer> updatedTransfer = Optional.ofNullable(clientTransferIdToTransfer.get(transferId))
                                                     .map(t -> new Transfer.Builder(t).setErrorCode(errorCode)
                                                                                      .build());
        if (!updatedTransfer.isPresent()) {
            log.debug("transfer status update is ok (updated transfer is absent); transferId: {}, status: {}",
                      transferId, errorCode);
        } else {
            clientTransferIdToTransfer.put(transferId, updatedTransfer.get());
            log.debug("transfer status update is ok; transferId: {}, status: {}, updated transfer: {}", transferId,
                      errorCode, updatedTransfer);
        }
        log.debug("transfer status update is ok; transferId: {}, error code: {}, updated transfer{} ", transferId,
                  errorCode, updatedTransfer);
        return updatedTransfer;
    }

    @Override
    public int size() {
        return clientTransferIdToTransfer.size();
    }

    @Override
    public boolean lockTransferForRead(TransferId transferId, long time, TimeUnit timeUnit)
        throws InterruptedException {
        Objects.requireNonNull(transferId, "transferId param must be not null");
        Objects.requireNonNull(transferId, "timeUnit param must be not null");

        log.debug("start to lock transfer for read; transferId: {}", transferId);
        ReadWriteLock readWriteLock = clientTransferIdToLock.get(transferId);
        if (readWriteLock == null) {
            log.debug("can't lock transfer (transfer lock is absent); transferId: {}", transferId);
            return false;
        }
        readWriteLock.readLock()
                     .tryLock(time, timeUnit);
        log.debug("transfer lock for read is ok; transferId: {}", transferId);
        return true;
    }

    @Override
    public boolean lockTransferForWrite(TransferId transferId, long time, TimeUnit timeUnit)
        throws InterruptedException {
        Objects.requireNonNull(transferId, "transferId param must be not null");
        Objects.requireNonNull(transferId, "timeUnit param must be not null");

        log.debug("start to lock transfer for write; transferId: {}", transferId);
        ReadWriteLock readWriteLock = clientTransferIdToLock.get(transferId);
        if (readWriteLock == null) {
            log.debug("can't lock transfer (transfer lock is absent); transferId: {}", transferId);
            return false;
        }
        readWriteLock.writeLock()
                     .tryLock(time, timeUnit);
        log.debug("transfer lock for write is ok; transferId: {}", transferId);
        return true;
    }

    @Override
    public boolean unlockTransferForRead(TransferId transferId) {
        Objects.requireNonNull(transferId, "transferId param must be not null");

        log.debug("start to unlock transfer for read; transferId: {}", transferId);
        ReadWriteLock readWriteLock = clientTransferIdToLock.get(transferId);
        if (readWriteLock == null) {
            log.debug("can't unlock transfer (transfer lock is absent); transferId: {}", transferId);
            return false;
        }
        readWriteLock.readLock()
                     .unlock();
        log.debug("transfer unlock for read is ok; transferId: {}", transferId);
        return true;
    }

    @Override
    public boolean unlockTransferForWrite(TransferId transferId) {
        Objects.requireNonNull(transferId, "transferId param must be not null");

        log.debug("start to unlock transfer for write; transferId: {}", transferId);
        ReadWriteLock readWriteLock = clientTransferIdToLock.get(transferId);
        if (readWriteLock == null) {
            log.debug("can't unlock transfer (transfer lock is absent); transferId: {}", transferId);
            return false;
        }
        readWriteLock.writeLock()
                     .unlock();
        log.debug("transfer unlock for write is ok; transferId: {}", transferId);
        return true;
    }

    @Override
    public void clear() {
        clientTransferIdToTransfer.clear();
        clientTransferIdToLock.clear();
    }
}
