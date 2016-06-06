package ru.ares4322.moneytransfer.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ru.ares4322.moneytransfer.Transfer;
import ru.ares4322.moneytransfer.TransferResource;
import ru.ares4322.moneytransfer.WebTransfer;

import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

//TODO add integration test for transfer API
//TODO if this class logic will be not change often add unit test for this class
class TransferResourceImpl implements TransferResource {

    private static final Logger log = LoggerFactory.getLogger(TransferResourceImpl.class);

    private final OnlineTransferService onlineTransferService;

    @Inject
    public TransferResourceImpl(OnlineTransferService onlineTransferService) {
        this.onlineTransferService = onlineTransferService;
    }

    @Override
    public Response createOrGetTransfer(long clientTransferId, long clientId, WebTransfer webTransfer) {
        UUID requestId = UUID.randomUUID();
        MDC.put("requestId", requestId.toString());
        try {
            log.info("start to handle transfer PUT; clientTransferId: {}, clientId: {}, webTransfer: {}",
                     clientTransferId, clientId, webTransfer);
            if (clientTransferId == 0 || clientId == 0 || webTransfer == null || webTransfer.amount == null
                || webTransfer.destinationAccountId == 0 || webTransfer.sourceAccountId == 0) {
                log.warn("transfer PUT handling error (wrong params); webTransfer: {}", webTransfer);
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("wrong transfer data")
                               .build();
            }
            Transfer transfer = TransferUtils.fromWebTransfer(webTransfer, clientId, clientTransferId);
            Transfer newTransfer = onlineTransferService.createOrGetTransfer(clientId, clientTransferId, transfer);

            WebTransfer newWebTransfer = TransferUtils.toWebTransfer(newTransfer);
            log.info("transfer PUT handling ok; webTransfer: {}", newWebTransfer);
            return Response.ok(newWebTransfer)
                           .build();
        } finally {
            MDC.remove("requestId");
        }
    }

    @Override
    public Response getTransfer(long clientTransferId, long clientId) {
        UUID requestId = UUID.randomUUID();
        MDC.put("requestId", requestId.toString());
        try {
            if (clientTransferId == 0 || clientId == 0) {
                log.warn("transfer GET handling error (wrong params); clientTransferId: {}, clientId: {}",
                         clientTransferId, clientId);
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("wrong transfer data")
                               .build();
            }
            log.info("start to handle transfer GET; clientTransferId: {}, clientId: {}", clientTransferId, clientId);
            Optional<Transfer> transfer = onlineTransferService.getTransfer(clientId, clientTransferId);
            log.info("transfer GET handling ok; webTransfer: {}", TransferUtils.toWebTransfer(transfer));
            return transfer.map(t -> Response.ok(TransferUtils.toWebTransfer(t))
                                             .build())
                           .orElse(Response.status(Response.Status.NOT_FOUND)
                                           .build());
        } finally {
            MDC.remove("requestId");
        }
    }
}