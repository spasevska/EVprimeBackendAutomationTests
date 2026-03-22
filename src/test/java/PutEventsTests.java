import client.EVPrimeClient;
import data.PostEventDataFactory;
import data.SignUpLoginDataFactory;
import database.DBClient;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
import models.request.SignUpLoginRequest;
import models.response.LoginResponse;
import models.response.PostUpdateDeleteEventResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static objectBuilder.PostUpdateEventObjectBuilder.createBodyForPostEvent;
import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PutEventsTests {
    private SignUpLoginRequest loginRequest;
    private LoginResponse loginResponseBody;
    private PostUpdateEventRequest postEventRequest;
    private String id;
    private DBClient dbClient = new DBClient();

    @Before
    public void setUp() {
        loginRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail("evprime@gmail.com")
                .setPassword("password")
                .createRequest();

        Response loginResponse = new EVPrimeClient()
                .login(loginRequest);

        loginResponseBody = loginResponse.body().as(LoginResponse.class);

        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime")
                .setImage("https://images.pexels.com/photos/35908536/pexels-photo-35908536.jpeg")
                .setDate("2026-01-25")
                .setLocation("Tokio")
                .setDescription("Nature")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse postResponse = response.body().as(PostUpdateDeleteEventResponse.class);
        id = postResponse.getMessage().substring(39);
    }

    @Test
    public void updateEvent() {
        postEventRequest.setTitle("EVprime updated");

        Response responseUpdate = new EVPrimeClient()
                .updateEvent(postEventRequest, loginResponseBody.getToken(), id);

        PostUpdateDeleteEventResponse updateResponse = responseUpdate.body().as(PostUpdateDeleteEventResponse.class);

        assertEquals(201, responseUpdate.statusCode());
        assertEquals("Successfully updated the event with id: " + id, updateResponse.getMessage());
    }

    @Test
    public void unsuccessfulUpdate() {
        postEventRequest.setTitle("");

        Response responseUpdate = new EVPrimeClient()
                .updateEvent(postEventRequest, loginResponseBody.getToken(), id);

        assertEquals(422, responseUpdate.statusCode());
        assertEquals("Updating the event failed due to validation errors.",
                responseUpdate.jsonPath().getString("message"));
        assertEquals("Invalid title.", responseUpdate.jsonPath().getString("errors.title"));
    }

    @After
    public void deleteEvent() throws SQLException {
        if (id != null) {
            assertTrue(dbClient.isEventDeleted(id));
        }
    }

}
