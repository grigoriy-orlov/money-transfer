package ru.ares4322.moneytransfer.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ares4322.moneytransfer.Account;
import ru.ares4322.moneytransfer.AccountResource;
import ru.ares4322.moneytransfer.WebAccount;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

//TODO add logging like in TransferResourceImpl
//TODO integration test for account API
//TODO if this class logic will be not change often add unit test for this class
class AccountResourceImpl implements AccountResource {

    private static final Logger log = LoggerFactory.getLogger(AccountResourceImpl.class);

    private final AccountService accountService;

    @Inject
    public AccountResourceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Response get(long accountId) {
        if (accountId == 0) {
            Response.status(Response.Status.BAD_REQUEST)
                    .entity("wrong account data")
                    .build();
        }
        try {
            return accountService.get(accountId)
                             .map(account -> Response.ok(AccountUtils.toWebAccount(account)).build())
                             .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (Exception e) {
            log.error("account getting error; {}", e);
            return Response.serverError().build();
        }
    }

    @Override
    public Response create(WebAccount webAccount) {
        if (webAccount.amount == null || webAccount.currencyId == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("wrong account data")
                           .build();
        }
        Account newAccount = AccountUtils.fromWebAccount(webAccount);
        Account accountFromDb = accountService.create(newAccount);
        return Response.ok(AccountUtils.toWebAccount(accountFromDb)).build();
    }
}
