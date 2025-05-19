package com.cdq.vies.client.dto;

import java.util.List;

public record CommonResponse(
        boolean actionSucceed,
        CheckVatResponse viesResponse,
        List<ErrorWrapper> errorWrappers
)
{
}