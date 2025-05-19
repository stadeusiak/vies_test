package com.cdq.vies;

import org.springframework.stereotype.Service;

import com.cdq.vies.client.ViesWebClient;
import com.cdq.vies.client.dto.CheckVatRequest;
import com.cdq.vies.client.dto.CommonResponse;
import com.cdq.vies.client.dto.ExecutionStats;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ViesService
{
    private final ViesWebClient viesWebClient;

    public Mono<CommonResponse> checkVat(Mono<CheckVatRequest> checkVatRequest)
    {
        return viesWebClient.checkVat(checkVatRequest)
                .map(c -> new CommonResponse(true, c, null));
    }

    public Mono<ExecutionStats> getExecutionStats()
    {
        return Mono.just(new ExecutionStats(viesWebClient.getTotalCounter(), viesWebClient.getSuccessCounter(), viesWebClient.getFailuresCounter()));
    }
}
