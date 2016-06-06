package ru.ares4322.moneytransfer.server;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.ares4322.moneytransfer.AccountResource;
import ru.ares4322.moneytransfer.Currency;
import ru.ares4322.moneytransfer.TransferResource;
import ru.ares4322.moneytransfer.WebAccount;
import ru.ares4322.moneytransfer.WebTransfer;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class AppIT {

    private static final int PORT = 8080;
    private static final String URL = "http://localhost:8080/";

    private HttpServer server;

    @BeforeClass
    public void setUp() throws Exception {
        server = new HttpServer(PORT);
        server.start();

        AccountResource accountResource = getProxy(AccountResource.class);
        WebAccount account1 = accountResource.create(new WebAccount(0, Currency.RUB.id, new BigDecimal("1000000")))
                                             .readEntity(WebAccount.class);
        WebAccount account2 = accountResource.create(new WebAccount(0, Currency.USD.id, new BigDecimal("500000")))
                                             .readEntity(WebAccount.class);
        WebAccount account3 = accountResource.create(new WebAccount(0, Currency.GBP.id, new BigDecimal("250000")))
                                             .readEntity(WebAccount.class);
        assertNotNull(account1);
        assertNotEquals(account1.id, 0);
        assertEquals(account1.id, 1);
        assertNotNull(account2);
        assertNotEquals(account2.id, 0);
        assertEquals(account2.id, 2);
        assertNotNull(account3);
        assertNotEquals(account3.id, 0);
        assertEquals(account3.id, 3);
    }

    @AfterClass
    public void tearDown() throws Exception {
        server.stop();
    }

    /**
     * try to transfer money N times concurrently between 3 accounts with different currencies through REST API
     * and compare initial and final account amounts sum
     */
    @Test
    public void concurrencyAndFunctionalTest() throws Exception {
        final int callTimes = 1000;

        int threads = Runtime.getRuntime()
                             .availableProcessors();
        final CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        LongAdder clientIdCounter = new LongAdder();
        for (int i = 0; i < threads; i++) {
            executor.execute(() -> {
                clientIdCounter.increment();
                long clientId = Thread.currentThread().getId();
                int clientTransferId = 1;
                Random random = new Random();
                TransferResource transferResource = getProxy(TransferResource.class);
                for (int j = 0; j < callTimes; j++) {
                    int amount = random.nextInt(50);
                    int sourceAccountId = random.nextInt(3) + 1;
                    int destinationAccountId = random.nextInt(3) + 1;
                    transferResource.createOrGetTransfer(clientTransferId++, clientId,
                                                         new WebTransfer(new BigDecimal(amount), sourceAccountId,
                                                                         destinationAccountId, null, null))
                                    .close();
                }
                latch.countDown();
            });
        }
        latch.await();

        AccountResource accountResource = getProxy(AccountResource.class);
        WebAccount account1 = accountResource.get(1).readEntity(WebAccount.class);
        WebAccount account2 = accountResource.get(2).readEntity(WebAccount.class);
        WebAccount account3 = accountResource.get(3).readEntity(WebAccount.class);
        BigDecimal amount2InRub = account2.amount.multiply(new BigDecimal("2"));
        BigDecimal amount3InRub = account3.amount.multiply(new BigDecimal("4"));
        BigDecimal expectedAmount = new BigDecimal("3000000").stripTrailingZeros();

        assertEquals(account1.amount.add(amount2InRub)
                                    .add(amount3InRub)
                                    .stripTrailingZeros(), expectedAmount);
    }

    private static <T> T getProxy(Class<T> iFace) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URL);
        ResteasyWebTarget resteasyWebTarget = (ResteasyWebTarget) webTarget;
        return resteasyWebTarget.proxy(iFace);
    }
}