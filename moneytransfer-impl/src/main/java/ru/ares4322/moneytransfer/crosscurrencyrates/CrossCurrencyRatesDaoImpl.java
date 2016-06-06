package ru.ares4322.moneytransfer.crosscurrencyrates;

import ru.ares4322.moneytransfer.Currency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//TODO add Resource, Service and dynamic storage
//TODO add logging like in TransferDaoImpl
//TODO add unit test for in-memory and integration test for remote DB implementations
class CrossCurrencyRatesDaoImpl implements CrossCurrencyRatesDao {

    private final Map<CrossCurrencyId, BigDecimal> crossCurrencyRates = new HashMap<>();

    public CrossCurrencyRatesDaoImpl() {
        crossCurrencyRates.put(new CrossCurrencyId(Currency.RUB, Currency.USD), new BigDecimal("0.5"));
        crossCurrencyRates.put(new CrossCurrencyId(Currency.USD, Currency.RUB), new BigDecimal("2"));
        crossCurrencyRates.put(new CrossCurrencyId(Currency.RUB, Currency.GBP), new BigDecimal("0.25"));
        crossCurrencyRates.put(new CrossCurrencyId(Currency.GBP, Currency.RUB), new BigDecimal("4"));
        crossCurrencyRates.put(new CrossCurrencyId(Currency.USD, Currency.GBP), new BigDecimal("0.5"));
        crossCurrencyRates.put(new CrossCurrencyId(Currency.GBP, Currency.USD), new BigDecimal("2"));
    }

    @Override
    public BigDecimal get(Currency sourceCurrency, Currency destinationCurrency) {
        Objects.requireNonNull(sourceCurrency, "sourceCurrency param must be not null");
        Objects.requireNonNull(sourceCurrency, "destinationCurrency param must be not null");

        return crossCurrencyRates.get(new CrossCurrencyId(sourceCurrency, destinationCurrency));
    }

    private class CrossCurrencyId {
        public final Currency sourceCurrency;
        public final Currency destinationCurrency;

        private CrossCurrencyId(Currency sourceCurrency, Currency destinationCurrency) {
            this.sourceCurrency = sourceCurrency;
            this.destinationCurrency = destinationCurrency;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CrossCurrencyId)) {
                return false;
            }
            CrossCurrencyId that = (CrossCurrencyId) obj;
            return sourceCurrency == that.sourceCurrency && destinationCurrency == that.destinationCurrency;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceCurrency, destinationCurrency);
        }

        @Override
        public String toString() {
            return "CrossCurrencyId{" + "sourceCurrency=" + sourceCurrency + ", destinationCurrency="
                   + destinationCurrency + '}';
        }
    }

}
