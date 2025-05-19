package com.cdq.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.cdq.api.v1.model.CheckUsageResponse;
import com.cdq.api.v1.model.CheckVatRequest;
import com.cdq.api.v1.model.CommonResponse;
import com.cdq.vies.ViesService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ViesWebService implements ViesGatewayApi
{

    private final ViesService viesService;
    private final ApiEntityMapper mapper;

    @Override
    public Mono<ResponseEntity<CommonResponse>> checkVatNumber(Mono<CheckVatRequest> body, ServerWebExchange exchange)
    {
        return viesService.checkVat(body.map(mapper::toModel))
                .map(mapper::toApi)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<CheckUsageResponse>> usage(ServerWebExchange exchange)
    {
        return viesService.getExecutionStats()
                .map(mapper::toApi)
                .map(ResponseEntity::ok);
    }
}
