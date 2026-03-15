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

public class PostEventTests {
    private SignUpLoginRequest signUpRequest;
    private LoginResponse loginResponseBody;
    private PostUpdateEventRequest postEventRequest;
    private static String id;
    private DBClient dbClient = new DBClient();

    @Before
    public void setUp() {
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(RandomStringUtils.randomAlphanumeric(10)+ "@mail.com")
                .setPassword(RandomStringUtils.randomAlphanumeric(10))
                .createRequest();

        // sign up
        new EVPrimeClient()
                .signUp(signUpRequest);

        // login
        Response loginResponse = new EVPrimeClient()
                .login(signUpRequest);

        loginResponseBody = loginResponse.body().as(LoginResponse.class);
    }

    @Test
    public void successfulPostEventTest() throws SQLException {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime")
                .setImage("https://thumbs.dreamstime.com/b/beautiful-rain-forest-ang-ka-nature-trail-doi-inthanon-national-park-thailand-36703721.jpg")
                .setDate("2026-01-25")
                .setLocation("Tokio")
                .setDescription("Nature")
                .createRequest();

        // creating a new event
        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse postResponse = response.body().as(PostUpdateDeleteEventResponse.class);
        id = postResponse.getMessage().substring(39);

        assertEquals(201, response.statusCode());
        assertTrue(postResponse.getMessage().contains("Successfully created an event with id: " + id));
        assertEquals(postEventRequest.getTitle(), dbClient.getEventFromDB(id).getTitle());
        assertEquals(postEventRequest.getImage(), dbClient.getEventFromDB(id).getImage());
        assertEquals(postEventRequest.getDate(), dbClient.getEventFromDB(id).getDate());
        assertEquals(postEventRequest.getLocation(), dbClient.getEventFromDB(id).getLocation());
        assertEquals(postEventRequest.getDescription(), dbClient.getEventFromDB(id).getDescription());

    }

    @After
    public void deleteEvent() throws SQLException {
        assertTrue(dbClient.isEventDeleted(id));
    }

}
