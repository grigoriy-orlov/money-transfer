package ru.ares4322.moneytransfer.account;

import ru.ares4322.moneytransfer.Account;

import java.util.Optional;

public interface AccountService {
    Account create(Account account);

    Optional<Account> get(long accountId);
}
