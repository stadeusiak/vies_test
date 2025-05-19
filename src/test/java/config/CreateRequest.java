package config;

import com.cdq.vies.client.dto.CheckVatRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CreateRequest {
    public static Response sendGetRequestParam(String baseUrl, String endpoint) {

        return given()
                .spec(BaseRequest.requestSpecQueryParam())
                .when()
                .get(baseUrl+endpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    public static Response sendPostRequest(String baseUrl, String endpoint, CheckVatRequest body) {

        return given()
                .spec(BaseRequest.requestSpecQueryParam())
                .when()
                .body(body)
                .post(baseUrl+endpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    public static Response sendPostRequestStringBody(String baseUrl, String endpoint, String body) {

        return given()
                .spec(BaseRequest.requestSpecQueryParam())
                .when()
                .body(body)
                .post(baseUrl+endpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }

}
