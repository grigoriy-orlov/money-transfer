package ru.ares4322.moneytransfer.account;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import ru.ares4322.moneytransfer.AccountResource;

public class AccountModule extends AbstractModule {

    @Override
    protected void configure() {
        binder().bind(AccountResource.class)
                .to(AccountResourceImpl.class)
                .in(Singleton.class);

        binder().bind(AccountService.class)
                .to(AccountServiceImpl.class)
                .in(Singleton.class);

        binder().bind(AccountDao.class)
                .to(AccountDaoImpl.class)
                .in(Singleton.class);
    }
}
