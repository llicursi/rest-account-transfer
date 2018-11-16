# rest-account-transfer v1
[![Build Status](https://travis-ci.org/llicursi/rest-account-transfer.svg?branch=master)](https://travis-ci.org/llicursi/rest-account-transfer)
[![codecov](https://codecov.io/gh/llicursi/rest-account-transfer/branch/master/graph/badge.svg)](https://codecov.io/gh/llicursi/rest-account-transfer)
*Project is complete*.
  
Rest api for transferring money safely between two accounts, considering that parallel request shall be processed consistently and won't produce a negative account balance.

Basic premises for a transfer to work:
 - The accounts must exists
 - The source account must have enough money to transfer to a target account.
 - Allowed only _non-zero_ positive amount
 - Accounts are identified by a positive integer number

## Install and Execution

 1. Clone this repository `https://github.com/llicursi/rest-account-transfer.git` and use the only branch `master`
 2. Run `./mvnw clean package -DskipTests` on the root folder of this project. **jdk is required**
 3. Once the build is successful, run `java -jar target/transfer-service-1.0.0.jar`
 4. Access [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.htm) and "*voilà*"

By this point the rest Api should be running and a sample data is available, 
so you may be able to transfer some Westeros money  

## Testing 
Given the magic of the swagger, the following endpoints should be documented and available for testing 

### Web test
| Method | Endpoint | Controller method | Short description |
| ---- | --------- | ----------------------------------- | ----------------------------------------------------- |
| GET  | `/account`  | findAll                             | List all accounts                                     |
| POST | `/account`  | save                                | Save a new account                                    |
| GET  | `/account/{source}/incoming` | findAllIncoming   | List all incomes from a ***source*** account          |
| GET  | `/account/{source}/outgoing` | findAllOutgoing   | List all outgoing of a ***target*** account           |
| POST | `/account/{source}/transfer/{target}` | transfer | Transfer some money from ***source*** to ***target*** |

Use [swagger-ui](http://localhost:8080/swagger-ui.html) or call it directly like the following samples

##### Samples
 * [http://localhost:8080/account](http://localhost:8080/account) List all available accounts from Westeros          
 * [http://localhost:8080/account/13/incoming](http://localhost:8080/account/13/incoming) Show all transfers **to** Theon Greyjoy   
 * [http://localhost:8080/account/1/outgoing](http://localhost:8080/account/1/outgoing)  Show all transfers **from** Ned Stark 

The other two endpoints may require something to simulate a POST, like postman.

### Unit tests

 1. Run in a command line `./mwnw test`
 2. Wait for the following result 
 ``` 
 (...) Endeless lines of logs debug displays  
 [INFO] Results:
 [INFO]
 [INFO] Tests run: 61, Failures: 0, Errors: 0, Skipped: 0
 [INFO]
 ```
It's important to highlight the parallel integration test that simulates 12 transfer request from 
account A to account B:    
[whenTransfer_givenParallelTransfer_synchronouslyProcess](https://github.com/llicursi/rest-account-transfer/blob/master/src/test/java/com/licursi/rest/transferservice/controller/AccountControllerIntegrationTest.java#L131)

    GIVEN THAT: 
         `Tyrion` has a balance of '8000.00' and 12 transfer requests to 
         `Little Finger`, of an amount of '1000.00', will be processed in parallel.  
    THEN: 
          8 successful transaction (Status code : 200) 
          4 failed transaction (Status code : 400). 
          All `Tyrion` money should be in possess of `Little Finger`
          `Tyrion` balance should be a whole bunch of endless ZERO. Nothing
          
### Code coverage
Check my beautiful graph of test coverage. *It's a link*    
[![Codecov](https://codecov.io/gh/llicursi/rest-account-transfer/branch/master/graphs/sunburst.svg)](https://codecov.io/gh/llicursi/rest-account-transfer)



## Technologies

 | Name                    | Purpose |
 | --------------          | ------- |
 | Java 8                  | Coding language |
 | Spring Boot             | Stand-alone Spring applications |
 | Swagger                 | API documentation |
 | Lombok                  | Getters and Setters automatic generations |
 | H2                      | In memory database |
 | junit, mockito, assertj | Unit test |
 | Maven                   | Packaging |
 | TravisCi                | Continuous Integration |
 | Codecov                 | Test coverage using Cobertura |
          
***Notice*** : [Lombok](https://projectlombok.org/) **requires a plugin** to be installed on the IDE for better usage. 
Otherwise the lack of getter and setters generated during compilation time may be presented as an error on your IDE
 
## Contribution
Development is complete, but if you wanna review my code and suggest ***new cool stuff*** to test, mail me!
