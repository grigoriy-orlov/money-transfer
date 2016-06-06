package ru.ares4322.moneytransfer.transfer;

import com.google.inject.AbstractModule;

import ru.ares4322.moneytransfer.TransferResource;

public class TransferModule extends AbstractModule {

    @Override
    protected void configure() {
        binder().bind(TransferResource.class).to(TransferResourceImpl.class).asEagerSingleton();
        binder().bind(OnlineTransferService.class).to(OnlineTransferServiceImpl.class).asEagerSingleton();
        binder().bind(TransferDao.class).to(TransferDaoImpl.class).asEagerSingleton();
    }
}
