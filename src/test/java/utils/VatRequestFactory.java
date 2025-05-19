package utils;

import com.cdq.vies.client.dto.CheckVatRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VatRequestFactory {
    public static CheckVatRequest buildCheckVatRequestDto(
            String countryCode,
            String vatNumber,
            String requesterMemberStateCode,
            String requesterNumber,
            String traderName,
            String traderStreet,
            String traderPostalCode,
            String traderCity,
            String traderCompanyType
    ) {
        return new CheckVatRequest(
                countryCode,
                vatNumber,
                requesterMemberStateCode,
                requesterNumber,
                traderName,
                traderStreet,
                traderPostalCode,
                traderCity,
                traderCompanyType
        );
    }

    public static String buildJsonExcludingNulls(String countryCode,
                                                 String vatNumber,
                                                 String requesterMemberStateCode,
                                                 String requesterNumber,
                                                 String traderName,
                                                 String traderStreet,
                                                 String traderPostalCode,
                                                 String traderCity,
                                                 String traderCompanyType) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        CheckVatRequest request = new CheckVatRequest(
                countryCode,
                vatNumber,
                requesterMemberStateCode,
                requesterNumber,
                traderName,
                traderStreet,
                traderPostalCode,
                traderCity,
                traderCompanyType
        );
        return mapper.writeValueAsString(request);
    }
}
