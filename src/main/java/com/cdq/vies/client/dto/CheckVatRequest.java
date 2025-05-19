package com.cdq.vies.client.dto;

public record CheckVatRequest(
    String countryCode,
    String vatNumber,
    String requesterMemberStateCode,
    String requesterNumber,
    String traderName,
    String traderStreet,
    String traderPostalCode,
    String traderCity,
    String traderCompanyType
) {}