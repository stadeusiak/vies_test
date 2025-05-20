# ✅ VIES API Automated Tests

A comprehensive suite of automated tests for validating the VIES (VAT Information Exchange System) API, built using Spring Boot, Testcontainers, and RestAssured.

## 📦 Project Overview

This project includes:

- **Positive tests** – validate VAT numbers across various EU countries.
- **Validation tests (negative)** – verify error handling for incomplete or invalid input data.
- **Load tests** – simulate high concurrency to trigger TIMEOUT errors.
- **Error simulation tests** – use WireMock to mock special error conditions like blocked VAT numbers or unavailable Member States.

## 🛠️ Technologies Used

- **Java 21**
- **Spring Boot**
- **JUnit 5**
- **RestAssured**
- **Testcontainers**
- **WireMock**
- **Allure Reports**
- **Gradle**
- **GitHub Actions**

##  Running Tests Locally

### Requirements

- Docker
- JDK 21
- Gradle (or `./gradlew` wrapper)

### Steps

```bash
./gradlew bootJar
docker build -t cdq.demo.vies .
./gradlew test -DTESTCONTAINERS_CHECKS_DISABLE=true
```

Tests automatically spin up the `cdq.demo.vies` container and run HTTP tests against port 8080.

##  Project Structure

```
├── config
│   ├── BaseTest.java               # Testcontainers setup
│   ├── BaseRequest.java           # RestAssured specification
├── utils
│   └── VatRequestFactory.java     # Request object generation
├── com.cdq.vies
│   ├── ViesApplicationTests.java  # Positive and validation tests
│   ├── ViesApplicationLoadTests.java # Load testing (timeouts)
│   ├── ViesErrorHandlingTests.java   # WireMock-based error tests
├── resources
│   └── __files/                   # WireMock JSON response stubs
```

## Sample Test

```java
@Test
void shouldValidateEuropeanVatNumber() {
    CheckVatRequest request = VatRequestFactory.buildCheckVatRequestDto("PL", "8992790965", null, null, ...);
    Response rawResponse = CreateRequest.sendPostRequest(BASE_URL, CHECKVAT_ENDPOINT, request);
    CommonResponse response = rawResponse.as(CommonResponse.class);

    assertThat(rawResponse.statusCode()).isEqualTo(200);
    assertThat(response.actionSucceed()).isTrue();
    assertThat(response.viesResponse().valid()).isTrue();
}
```

##  Testcontainers

All tests extend `BaseTest`, which:

- Builds the `cdq.demo.vies` Docker image
- Spins up the API container on port 8080
- Exposes the mapped container port via `BASE_URL`

##  GitHub Actions CI

The `.github/workflows/test.yml` file runs tests automatically:

- Builds the Spring Boot JAR and Docker image
- Executes Testcontainers-based tests
- Captures container logs on failure

## Allure Report (Optional)

To generate an Allure report locally:

```bash
./gradlew clean test
allure serve build/allure-results
```

## 🧹 Cleanup

All containers are automatically cleaned up by Testcontainers after test execution.

## ✍️ Author

Szymon Tadeusiak– Backend test automation for VIES API using modern CI/CD tools and containerization.

---