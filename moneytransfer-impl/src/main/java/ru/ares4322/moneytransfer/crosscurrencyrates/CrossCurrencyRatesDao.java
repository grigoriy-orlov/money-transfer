package ru.ares4322.moneytransfer.crosscurrencyrates;

import ru.ares4322.moneytransfer.Currency;

import java.math.BigDecimal;

public interface CrossCurrencyRatesDao {

    BigDecimal get(Currency sourceCurrency, Currency destinationCurrency);
}
