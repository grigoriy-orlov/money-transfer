package ru.ares4322.moneytransfer.crosscurrencyrates;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class CrossCurrencyRatesModule extends AbstractModule {

    @Override
    protected void configure() {
        binder().bind(CrossCurrencyRatesDao.class).to(CrossCurrencyRatesDaoImpl.class).in(Singleton.class);
    }
}
