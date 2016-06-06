package ru.ares4322.moneytransfer.account;

import net.jcip.annotations.NotThreadSafe;
import ru.ares4322.moneytransfer.Account;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//TODO add logging like in TransferDaoImpl

/**
 * Only getNextId() thread safe.
 * For other methods for thread safety use lockXXX/unlockXXX methods.
 */
@NotThreadSafe
class AccountDaoImpl implements AccountDao {

    private final ConcurrentMap<Long, Account> accountIdToAccount = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, ReadWriteLock> accountIdToLock = new ConcurrentHashMap<>();

    private LongAdder nextId = new LongAdder();

    @Override
    public Account put(long accountId, Account account) {
        Account prevAccount = accountIdToAccount.putIfAbsent(accountId, account);
        if (prevAccount == null) {
            accountIdToLock.putIfAbsent(accountId, new ReentrantReadWriteLock(true));
            return account;
        }
        return prevAccount;
    }

    @Override
    public Optional<Account> get(long accountId) {
        return Optional.ofNullable(accountIdToAccount.get(accountId));
    }

    @Override
    public void updateAmount(long accountId, BigDecimal amount) {
        Account account = accountIdToAccount.get(accountId);
        if (account == null) {
            return;
        }
        accountIdToAccount.put(accountId, new Account(accountId, account.currency, amount));
    }

    @Override
    public boolean lockAccountForRead(long accountId, long time, TimeUnit timeUnit) throws InterruptedException {
        ReadWriteLock readWriteLock = accountIdToLock.get(accountId);
        return readWriteLock != null && readWriteLock.readLock()
                                                     .tryLock(time, timeUnit);
    }


    @Override
    public boolean lockAccountForWrite(long accountId, long time, TimeUnit timeUnit) throws InterruptedException {
        ReadWriteLock readWriteLock = accountIdToLock.get(accountId);
        return readWriteLock != null && readWriteLock.writeLock()
                                                     .tryLock(time, timeUnit);
    }

    @Override
    public boolean unlockAccountForRead(long accountId) {
        ReadWriteLock readWriteLock = accountIdToLock.get(accountId);
        if (readWriteLock == null) {
            return false;
        }
        readWriteLock.readLock().unlock();
        return true;
    }

    @Override
    public boolean unlockAccountForWrite(long accountId) {
        ReadWriteLock readWriteLock = accountIdToLock.get(accountId);
        if (readWriteLock == null) {
            return false;
        }
        readWriteLock.writeLock().unlock();
        return true;
    }

    @Override
    public long getNextId() {
        nextId.increment();
        return nextId.longValue();
    }

}