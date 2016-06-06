package ru.ares4322.moneytransfer.transfer;

import java.util.Objects;

public class TransferId {

    private final Long clientId;
    private final Long clientTransferId;

    public TransferId(Long clientId, Long clientTransferId) {
        this.clientId = clientId;
        this.clientTransferId = clientTransferId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TransferId)) {
            return false;
        }
        TransferId that = (TransferId) obj;
        return Objects.equals(clientId, that.clientId) && Objects.equals(clientTransferId, that.clientTransferId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientTransferId);
    }

    @Override
    public String toString() {
        return "TransferId{"
               + "clientId=" + clientId
               + ", clientTransferId=" + clientTransferId
               + '}';
    }
}
