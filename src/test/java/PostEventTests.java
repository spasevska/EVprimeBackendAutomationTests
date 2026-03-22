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
                .setEmail(RandomStringUtils.randomAlphanumeric(10) + "@mail.com")
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

    // unsuccessful
    @Test
    public void postEventNonToken() {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime")
                .setImage("https://thumbs.dreamstime.com/b/beautiful-rain-forest-ang-ka-nature-trail-doi-inthanon-national-park-thailand-36703721.jpg")
                .setDate("2026-01-25")
                .setLocation("Tokio")
                .setDescription("Nature")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, "");

        PostUpdateDeleteEventResponse postResponse = response.body().as(PostUpdateDeleteEventResponse.class);

        assertEquals(401, response.statusCode());
        assertEquals("Not authenticated.", postResponse.getMessage());
    }

    @Test
    public void postEventEmptyTitle() {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("")
                .setImage("https://thumbs.dreamstime.com/b/beautiful-rain-forest-ang-ka-nature-trail-doi-inthanon-national-park-thailand-36703721.jpg")
                .setDate("2026-01-25")
                .setLocation("Tokio")
                .setDescription("Nature")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        assertEquals(422, response.statusCode());
        assertEquals("Adding the event failed due to validation errors.",
                response.jsonPath().getString("message"));
        assertEquals("Invalid title.", response.jsonPath().getString("errors.title"));

    }

    @Test
    public void postEventEmptyImage() {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime")
                .setImage("")
                .setDate("2026-01-25")
                .setLocation("Tokio")
                .setDescription("Nature")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        assertEquals(422, response.statusCode());
        assertEquals("Adding the event failed due to validation errors.",
                response.jsonPath().getString("message"));
        assertEquals("Invalid image.", response.jsonPath().getString("errors.image"));
    }

    @Test
    public void postEventEmptyDate() {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime")
                .setImage("https://images.pexels.com/photos/35908536/pexels-photo-35908536.jpeg")
                .setDate("")
                .setLocation("Tokio")
                .setDescription("Nature")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        assertEquals(422, response.statusCode());
        assertEquals("Adding the event failed due to validation errors.",
                response.jsonPath().getString("message"));
        assertEquals("Invalid date.", response.jsonPath().getString("errors.date"));
    }

    @Test
    public void postEventEmptyLocation() {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime")
                .setImage("https://images.pexels.com/photos/35908536/pexels-photo-35908536.jpeg")
                .setDate("2026-01-25")
                .setLocation("")
                .setDescription("Nature")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        assertEquals(422, response.statusCode());
        assertEquals("Adding the event failed due to validation errors.",
                response.jsonPath().getString("message"));
        assertEquals("Invalid location.", response.jsonPath().getString("errors.description"));
    }

    @Test
    public void postEventEmptyDescription() {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime")
                .setImage("https://images.pexels.com/photos/35908536/pexels-photo-35908536.jpeg")
                .setDate("2026-01-25")
                .setLocation("Tokio")
                .setDescription("")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        assertEquals(422, response.statusCode());
        assertEquals("Adding the event failed due to validation errors.",
                response.jsonPath().getString("message"));
        assertEquals("Invalid description.", response.jsonPath().getString("errors.description"));
    }

    @After
    public void deleteEvent() throws SQLException {
        if (id != null) {
            assertTrue(dbClient.isEventDeleted(id));
        }
    }

}
