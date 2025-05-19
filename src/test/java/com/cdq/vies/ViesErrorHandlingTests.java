package com.cdq.vies;

import com.cdq.vies.client.dto.CheckVatRequest;
import com.cdq.vies.client.dto.CommonResponse;
import com.cdq.vies.client.dto.ErrorWrapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import config.CreateRequest;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import utils.VatRequestFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DisplayName("Obsługa Błędów")
class ViesErrorHandlingTests {
    private static final int MOCK_PORT = 8089;
    private static WireMockServer wireMockServer;
    private static final String BASE_URL = "http://localhost:" + MOCK_PORT;
    private static final String BLOCKED_VAT_MESSAGE = "The VAT number is blocked due to a specific filter on this number.";
    private static final String BLOCKED_VAT_NUMBER = "9999999999";
    private static final String BLOCKED_IP_NUMBER = "123";
    private static final String BLOCKED_IP_MESSAGE = "Your IP address has been rejected due to a filter.";
    private static final String MS_UNAVAILABLE_MESSAGE = "The request has been processed but the Member State service to validate the request is unavailable. Please retry later on";

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(
                new WireMockConfiguration()
                        .port(MOCK_PORT)
                        .extensions(new ResponseTemplateTransformer(true))
        );
        wireMockServer.start();
        configureFor("localhost", MOCK_PORT);
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    @Test
    void shouldReturnVatBlockedMessageWithCommonResponse() {
        wireMockServer.resetAll();
        stubForBlockedVatResponse();
        CheckVatRequest request = VatRequestFactory.buildCheckVatRequestDto(
                "PL",
                BLOCKED_VAT_NUMBER,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Response rawResponse = CreateRequest.sendPostRequest(BASE_URL, "/checkVat", request);
        int statusCode = rawResponse.getStatusCode();
        CommonResponse response = rawResponse.as(CommonResponse.class);

        var errorMessage = response.errorWrappers().stream()
                .map(ErrorWrapper::message)
                .toList().toString();

        assertThat(statusCode).isEqualTo(200);
        Assertions.assertThat(errorMessage).contains(BLOCKED_VAT_MESSAGE);
    }

    @Test
    void shouldReturnIpBlockedMessageWithCommonResponse() {
        wireMockServer.resetAll();
        stubForBlockedIPResponse();
        CheckVatRequest request = VatRequestFactory.buildCheckVatRequestDto(
                "PL",
                "8992790965",
                null,
                BLOCKED_IP_NUMBER,
                null,
                null,
                null,
                null,
                null
        );

        Response rawResponse = CreateRequest.sendPostRequest(BASE_URL, "/checkVat", request);
        int statusCode = rawResponse.getStatusCode();
        CommonResponse response = rawResponse.as(CommonResponse.class);

        var errorMessage = response.errorWrappers().stream()
                .map(ErrorWrapper::message)
                .toList().toString();

        assertThat(statusCode).isEqualTo(200);
        Assertions.assertThat(errorMessage).contains(BLOCKED_IP_MESSAGE);
    }

    @Test
    void shouldReturnUnavailableWithCommonResponse() {
        wireMockServer.resetAll();
        stubForMemberStateResponse();
        CheckVatRequest request = VatRequestFactory.buildCheckVatRequestDto(
                "DE",
                "456777999",
                "AT",
                "567777",
                null,
                null,
                null,
                null,
                null
        );

        Response rawResponse = CreateRequest.sendPostRequest(BASE_URL, "/checkVat", request);
        int statusCode = rawResponse.getStatusCode();
        CommonResponse response = rawResponse.as(CommonResponse.class);

        var errorMessage = response.errorWrappers().stream()
                .map(ErrorWrapper::message)
                .toList().toString();

        assertThat(statusCode).isEqualTo(200);
        Assertions.assertThat(errorMessage).contains(MS_UNAVAILABLE_MESSAGE);
    }

    private static void stubForBlockedVatResponse() {
        stubFor(post(urlEqualTo("/checkVat"))
                .withRequestBody(containing("\"vatNumber\":\"9999999999\""))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("vat_blocked_response.json")
                        .withTransformers("response-template")));
    }

    private static void stubForBlockedIPResponse() {
        stubFor(post(urlEqualTo("/checkVat"))
                .withRequestBody(containing("\"requesterNumber\":\"123\""))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("ip_blocked_response.json")
                        .withTransformers("response-template")));
    }

    private static void stubForMemberStateResponse() {
        stubFor(post(urlEqualTo("/checkVat"))
                .withRequestBody(containing("\"requesterMemberStateCode\":\"AT\""))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("ms_unavailable_response.json")
                        .withTransformers("response-template")));
    }
}
