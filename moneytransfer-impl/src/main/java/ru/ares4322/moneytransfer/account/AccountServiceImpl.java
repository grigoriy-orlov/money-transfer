package ru.ares4322.moneytransfer.account;

import ru.ares4322.moneytransfer.Account;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

//TODO add logging like in TransferServiceImpl
//TODO integration test for this service + Dao
//TODO if this class logic will be not change often add unit test for this class
class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;

    @Inject
    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account create(Account account) {
        Objects.requireNonNull(account, "account param must be not null;");
        long accountId = accountDao.getNextId();
        return accountDao.put(accountId, new Account(accountId, account.currency, account.amount));
    }

    @Override
    public Optional<Account> get(long accountId) {
        try {
            accountDao.lockAccountForRead(accountId, 1, TimeUnit.SECONDS);
            return accountDao.get(accountId);
        } catch (InterruptedException e) {
            throw new IllegalStateException("account getting interruption error;", e);
        } finally {
            accountDao.unlockAccountForRead(accountId);
        }
    }
}
