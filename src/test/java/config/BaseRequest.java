package config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseRequest {
    public static RequestSpecification requestSpecQueryParam() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setAccept(ContentType.JSON);
        requestSpecBuilder.setContentType(ContentType.JSON);
        requestSpecBuilder.setUrlEncodingEnabled(true);
        requestSpecBuilder.addFilter(new AllureRestAssured());
        requestSpecBuilder.addFilter(new RequestLoggingFilter());
        requestSpecBuilder.addFilter(new ResponseLoggingFilter());
        return requestSpecBuilder.build();
    }
}
