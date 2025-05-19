package com.cdq.vies;

import com.cdq.vies.client.dto.CommonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import config.BaseTest;
import config.CreateRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.VatRequestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ViesApplicationLoadTests extends BaseTest {

    @Test
    @Disabled
    void shouldReturnTimeoutError() throws JsonProcessingException {
        //GIVEN
        int numberOfRequests = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(1000);
        List<Future<Response>> responses = new ArrayList<>();

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
        for (int i = 0; i < numberOfRequests; i++) {
            responses.add(executor.submit(() ->
                    CreateRequest.sendPostRequestStringBody(BASE_URL, CHECKVAT_ENDPOINT, request)
            ));
        }

        //THEN
        executor.shutdown();
        long timeoutResponses = getTimeoutErrors(responses);
        assertThat(timeoutResponses).isGreaterThan(0);
    }

    private static long getTimeoutErrors(List<Future<Response>> futures) {
        return futures.stream()
                .map(f -> {
                    try {
                        return f.get().as(CommonResponse.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(resp -> !resp.actionSucceed())
                .filter(resp -> resp.errorWrappers() != null &&
                        resp.errorWrappers().stream()
                                .anyMatch(e -> "TIMEOUT".equals(e.error()) &&
                                        e.message() != null &&
                                        e.message().contains("timed out")))
                .count();
    }
}
