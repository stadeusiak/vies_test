package com.cdq.vies.client;

import com.cdq.vies.client.dto.CommonResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonResponseException extends Exception
{

    private final CommonResponse commonResponse;

}
