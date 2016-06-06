package ru.ares4322.moneytransfer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TransferResource {

    @PUT
    @Path("/v1/{client_transfer_id}/client/{client_id}")
    Response createOrGetTransfer(@PathParam("client_transfer_id") long clientTransferId,
                                 @PathParam("client_id") long clientId,
                                 WebTransfer webTransfer);

    @GET
    @Path("/v1/{client_transfer_id}/client/{client_id}")
    Response getTransfer(@PathParam("client_transfer_id") long clientTransferId, @PathParam("client_id") long clientId);
}
