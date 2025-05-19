package com.cdq.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cdq.api.v1.model.CommonResponse;
import com.cdq.vies.client.CommonResponseException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionMapper
{

    private final ApiEntityMapper mapper;

    @ExceptionHandler(exception = { CommonResponseException.class })
    public Mono<ResponseEntity<CommonResponse>> map(CommonResponseException exception)
    {
        return Mono.just(exception)
                .map(CommonResponseException::getCommonResponse)
                .map(mapper::toApi)
                .map(cr -> ResponseEntity.status(HttpStatus.OK.value()).body(cr));
    }
}
