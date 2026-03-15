package client;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
import models.request.SignUpLoginRequest;
import util.Configuration;

public class EVPrimeClient {

    public Response signUp(SignUpLoginRequest requestBody) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when().log().all()
                .body(requestBody)
                .post(Configuration.SIGN_UP)
                .thenReturn();
    }

    public Response login(SignUpLoginRequest requestBody) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when().log().all()
                .body(requestBody)
                .post(Configuration.LOGIN)
                .thenReturn();
    }

    public Response getAllEvents() {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when().log().all()
                .get(Configuration.EVENTS)
                .thenReturn();
    }

    public Response getSingleEvent(String id) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when().log().all()
                .get(Configuration.EVENTS + "/" + id)
                .thenReturn();
    }

    public Response postEvent(PostUpdateEventRequest requestBody, String token) {
        return RestAssured
                .given()
                .header("Authorization", "bearer " + token)
                .contentType(ContentType.JSON)
                .when().log().all()
                .body(requestBody)
                .post(Configuration.EVENTS)
                .thenReturn();
    }

    public Response updateEvent(PostUpdateEventRequest requestBody, String token, String id) {
        return RestAssured
                .given()
                .header("Authorization", "bearer " + token)
                .contentType(ContentType.JSON)
                .when().log().all()
                .body(requestBody)
                .put(Configuration.EVENTS + "/" + id)
                .thenReturn();
    }

    public Response deleteEvent(String id, String token) {
        return RestAssured
                .given()
                .header("Authorization", "bearer " + token)
                .contentType(ContentType.JSON)
                .when().log().all()
                .delete(Configuration.EVENTS + "/" + id)
                .thenReturn();
    }

}
