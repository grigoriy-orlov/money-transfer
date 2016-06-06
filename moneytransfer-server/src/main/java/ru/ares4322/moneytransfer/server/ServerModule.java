package ru.ares4322.moneytransfer.server;

import com.google.inject.AbstractModule;

import ru.ares4322.moneytransfer.account.AccountModule;
import ru.ares4322.moneytransfer.crosscurrencyrates.CrossCurrencyRatesModule;
import ru.ares4322.moneytransfer.transfer.TransferModule;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new TransferModule());
        install(new AccountModule());
        install(new CrossCurrencyRatesModule());
    }
}
