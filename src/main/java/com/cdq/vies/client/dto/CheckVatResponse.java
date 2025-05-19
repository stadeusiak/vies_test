package com.cdq.vies.client.dto;

import java.time.OffsetDateTime;

public record CheckVatResponse(
        String countryCode,
        String vatNumber,
        OffsetDateTime requestDate,
        boolean valid,
        String requestIdentifier,
        String name,
        String address,
        String traderName,
        String traderStreet,
        String traderPostalCode,
        String traderCity,
        String traderCompanyType,
        String traderNameMatch,
        String traderStreetMatch,
        String traderPostalCodeMatch,
        String traderCityMatch,
        String traderCompanyTypeMatch
)
{
}