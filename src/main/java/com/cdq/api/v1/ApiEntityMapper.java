package com.cdq.api.v1;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ApiEntityMapper
{

    com.cdq.vies.client.dto.CheckVatRequest toModel(com.cdq.api.v1.model.CheckVatRequest source);

    com.cdq.api.v1.model.CheckVatResponse toApi(com.cdq.vies.client.dto.CheckVatResponse source);

    com.cdq.api.v1.model.CommonResponse toApi(com.cdq.vies.client.dto.CommonResponse source);

    com.cdq.api.v1.model.CheckUsageResponse toApi(com.cdq.vies.client.dto.ExecutionStats executionStats);
}
