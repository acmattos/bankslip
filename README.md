# BankSlip API

Simple REST API to deal with bank slip creation, payment, cancelation and fine 
calculation. 
Besides, it explores features of [Lombok](https://projectlombok.org/
"Project Lombok"), a java library that automatically plugs into your editor and
build tools, spicing up your java, and [Swagger Specification](
https://swagger.io/ "Swagger OpenAPI"), that allows anyone to visualize and 
interact with the API’s resources without having any of the implementation logic
in place.

## Specification

This REST API was built to generate bank slips and will be used by a Financial
Management System (FMS) module.

In order to allow FMS module to work properly, some endpoints must be available
and de following sections will describe its requirements.

### Create Bank Slip Endpoint

This endpoint will receive a valid bank slip and store it on database, to be 
consumed by the API itself. 

**Endpoint:** *POST* http://localhost:8080/rest/bankslips

**Format:**

This is a sample of a valid bank slip JSON (request body) object:

```
{
   "due_date" : "2018-01-01" ,
   "total_in_cents" : "100000" ,
   "customer" : "ACME Company" ,
   "status" : "PENDING"
}
```

| Field *        | Type       | Format                  |
| -------------- | :--------: | ----------------------- |
| due_date       | Date       | yyyy-MM-dd              |
| total_in_cents | BigDecimal | Amount in Cents         |
| customer       | String     |                         |
| status         | String     | PENDING, PAID, CANCELED | 

(*) All fields are required!

**Response Messages:**

| code  | Message                                                        | 
| :---: | -------------------------------------------------------------- | 
| 201   | Bankslip created                                               | 
| 400   | Bankslip not provided in the request body                      | 
| 422   | Invalid bankslip provided. Check HEADERS for more information! | 

**Response Header Messages:** 

| code  | name          | value                                                      |
| :---: | ------------- | ---------------------------------------------------------- |
| 201   | location      | /rest/bankslips/CREATED_BANK_SLIP_ID                       |
| 422   | dueDate       | Can't be null or empty!/Date format accepted: (yyyy-MM-dd) |
| 422   | totalInCents  | Can't be null or below zero!                               |
| 422   | customer      | Can't be null or empty!                                    |
| 422   | status        | Can't be null!                                             |
| 500   | iserror       | Could not create new bank slip: CAUSE_MESSAGE"             |

### List Bank Slip Endpoint

This endpoint will list all available bank slips. 

**Endpoint:** *GET* http://localhost:8080/rest/bankslips

**Format:**

This is a sample of a response (code 200) from this endpoint: 
```
[
   {
      "id" : "a22c71fb-c452-4a8a-8b30-c95f84ab0a0c",
      "due_date" : "2018-01-01" ,
      "total_in_cents" : "100000" ,
      "customer" : "ACME Company" ,
   },
   {
      "id" : "deb98857-2012-4ec4-bbe4-2db73f10f2f2",
      "due_date" : "2018-01-01" ,
      "total_in_cents" : "100000" ,
      "customer" : "EMCA Company" ,
   }
]   
```
     
**Response Messages:**

| code  | Message            | 
| :---: | ------------------ | 
| 200   | LIST_OF_BANK_SLIPS | 
| 400   | No bank slip found | 

**Response Header Messages:** 
   
| code  | name          | value                                        |
| :---: | ------------- | -------------------------------------------- |
| 500   | iserror       | Could not find all bank slips: CAUSE_MESSAGE |

### Detail Bank Slip Endpoint

This endpoint will find one bank slip defined by its ID and will calculate 
a fine for it, in case of delay.

**Business Rule:**
* Up to 10 days of delay: 0.5% of fine (Simple Interest)
* Beyond 10 days of delay: 1% of fine (Simple Interest)

**Endpoint:** *GET* http://localhost:8080/rest/bankslips/{id}

**Format:**

This is a sample of a response (code 200) from this endpoint: 
```
{
   "id" : "a22c71fb-c452-4a8a-8b30-c95f84ab0a0c",
   "due_date" : "2018-01-01" ,
   "total_in_cents" : "100000" ,
   "customer" : "ACME Company" ,
   "fine" : "1000" ,
   "status" : "PENDING"
}  
```
     
**Response Messages:**

| code  | Message                                       | 
| :---: | --------------------------------------------- | 
| 200   | DETAILED_BANK_SLIP                            | 
| 400   | Invalid id provided - it must be a valid UUID | 
| 404   | Bankslip not found with the specified id      | 

**Response Header Messages:** 

| code  | name          | value                                        |
| :---: | ------------- | -------------------------------------------- |
| 500   | iserror       | Could not find all bank slips: CAUSE_MESSAGE |

### Pay a Bank Slip Endpoint

This endpoint will pay a bank slip defined by an ID.

**Endpoint:** *PUT* http://localhost:8080/rest/bankslips/{id}

**Format:**

This is a sample of a request body used in endpoint: 
```
{
   "status" : "PAID"
}  
```
     
**Response Messages:**

| code  | Message                                    | 
| :---: | ------------------------------------------ | 
| 200   | Bankslip paid                              | 
| 404   | Bankslip not found with the specified id   | 
| 422   | Invalid bankslip status provided (PENDING) | 

**Response Header Messages:** 

| code  | name    | value                                        |
| :---: | ------- | -------------------------------------------- |
| 500   | iserror | Could not pay or cancel this particular bank slip: CAUSE_MESSAGE |

### Cancel a Bank Slip Endpoint

This endpoint will cancel a bank slip defined by an ID.

**Endpoint:** *PUT* http://localhost:8080/rest/bankslips/{id}

**Format:**

This is a sample of a request body used in endpoint: 
```
{
   "status" : "CANCELED"
}  
```
     
**Response Messages:**

| code  | Message                                    | 
| :---: | ------------------------------------------ | 
| 200   | Bankslip canceled                          | 
| 404   | Bankslip not found with the specified id   | 
| 422   | Invalid bankslip status provided (PENDING) | 

**Response Header Messages:** 

| code  | name    | value                                        |
| :---: | --------| -------------------------------------------- |
| 500   | iserror | Could not pay or cancel this particular bank slip: CAUSE_MESSAGE |

## Development Environment

### Software Requirements

- GIT 2.13.1.windows.2 or later;
- JDK 1.8 or later;
- Maven 3 or later;
- MongoBD 3.6.5 or later.

### Software Libraries
- spring-boot-starter-data-mongodb;
- spring-boot-starter-web (Spring Boot, Core, MVC, Log, Tomcat);
- spring-boot-starter-test (Hamcrest, JUnit, Mockito);
- lombok;
- de.flapdoodle.embed.mongo (Embedded MongoDB);
- springfox-swagger2;
- springfox-swagger-ui.

### Getting Started

1. Ensure that GIT, JDK, Maven and MongoDB are installed and working properly.
2. Ensure that MongoBD is up and running.
3. Open a command console and execute MongoDB Shell, by typing.

   > mongo.exe

4. Switch to `bankslipdb` MongoDB database, by typing: 

   > use bankslipdb;

5. Create `bankslipdb` user, by typing:
 
   > db.createUser({user: "bankslip", pwd: "bankslip2018", roles: []});

6. `Successfully added user: { "user" : "bankslip", "roles" : [ ] }` message 
   should appear.
7. Open a command console and change directory to one of your preference. 
8. Get a version of this project by typing:
   
   > git clone https://github.com/acmattos/bankslip.git

9. After clone action is done, change to `bankslip` directory.
10. Build a version of `Bankslip Application` by typing:

   > mvn clean compile install 

11. After `mvn` command is done, change to `target` directory.
12. Run application by typing:

   > java -jar bankslip-1.0.0.jar

   - This application will listen to 8080 port, so ensure that this port is 
     free, otherwise the application will not start.
   - If the port is **busy**, you will be able to change it. Just open
     `<PROJECT_ROOT_PATH>\src\main\resources\application.properties` 
     and change the following property:
   
     > server.port=<TYPE_NEW_PORT_HERE>
   
   - **OBS**: Keep in mind that this action requires a new artifact 
     construction (change directory to `<PROJECT_ROOT_PATH>` and follow *step
     10* instructions).
13. Open a browser window and type:

   > http://localhost:8080/rest/swagger-ui.html

14. This action will start Swagger UI application. This application allows you
    to manipulate BankSlip API easily. Swagger UI interface is self-explained
    and does not require anything else for you to explore BankSlip API
    Endpoints. 
15. At this point you've accomplished all the following steps: 
    - Obtaining BankSlip API code from GitHub;
    - Preparing MongoDB to support BankSlip API Application;
    - Compiling\Testing\Building BankSlip API Application;
    - Running BankSlip API Application;
    - Lauching Swagger UI Application;
    - Evaluating BankSlip API Application using Swagger UI.
    
### Architecture
     
The application was built on top of Spring Boot Framework. Designed as a REST 
API application, a WEB module was the best choice to accomodate  
`@RestController`s and all HTTP features through an embedded Tomcat Container.

The database chosen to persist `BankSlip API` data was `MongoDB`: a perfect 
choice to accomodate Bank Slip Documents that comes back and forth as JSON 
objects.

REST API documentation is done trough annotations of Swagger API. Swagger API 
provides tools that helps you to document REST APIs along with your code, 
making it easy to keep API documentation always updated. Swagger also provides 
an UI (Swagger UI): a powerful tool that allows you to see API's documentation
and testing it in a very easy way.

Lombok comes in place to reduce code writing. Getters, Setters, ToString methods
are handled by this fancy library, cleaning out your code and freeing you from 
writing tedious methods.

BankSlip API is fully tested. JUnit automates both Unit and Integration tests. 
Mockito helps you to mock complex object's behaviour, allowing you to cover many
flows of your code design. Hamcrest provides matchers that can be combined to 
create flexible expressions of intent. Embedded MongoDB provides a platform 
neutral way for running a MongoDB instance during Integration Tests.

BankSlip API is heavily tested using unit tests because they are lighter than 
integration tests (IT). IT's are left to:
- Test UUID assignment, on bank slip's document creation
- Test endpoint's point-to-point communication (request received from client, 
  request processed, database manipulation, response produced to client) 
- Test application's state during P2P communication.
 
### API Packages

This is a brief description of BankSlip API packages.

#### br.com.acmattos.bankslip

Application's root package. 

`BankSlipApplication`: bootstraps BankSlip API.

#### br.com.acmattos.bankslip.config

Contains the configuration of Swagger UI application.
 
`SwaggerConfig`: configures Swagger in order to generate UI application 
properly.

#### br.com.acmattos.bankslip.data

Contains all artifacts responsible to manipulate MongoDB Documents from and to 
database.

`BankSlip`: Entity that corresponds to a MongoBD bank slip document.

`BankSlipRepository`: Repository interface used to manipulate database 
documents.

`BankSlipStatusEnum`: Defines possible values of a BankSlip status. 

`BankSlipUUIDGeneratorEventListener`: Listen to `BeforeConvertEvent` events, 
detecting if some entity needs to get a UUID.

#### br.com.acmattos.bankslip.rest

Contains all artifacts responsible for dealing with HTTP communication. They can
be devided into two groups: Data Transfer Objects (carry data from API and to 
API); Communication Processors (get requests, processing them and generating 
appropriate responses).

`AbstractBankSlipDTO`: Base class for BankSlip data.

`DetailedBankSlipDTO`: Holds detailed BankSlip stored data (including, fine if 
applied) returned from API after a *GET* request together with UUID.

`NewBankSlipDTO`: Holds new BankSlip data sent to API for database storing after
a *POST* request.

`SavedBankSlipDTO`: Holds short version of BankSlip stored data returned from 
API after a *GET* request.

`UpdatedBankSlipStatusDTO`: Holds BankSlip Status sent to API for pay or cancel
a specific BankSlip (defined by its UUID) after a *PUT* request.

`BankSlipController`: Exposes BankSlip APIs endpoints and processes all 
requests.

`BankSlipExceptionHandlerAdvice`: Detects some exceptional conditions and 
generates responses.

`ResponseEntityBuilder`: Helper class that aids `BankSlipController` and 
`BankSlipExceptionHandlerAdvice` to prepare the response to be sent to client.
