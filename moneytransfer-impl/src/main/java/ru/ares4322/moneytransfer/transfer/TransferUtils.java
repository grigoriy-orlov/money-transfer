package ru.ares4322.moneytransfer.transfer;

import ru.ares4322.moneytransfer.Transfer;
import ru.ares4322.moneytransfer.WebTransfer;

import java.util.Optional;

abstract class TransferUtils {

    public static WebTransfer toWebTransfer(Transfer transfer) {
        return new WebTransfer.Builder().setAmount(transfer.amount)
                                        .setDestinationAccountId(transfer.destinationAccountId)
                                        .setSourceAccountId(transfer.sourceAccountId)
                                        .setStatus(transfer.status)
                                        .setErrorCode(transfer.errorCode)
                                        .build();
    }

    public static WebTransfer toWebTransfer(Optional<Transfer> transfer) {
        return transfer.map(t -> new WebTransfer.Builder().setAmount(t.amount)
                                                   .setDestinationAccountId(t.destinationAccountId)
                                                   .setSourceAccountId(t.sourceAccountId)
                                                   .setStatus(t.status)
                                                   .setErrorCode(t.errorCode)
                                                   .build())
                .orElse(new WebTransfer.Builder().build());
    }

    public static Transfer fromWebTransfer(WebTransfer webTransfer, long clientId, long clientTransferId) {
        return new Transfer.Builder().setTransferId(new TransferId(clientId, clientTransferId))
                                     .setSourceAccountId(webTransfer.sourceAccountId)
                                     .setDestinationAccountId(webTransfer.destinationAccountId)
                                     .setAmount(webTransfer.amount)
                                     .setStatus(webTransfer.status)
                                     .setErrorCode(webTransfer.errorCode)
                                     .build();
    }
}
