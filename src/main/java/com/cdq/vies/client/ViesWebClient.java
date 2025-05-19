package com.cdq.vies.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.cdq.vies.client.dto.CheckVatRequest;
import com.cdq.vies.client.dto.CheckVatResponse;
import com.cdq.vies.client.dto.CommonResponse;
import com.cdq.vies.client.dto.ErrorWrapper;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ViesWebClient
{
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final WebClient viesClient;

    @Getter
    private long totalCounter = 0L;
    @Getter
    private long successCounter = 0L;
    @Getter
    private long failuresCounter = 0L;

    public Mono<CheckVatResponse> checkVat(Mono<CheckVatRequest> checkVatRequest)
    {
        totalCounter = totalCounter + 1;
        long currentSuccessCounter = successCounter;
        long currentFailedCounter = failuresCounter;

        return viesClient.post()
                .uri("/taxation_customs/vies/rest-api/check-vat-number")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(checkVatRequest, CheckVatResponse.class)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(CommonResponse.class)
                                .map(CommonResponseException::new))
                .bodyToMono(CheckVatResponse.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorMap(
                        TimeoutException.class,
                        e -> new CommonResponseException(
                                new CommonResponse(
                                        false,
                                        null,
                                        List.of(new ErrorWrapper("TIMEOUT", "Operation timed out after " + DEFAULT_TIMEOUT.toSeconds() + " seconds")))))
                .onErrorMap(
                        WebClientRequestException.class,
                        e -> new CommonResponseException(
                                new CommonResponse(
                                        false,
                                        null,
                                        List.of(new ErrorWrapper("REQUEST_ERROR", e.getMessage())))))
                .doOnError(e -> failuresCounter = currentFailedCounter + 1)
                .doOnSuccess(response -> successCounter = currentSuccessCounter + 1);
    }
}
