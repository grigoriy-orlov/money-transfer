package ru.ares4322.moneytransfer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//TODO add PUT and DELETE methods
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AccountResource {

    @GET
    @Path("/v1/{account_id}")
    Response get(@PathParam("account_id") long accountId);

    @POST
    @Path("/v1")
    Response create(WebAccount webAccount);
}
