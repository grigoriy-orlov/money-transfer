package ru.ares4322.moneytransfer;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

//TODO add create time and update time
//TODO remove jackson annotations to serializer class to impl module
public class WebTransfer {

    public final BigDecimal amount;
    public final long sourceAccountId;
    public final long destinationAccountId;
    public final Status status;
    public final ErrorCode errorCode;

    public WebTransfer() {
        amount = null;
        sourceAccountId = 0;
        destinationAccountId = 0;
        status = null;
        errorCode = null;
    }

    @JsonCreator
    public WebTransfer(@JsonProperty("amount") BigDecimal amount,
                       @JsonProperty("sourceAccountId") long sourceAccountId,
                       @JsonProperty("destinationAccountId") long destinationAccountId,
                       @JsonProperty("status") Status status,
                       @JsonProperty("errorCode") ErrorCode errorCode) {
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.status = status;
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WebTransfer)) {
            return false;
        }
        WebTransfer that = (WebTransfer) obj;
        return Objects.equals(amount, that.amount)
               && Objects.equals(sourceAccountId, that.sourceAccountId)
               && Objects.equals(destinationAccountId, that.destinationAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, sourceAccountId, destinationAccountId);
    }

    @Override
    public String toString() {
        return "WebTransfer{"
               + "amount=" + amount
               + ", sourceAccountId=" + sourceAccountId
               + ", destinationAccountId=" + destinationAccountId
               + '}';
    }


    public static class Builder {
        private BigDecimal amount;
        private long sourceAccountId;
        private long destinationAccountId;
        private Status status;
        private ErrorCode errorCode;

        public Builder() {
        }

        public Builder(WebTransfer webTransfer) {
            this.amount = webTransfer.amount;
            this.sourceAccountId = webTransfer.sourceAccountId;
            this.destinationAccountId = webTransfer.destinationAccountId;
            this.status = webTransfer.status;
            this.errorCode = webTransfer.errorCode;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder setSourceAccountId(long sourceAccountId) {
            this.sourceAccountId = sourceAccountId;
            return this;
        }

        public Builder setDestinationAccountId(long destinationAccountId) {
            this.destinationAccountId = destinationAccountId;
            return this;
        }

        public Builder setStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder setErrorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public WebTransfer build() {
            return new WebTransfer(amount, sourceAccountId, destinationAccountId, status, errorCode);
        }
    }
}
