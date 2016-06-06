# Money transfer app

## Goal 
Web service with REST API for money transferring between accounts.
 
## Requirements
* [java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [maven 3](http://maven.apache.org/download.cgi)

## Building
Standard maven flow
`cd <project-dir>`
`mvn clean test` - compile + run unit tests
`mvn clean integration-test` - compile + run unit tests + run integration tests
`mvn clean install` - compile + run unit tests + run integration tests + create deploy artifacts
for `mvn clean deploy` need to fulfill deployment params for maven plugins 

## Running
`java -jar <project-dir>/moneytransfer-distribution/target/moneytransfer-distribution-<build-version>-jar-with-dependencies.jar`

## Deploying
* uber-jar - `<project-dir>/moneytransfer-distribution/target/moneytransfer-distribution-<build-version>-jar-with-dependencies.jar`
* flat zip - `<project-dir>/moneytransfer-distribution/target/moneytransfer-distribution-<build-version>-bin.zip`

## API
* `PUT /transfer/v1/{client_transfer_id}/client/{client_id} + content-type: application/json + body: {amount: number, sourceAccountId: number, destinationAccountId: number}` - create new transfer with key client_transfer_id+client_id and try to apply it or return existing 
* `GET /transfer/v1/{client_transfer_id}/client/{client_id} + content-type: application/json` - return existing transfer with key client_transfer_id+client_id or empty object

## Comments 
1. Project has maven checkstyle plugin with Google java style without Javadocs rules (because code must be self-documented except exported parts and very sly parts)
1. Project has one unit test and one integration concurrent test for app. But all classes must be covered by unit and integration tests in production version.
1. Project creates 2 deploy artifacts - jar-with-dependencies.jar (user-jar) for local running and bin.zip (flat zip) without configs for deploy to servers.
1. App doesn't have external config but it have to be in production version.
1. In production version app has to have POST, PUT and DELETE methods for all REST API and not hard-coded implementation for cross-currency rates.
1. In production version app has to have persistence DB instead of in-memory implementation.
1. PUT method is using because app works like part of distributed system and it needs an idempotent protocol for transfer transaction create/update.
1. Mvn modules: api - only for rest api (DTO and jax-rs interfaces), impl - api implementation and other logic, server - runtime logic for impl, distribution - for deployment and runnable artifacts (it is not user on install and deploy stage)
1. Logs have request id for grep log entries by request locally or on log collection system (for transfer endpoint). We have to add accounts or transfer ids for log entries for production version. 
1. For monitoring we may use dropwizard-metrics or collect stats on access.log with external tool.
1. OnlineTransferService - is a part of online scheme. For offline scheme we must create transfer in one class add transfer money in other class offline.
1. OnlineTransferService has fine-grained locking (lock per account) but it will be useful only on large contention. On large contention on one ot two accounts transactions may not apply and need to tune retry attempts and lock timeouts. 
 
