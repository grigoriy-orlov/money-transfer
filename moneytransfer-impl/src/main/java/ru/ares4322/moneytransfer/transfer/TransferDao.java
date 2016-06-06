package ru.ares4322.moneytransfer.transfer;

import ru.ares4322.moneytransfer.ErrorCode;
import ru.ares4322.moneytransfer.Status;
import ru.ares4322.moneytransfer.Transfer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface TransferDao {

    Optional<Transfer> putIfAbsent(TransferId transferId, Transfer transfer);

    Optional<Transfer> get(TransferId transferId);

    Optional<Transfer> updateStatus(TransferId transferId, Status status);

    Optional<Transfer> updateErrorCode(TransferId transferId, ErrorCode errorCode);

    int size();

    boolean lockTransferForRead(TransferId transferId, long time, TimeUnit timeUnit) throws InterruptedException;

    boolean lockTransferForWrite(TransferId transferId, long time, TimeUnit timeUnit) throws InterruptedException;

    boolean unlockTransferForRead(TransferId transferId);

    boolean unlockTransferForWrite(TransferId transferId);

    void clear();
}
