package ru.ares4322.moneytransfer.transfer;

import ru.ares4322.moneytransfer.Transfer;

import java.util.Optional;

public interface OnlineTransferService {

    Transfer createOrGetTransfer(long clientId, long clientTransferId, Transfer transfer);

    Optional<Transfer> getTransfer(long clientId, long clientTransferId);
}
