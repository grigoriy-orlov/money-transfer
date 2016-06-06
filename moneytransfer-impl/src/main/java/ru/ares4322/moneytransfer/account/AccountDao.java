package ru.ares4322.moneytransfer.account;

import ru.ares4322.moneytransfer.Account;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface AccountDao {
    Account put(long accountId, Account account);

    Optional<Account> get(long accountId);

    void updateAmount(long accountId, BigDecimal amount);

    boolean lockAccountForRead(long accountId, long time, TimeUnit timeUnit) throws InterruptedException;

    boolean lockAccountForWrite(long accountId, long time, TimeUnit timeUnit) throws InterruptedException;

    boolean unlockAccountForRead(long accountId);

    boolean unlockAccountForWrite(long accountId);

    long getNextId();
}
