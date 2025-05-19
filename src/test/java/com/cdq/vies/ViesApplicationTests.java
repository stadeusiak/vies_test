package com.cdq.vies;

import com.cdq.vies.client.dto.CheckVatRequest;
import com.cdq.vies.client.dto.CommonResponse;
import com.cdq.vies.client.dto.ErrorWrapper;
import com.cdq.vies.client.dto.ExecutionStats;
import com.fasterxml.jackson.core.JsonProcessingException;
import config.BaseTest;
import config.CreateRequest;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import utils.VatRequestFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ViesApplicationTests extends BaseTest {
    public static final String MISSING_FIELD_MESSAGE = "A mandatory field is missing";
    public static final String MISSING_FIELD_ERROR = "VOW-ERR-11";
    public static final String INVALID_INPUT_MESSAGE = "Some data are invalid in the reqeuest";
    public static final String INVALID_INPUT_ERROR = "INVALID_INPUT";
    public static final String INVALID_VAT_MESSAGE = "The Vat number does not respect the configured pattern";
    public static final String INVALID_VAT_ERROR = "VOW-ERR-2600";
    public static final String INVALID_MEMBER_CODE_MESSAGE = "The requester member state code is invalid.";
    public static final String INVALID_MEMBER_CODE_ERROR = "VOW-ERR-2500";
    public static final String INVALID_REQUESTER_MESSAGE = "The requester VAT number does not respect the configured pattern";
    public static final String INVALID_REQUESTER_ERROR = "VOW-ERR-2700";

    @Nested
    @DisplayName("Testy Pozytywne")
    class PositiveCases {

        @ParameterizedTest
        @MethodSource("provideAllCountryCodesWithVat")
        void shouldValidateEuropeanVatNumber(String countryCode, String vatNumber) {
            // GIVEN
            CheckVatRequest request = VatRequestFactory.buildCheckVatRequestDto(
                    countryCode,
                    vatNumber,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            // WHEN
            Response rawResponse = CreateRequest.sendPostRequest(BASE_URL, CHECKVAT_ENDPOINT, request);
            int statusCode = rawResponse.getStatusCode();
            CommonResponse response = rawResponse.as(CommonResponse.class);
            String requestDate = String.valueOf(response.viesResponse().requestDate());

            // THEN
            assertThat(statusCode).isEqualTo(200);
            assertThat(requestDate).contains(TODAY_DATE);
            assertThat(response.actionSucceed()).isTrue();
            assertThat(response.viesResponse().valid()).isTrue();
        }

        static Stream<Arguments> provideAllCountryCodesWithVat() {
            return Stream.of(
                    Arguments.of("PL", "8992790965"),
                    Arguments.of("FR", "40303265045"),
                    Arguments.of("IT", "00743110157"),
                    Arguments.of("ES", "A08017535"),
                    Arguments.of("NL", "004495445B01"),
                    Arguments.of("IE", "6388047V")
            );
        }

        @Test
        void shouldReturnValidResponseWithMinimalRequest() throws JsonProcessingException {
            //GIVEN
            String request = VatRequestFactory.buildJsonExcludingNulls(
                    "PL",
                    "8992790965",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            //WHEN
            Response rawResponse = CreateRequest.sendPostRequestStringBody(BASE_URL, CHECKVAT_ENDPOINT, request);
            int statusCode = rawResponse.getStatusCode();
            CommonResponse response = rawResponse.as(CommonResponse.class);

            //THEN
            assertThat(statusCode).isEqualTo(200);
            assertThat(response.actionSucceed()).isTrue();
            assertThat(response.viesResponse().valid()).isTrue();
        }

        @Test
        void validVatWithRequester() throws JsonProcessingException {
            //GIVEN
            String request = VatRequestFactory.buildJsonExcludingNulls(
                    "PL",
                    "8992790965",
                    "PL",
                    "8992790965",
                    null,
                    null,
                    null,
                    null,
                    null
            );

            //WHEN
            Response rawResponse = CreateRequest.sendPostRequestStringBody(BASE_URL, CHECKVAT_ENDPOINT, request);
            int statusCode = rawResponse.getStatusCode();
            CommonResponse response = rawResponse.as(CommonResponse.class);

            //THEN
            assertThat(statusCode).isEqualTo(200);
            assertThat(response.actionSucceed()).isTrue();
            assertThat(response.viesResponse().valid()).isTrue();
        }

        @Test
        void shouldReturnCorrectMappingWithTraderFields() {
            // GIVEN
            CheckVatRequest request = VatRequestFactory.buildCheckVatRequestDto(
                    "ES",
                    "A28240752",
                    "DE",
                    "307699628",
                    "ATOS SPAIN SA",
                    "Calle Albarracin",
                    "28037",
                    "Madrid",
                    "---"
            );

            // WHEN
            Response rawResponse = CreateRequest.sendPostRequest(BASE_URL, CHECKVAT_ENDPOINT, request);
            CommonResponse response = rawResponse.as(CommonResponse.class);

            // THEN
            assertThat(rawResponse.statusCode()).isEqualTo(200);
            assertThat(response.actionSucceed()).isTrue();
            assertThat(response.viesResponse().valid()).isTrue();
            assertThat(response.viesResponse().traderCityMatch()).isEqualTo("VALID");
            assertThat(response.viesResponse().traderStreetMatch()).isEqualTo("VALID");
            assertThat(response.viesResponse().traderNameMatch()).isEqualTo("VALID");
            assertThat(response.viesResponse().traderPostalCodeMatch()).isEqualTo("VALID");
        }
    }

    @Nested
    @DisplayName("Testy Walidacji")
    class NegativeCases {

        @ParameterizedTest
        @MethodSource("shouldReturnCorrectErrorMessageWhenFieldsAreMissingSource")
        void shouldReturnCorrectErrorMessageWhenRequiredFieldsAreMissing(String countryCode,
                                                                         String VatNumber) throws JsonProcessingException {
            //GIVEN
            String request = VatRequestFactory.buildJsonExcludingNulls(
                    countryCode,
                    VatNumber,
                    "PL",
                    "8992790965",
                    null,
                    null,
                    null,
                    null,
                    null
            );

            //WHEN
            Response rawResponse = CreateRequest.sendPostRequestStringBody(BASE_URL, CHECKVAT_ENDPOINT, request);
            int statusCode = rawResponse.getStatusCode();
            CommonResponse response = rawResponse.as(CommonResponse.class);
            List<String> errorMessage = extractErrorMessages(response);
            List<String> errorType = extractErrorCodes(response);

            //THEN
            assertThat(statusCode).isEqualTo(200);
            assertThat(response.actionSucceed()).isFalse();
            assertThat(response.viesResponse()).isNull();
            assertThat(errorMessage).contains(MISSING_FIELD_MESSAGE);
            assertThat(errorType).contains(MISSING_FIELD_ERROR);
        }

        static Stream<Arguments> shouldReturnCorrectErrorMessageWhenFieldsAreMissingSource() {
            return Stream.of(
                    Arguments.of("", "8992790965"),
                    Arguments.of("PL", ""),
                    Arguments.of("", ""),
                    Arguments.of(null, ""),
                    Arguments.of("PL", null),
                    Arguments.of(null, null)
            );
        }

        @ParameterizedTest
        @MethodSource("shouldReturnCorrectResponseWhenInvalidRequestFormatSource")
        void shouldReturnCorrectResponseWhenInvalidFieldInput(String countryCode,
                                                              String VatNumber,
                                                              String requesterMemberStateCode,
                                                              String requesterNumber,
                                                              String expectedError,
                                                              String expectedErrorMessage) {
            //GIVEN
            SoftAssertions softly = new SoftAssertions();
            CheckVatRequest request = VatRequestFactory.buildCheckVatRequestDto(
                    countryCode,
                    VatNumber,
                    requesterMemberStateCode,
                    requesterNumber,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            //WHEN
            Response rawResponse = CreateRequest.sendPostRequest(BASE_URL, CHECKVAT_ENDPOINT, request);
            int statusCode = rawResponse.getStatusCode();
            CommonResponse response = rawResponse.as(CommonResponse.class);
            assertThat(response.actionSucceed())
                    .as("Response should not be successful, but was: " + response.actionSucceed())
                    .isFalse();
            List<String> errorMessage = extractErrorMessages(response);
            List<String> errorType = extractErrorCodes(response);

            //THEN
            assertThat(statusCode).isEqualTo(200);
            softly.assertThat(response.viesResponse()).isNull();
            assertThat(errorMessage).contains(expectedError);
            assertThat(errorType).contains(expectedErrorMessage);
        }


        static Stream<Arguments> shouldReturnCorrectResponseWhenInvalidRequestFormatSource() {
            return Stream.of(
                    Arguments.of("PL".repeat(130), "8992790965", "", "", INVALID_INPUT_MESSAGE, INVALID_INPUT_ERROR),
                    Arguments.of("PL", "8992790965".repeat(30), "", "", INVALID_INPUT_MESSAGE, INVALID_INPUT_ERROR),
                    Arguments.of("PL", "ABC-8992790965", "", "", INVALID_VAT_MESSAGE, INVALID_VAT_ERROR),
                    Arguments.of("PL", "  8992790965", "", "", INVALID_VAT_MESSAGE, INVALID_VAT_ERROR),
                    Arguments.of("PL", "8992790965  ", "", "", INVALID_VAT_MESSAGE, INVALID_VAT_ERROR),
                    Arguments.of("PL", "_8992790965  ", "", "", INVALID_VAT_MESSAGE, INVALID_VAT_ERROR),
                    Arguments.of("PL", "123", "", "", INVALID_VAT_MESSAGE, INVALID_VAT_ERROR),
                    Arguments.of("PL", "ABC-8992790965", "", "", INVALID_VAT_MESSAGE, INVALID_VAT_ERROR),
                    Arguments.of("PL", "ABC-8992790965", "", "", INVALID_VAT_MESSAGE, INVALID_VAT_ERROR),
                    Arguments.of("12", "8992790965", "", "", INVALID_INPUT_MESSAGE, INVALID_INPUT_ERROR),
                    Arguments.of("A1", "8992790965", "", "", INVALID_INPUT_MESSAGE, INVALID_INPUT_ERROR),
                    Arguments.of("PL", "8992790965", "1234", "8992790965", INVALID_MEMBER_CODE_MESSAGE, INVALID_MEMBER_CODE_ERROR),
                    Arguments.of("PL", "value", "PLL", "8992790965", INVALID_MEMBER_CODE_MESSAGE, INVALID_MEMBER_CODE_ERROR),
                    Arguments.of("PL", "value", "P", "8992790965", INVALID_MEMBER_CODE_MESSAGE, INVALID_MEMBER_CODE_ERROR),
                    Arguments.of("PL", "value", "P1", "8992790965", INVALID_MEMBER_CODE_MESSAGE, INVALID_MEMBER_CODE_ERROR),
                    Arguments.of("PL", "value", "P-", "8992790965", INVALID_MEMBER_CODE_MESSAGE, INVALID_MEMBER_CODE_ERROR),
                    Arguments.of("PL", "value", "PL ", "8992790965", INVALID_MEMBER_CODE_MESSAGE, INVALID_MEMBER_CODE_ERROR),
                    Arguments.of("PL", "value", " PL", "8992790965", INVALID_MEMBER_CODE_MESSAGE, INVALID_MEMBER_CODE_ERROR),
                    Arguments.of("PL", "value", " PL ", "8992790965", INVALID_MEMBER_CODE_MESSAGE, INVALID_MEMBER_CODE_ERROR),
                    Arguments.of("PL", "value", "PL", "abc", INVALID_REQUESTER_MESSAGE, INVALID_REQUESTER_ERROR),
                    Arguments.of("PL", "value", "PL", "8992abc123", INVALID_REQUESTER_MESSAGE, INVALID_REQUESTER_ERROR),
                    Arguments.of("PL", "value", "PL", " 8992790965", INVALID_REQUESTER_MESSAGE, INVALID_REQUESTER_ERROR),
                    Arguments.of("PL", "value", "PL", " 8992790965", INVALID_REQUESTER_MESSAGE, INVALID_REQUESTER_ERROR)
            );
        }

        @Test
        void nonexistentVatNumber() {
            //GIVEN
            CheckVatRequest request = VatRequestFactory.buildCheckVatRequestDto(
                    "PL",
                    "0000000000",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            //WHEN
            Response rawResponse = CreateRequest.sendPostRequest(BASE_URL, CHECKVAT_ENDPOINT, request);
            int statusCode = rawResponse.getStatusCode();
            CommonResponse response = rawResponse.as(CommonResponse.class);

            //THEN
            assertThat(statusCode).isEqualTo(200);
            assertThat(response.actionSucceed()).isTrue();
            assertThat(response.viesResponse().valid()).isFalse();
        }


        @Test
        void shouldGetResponseWithCountedStats() {
            // WHEN
            Response rawResponse = CreateRequest.sendGetRequestParam(BASE_URL, GETVAT_ENDPOINT);
            ExecutionStats stats = rawResponse.as(ExecutionStats.class);

            // THEN
            assertThat(rawResponse.statusCode()).isEqualTo(200);
            assertThat(stats.total()).isEqualTo(30);
            assertThat(stats.success()).isEqualTo(8);
            assertThat(stats.failures()).isEqualTo(22);
        }
    }

    private List<String> extractErrorMessages(CommonResponse response) {
        if (response == null || response.errorWrappers() == null) {
            return List.of();
        }
        return response.errorWrappers().stream()
                .map(ErrorWrapper::message)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<String> extractErrorCodes(CommonResponse response) {
        if (response == null || response.errorWrappers() == null) {
            return List.of();
        }
        return response.errorWrappers().stream()
                .map(ErrorWrapper::error)
                .filter(Objects::nonNull)
                .toList();
    }
}
