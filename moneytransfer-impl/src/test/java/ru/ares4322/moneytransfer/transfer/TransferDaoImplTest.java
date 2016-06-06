package ru.ares4322.moneytransfer.transfer;

import org.testng.annotations.Test;
import ru.ares4322.moneytransfer.ErrorCode;
import ru.ares4322.moneytransfer.Status;
import ru.ares4322.moneytransfer.Transfer;

import java.math.BigDecimal;
import java.util.Optional;

import static org.testng.Assert.assertEquals;

//TODO add lock/unlock tests
public class TransferDaoImplTest {

    @Test
    public void testPutAndGet() throws Exception {
        TransferDao transferDao = new TransferDaoImpl();
        assertEquals(transferDao.size(), 0);

        TransferId transferId1 = new TransferId(1L, 1L);
        TransferId transferId2 = new TransferId(2L, 1L);

        Transfer transfer1 = new Transfer.Builder().setTransferId(transferId1)
                                                   .setSourceAccountId(2L)
                                                   .setDestinationAccountId(3L)
                                                   .setAmount(new BigDecimal("100"))
                                                   .build();
        transferDao.putIfAbsent(transferId1, transfer1);

        assertEquals(transferDao.size(), 1);
        assertEquals(transferDao.get(transferId1), Optional.of(transfer1));

        Transfer transfer2 = new Transfer.Builder().setTransferId(transferId2)
                                                   .setSourceAccountId(4L)
                                                   .setDestinationAccountId(5L)
                                                   .setAmount(new BigDecimal("100"))
                                                   .build();
        transferDao.putIfAbsent(transferId2, transfer2);

        assertEquals(transferDao.size(), 2);
        assertEquals(transferDao.get(transferId1), Optional.of(transfer1));
        assertEquals(transferDao.get(transferId2), Optional.of(transfer2));

        transferDao.putIfAbsent(transferId2, new Transfer.Builder(transfer2).setSourceAccountId(7L)
                                                                                       .build());
        assertEquals(transferDao.size(), 2);
        assertEquals(transferDao.get(transferId1), Optional.of(transfer1));
        assertEquals(transferDao.get(transferId2), Optional.of(transfer2));
    }

    @Test
    public void testStatusUpdate() throws Exception {
        TransferDao transferDao = new TransferDaoImpl();
        assertEquals(transferDao.size(), 0);

        TransferId transferId1 = new TransferId(1L, 1L);
        Transfer transfer1 = new Transfer.Builder().setTransferId(transferId1)
                                                   .setSourceAccountId(2L)
                                                   .setDestinationAccountId(3L)
                                                   .setAmount(new BigDecimal("100"))
                                                   .build();

        transferDao.putIfAbsent(transferId1, transfer1);

        assertEquals(transferDao.size(), 1);
        assertEquals(transferDao.get(transferId1), Optional.of(transfer1));

        transferDao.updateStatus(transferId1, Status.PROCESSED);
        assertEquals(transferDao.get(transferId1).get().status, Status.PROCESSED);

        transferDao.updateStatus(transferId1, Status.FINISHED_OK);
        assertEquals(transferDao.get(transferId1).get().status, Status.FINISHED_OK);
    }

    @Test
    public void testErrorCodeUpdate() throws Exception {
        TransferDao transferDao = new TransferDaoImpl();
        assertEquals(transferDao.size(), 0);

        TransferId transferId1 = new TransferId(1L, 1L);
        Transfer transfer1 = new Transfer.Builder().setTransferId(transferId1)
                                                   .setSourceAccountId(2L)
                                                   .setDestinationAccountId(3L)
                                                   .setAmount(new BigDecimal("100"))
                                                   .build();

        transferDao.putIfAbsent(transferId1, transfer1);

        assertEquals(transferDao.size(), 1);
        assertEquals(transferDao.get(transferId1), Optional.of(transfer1));

        transferDao.updateErrorCode(transferId1, ErrorCode.OK);
        assertEquals(transferDao.get(transferId1).get().errorCode, ErrorCode.OK);

        transferDao.updateErrorCode(transferId1, ErrorCode.CONTENTION_ERROR);
        assertEquals(transferDao.get(transferId1).get().errorCode, ErrorCode.CONTENTION_ERROR);
    }
}