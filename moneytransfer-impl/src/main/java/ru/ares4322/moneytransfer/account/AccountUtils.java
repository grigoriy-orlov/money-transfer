package ru.ares4322.moneytransfer.account;

import ru.ares4322.moneytransfer.Account;
import ru.ares4322.moneytransfer.Currency;
import ru.ares4322.moneytransfer.WebAccount;

abstract class AccountUtils {

    public static Account fromWebAccount(WebAccount webAccount) {
        return new Account(webAccount.id, Currency.byId(webAccount.currencyId), webAccount.amount);
    }

    public static WebAccount toWebAccount(Account account) {
        return new WebAccount(account.id, account.currency.id, account.amount);
    }
}
