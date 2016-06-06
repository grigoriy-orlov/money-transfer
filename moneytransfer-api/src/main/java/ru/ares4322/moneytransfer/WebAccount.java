package ru.ares4322.moneytransfer;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

//TODO remove jackson annotations to serializer class to impl module
public final class WebAccount {

    public final long id;
    public final int currencyId;
    public final BigDecimal amount;

    @JsonCreator
    public WebAccount(@JsonProperty("id") long id,
                      @JsonProperty("currencyId") int currencyId,
                      @JsonProperty("amount") BigDecimal amount) {
        this.id = id;
        this.currencyId = currencyId;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WebAccount)) {
            return false;
        }
        WebAccount that = (WebAccount) obj;
        return id == that.id
               && currencyId == that.currencyId
               && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyId, amount);
    }

    @Override
    public String toString() {
        return "WebAccount{"
               + "id=" + id
               + ", currencyId=" + currencyId
               + ", amount=" + amount
               + '}';
    }
}
