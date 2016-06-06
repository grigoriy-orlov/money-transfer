package ru.ares4322.moneytransfer;

import ru.ares4322.moneytransfer.transfer.TransferId;

import java.math.BigDecimal;
import java.util.Objects;

//TODO add create time and update time
public final class Transfer {

    public final TransferId transferId;
    public final long sourceAccountId;
    public final long destinationAccountId;
    public final BigDecimal amount;
    public final Status status;
    public final ErrorCode errorCode;

    public Transfer(TransferId transferId,
                    long sourceAccountId,
                    long destinationAccountId,
                    BigDecimal amount,
                    Status status,
                    ErrorCode errorCode) {
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.status = status;
        this.errorCode = errorCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Transfer)) {
            return false;
        }
        Transfer transfer = (Transfer) obj;
        return sourceAccountId == transfer.sourceAccountId
               && destinationAccountId == transfer.destinationAccountId
               && Objects.equals(transferId, transfer.transferId)
               && Objects.equals(amount, transfer.amount)
               && status == transfer.status
               && errorCode == transfer.errorCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferId, sourceAccountId, destinationAccountId, amount, status, errorCode);
    }

    @Override
    public String toString() {
        return "Transfer{"
               + "transferId=" + transferId
               + ", sourceAccountId=" + sourceAccountId
               + ", destinationAccountId=" + destinationAccountId
               + ", amount=" + amount
               + ", status=" + status
               + ", errorCode=" + errorCode
               + '}';
    }

    public static class Builder {
        private TransferId transferId;
        private long sourceAccountId;
        private long destinationAccountId;
        private BigDecimal amount;
        private Status status;
        private ErrorCode errorCode;

        public Builder() {
        }

        public Builder(Transfer transfer) {
            this.transferId = transfer.transferId;
            this.sourceAccountId = transfer.sourceAccountId;
            this.destinationAccountId = transfer.destinationAccountId;
            this.amount = transfer.amount;
            this.status = transfer.status;
            this.errorCode = transfer.errorCode;
        }

        public Builder setTransferId(TransferId transferId) {
            this.transferId = transferId;
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

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
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

        public Transfer build() {
            return new Transfer(transferId, sourceAccountId, destinationAccountId, amount, status, errorCode);
        }
    }
}
