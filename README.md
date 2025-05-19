# Java Automation Developer at CDQ

> ###### Prerequisites:
> * Docker
> * JDK 17+

## How to build application:

```bash
./gradlew build
```

## How to run application:

```bash
./gradlew bootRun
```

## How to access swagger

Swagger UI can be accessed via browser by url http://localhost:8080/swagger-ui/index.html

## How to build docker image

```bash
./gradlew bootBuildImage
```

it will create image `cdq.demo.vies` which can be run by

```bash
docker run -p 8080:8080 cdq.demo.vies   
````

## Product requirements

In the system there is a need to count the number of request to [VIES]([https://ec.europa.eu/taxation_customs/vies/#/technical-information). The system should count all requests which succeed. A successful response is understood as a response from VIES with status code 200, 400 or 500. 

A failed response is understood as following:

* our service is not able to connect to VIES. e.g. there is no internet connection 
* VIES returns a response in more than 10s

The VIES wrapper should always return a 200 status code. In the response body should be the reason of the failure.

The api is using the REST api of VIES. Usefully links are here:

- [Technical documentation](https://taxation-customs.ec.europa.eu/document/2e78eb36-537f-46a8-b6b6-393365254869_en)
- [Technial Information](https://ec.europa.eu/taxation_customs/vies/#/technical-information) 

## About the api

This api is a wrapper around [VIES VAT number validation service](https://europa.eu/youreurope/business/taxation/vat/check-vat-number-vies/index_pl.htm). The goal of this wrapper is to count the number of successful request to VIES.

To count the number of request use the GET /usage endpoint.

Example request are in [ViesWebService.http](./src/main/resources/ViesWebService.http) file

## Task

The goal of this task is to write a test strategy and then implement end-to-end test according to the test strategy for the above described api. 

The test strategy can be readme file which describes what is your idea how to test the api. 

The second part of the task is to implement the tests and execute them. The test execution should be one command which executed all tests.

If there are some bugs please create a bug report. 

Feel free to use any tools or libraries you want.