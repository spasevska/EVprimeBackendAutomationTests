import client.EVPrimeClient;
import data.PostEventDataFactory;
import data.SignUpLoginDataFactory;
import database.DBClient;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
import models.request.SignUpLoginRequest;
import models.response.LoginResponse;
import models.response.PostUpdateDeleteEventResponse;
import org.junit.Before;
import org.junit.Test;
import util.Configuration;

import java.sql.SQLException;

import static objectBuilder.PostUpdateEventObjectBuilder.createBodyForPostEvent;
import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DeleteEventTests {

    DBClient dbClient = new DBClient();
    private SignUpLoginRequest signUpRequest;
    private LoginResponse loginResponseBody;
    private PostUpdateEventRequest postEventRequest;
    private String id;


    @Before
    public void setUp() {
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(Configuration.USER_MAIL)
                .setPassword(Configuration.USER_PASSWORD)
                .createRequest();

        new EVPrimeClient()
                .signUp(signUpRequest);

        Response response = new EVPrimeClient()
                .login(signUpRequest);

        loginResponseBody = response.body().as(LoginResponse.class);

        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime")
                .setImage("https://images.pexels.com/photos/35908536/pexels-photo-35908536.jpeg")
                .setDate("2026-01-25")
                .setLocation("Tokio")
                .setDescription("Nature")
                .createRequest();

        Response responsePost = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse postResponse = responsePost.body().as(PostUpdateDeleteEventResponse.class);
        id = postResponse.getMessage().substring(39);
    }

    @Test
    public void delete() throws SQLException {
        Response responseBody = new EVPrimeClient()
                .deleteEvent(id, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse responseDelete = responseBody.body().as(PostUpdateDeleteEventResponse.class);

        assertEquals(200, responseBody.statusCode());
        assertEquals("Successfully deleted the event with id: " + id, responseDelete.getMessage());
        assertFalse(dbClient.isEventDeleted(id));
    }
}