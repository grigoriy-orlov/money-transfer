package ru.ares4322.moneytransfer;

import java.math.BigDecimal;
import java.util.Objects;

public final class Account {

    public final long id;
    public final Currency currency;
    public final BigDecimal amount;

    public Account(long id,
                   Currency currency,
                   BigDecimal amount) {
        this.id = id;
        this.currency = currency;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account)) {
            return false;
        }
        Account account = (Account) obj;
        return id == account.id
               && currency == account.currency
               && Objects.equals(amount, account.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currency, amount);
    }

    @Override
    public String toString() {
        return "Account{"
               + "id=" + id
               + ", currency=" + currency
               + ", amount=" + amount
               + '}';
    }
}
