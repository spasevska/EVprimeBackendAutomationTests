import client.EVPrimeClient;
import data.PostEventDataFactory;
import data.SignUpLoginDataFactory;
import database.DBClient;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
import models.request.SignUpLoginRequest;
import models.response.GetEventsResponse;
import models.response.LoginResponse;
import models.response.PostUpdateDeleteEventResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.Configuration;

import java.sql.SQLException;

import static objectBuilder.PostUpdateEventObjectBuilder.createBodyForPostEvent;
import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;
import static org.junit.Assert.*;

public class GetSingleEvent {

    private SignUpLoginRequest signUpRequest;
    private LoginResponse loginResponseBody;
    private static String id;
    private static String invalidId = "Invalid-id-evprime";
    private PostUpdateEventRequest postEventRequest;
    private DBClient dbClient = new DBClient();

    @Before
    public void setUp() {

        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(Configuration.USER_MAIL)
                .setPassword(Configuration.USER_PASSWORD)
                .createRequest();

        // sign up
        new EVPrimeClient()
                .signUp(signUpRequest);

        // login
        Response loginResponse = new EVPrimeClient()
                .login(signUpRequest);

        loginResponseBody = loginResponse.body().as(LoginResponse.class);

        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("EVprime new")
                .setImage("https://images.pexels.com/photos/35908536/pexels-photo-35908536.jpeg")
                .setDate("2026-01-10")
                .setLocation("Tokio new")
                .setDescription("Nature new")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse postResponse = response.body().as(PostUpdateDeleteEventResponse.class);
        id = postResponse.getMessage().substring(39);
    }

    @Test
    public void getEventById() {
        Response responseById = new EVPrimeClient()
                .getSingleEvent(id);

        GetEventsResponse response = responseById.body().as(GetEventsResponse.class);

        assertEquals(200, responseById.statusCode());
        assertFalse(response.getEvents().isEmpty());
        assertEquals(1, response.getEvents().size());
        assertNotNull(response);

        assertEquals(postEventRequest.getTitle(), response.getEvents().get(0).getTitle());
        assertEquals(postEventRequest.getImage(), response.getEvents().get(0).getImage());
        assertEquals(postEventRequest.getDate(), response.getEvents().get(0).getDate());
        assertEquals(postEventRequest.getLocation(), response.getEvents().get(0).getLocation());
        assertEquals(postEventRequest.getDescription(), response.getEvents().get(0).getDescription());
    }

    @Test
    public void getEventNonExistingId() {
        Response responseById = new EVPrimeClient()
                .getSingleEvent(invalidId);

        GetEventsResponse response = responseById.body().as(GetEventsResponse.class);
        assertEquals(200, responseById.statusCode());
        assertTrue(response.getEvents().isEmpty());
    }

    @After
    public void deleteEvent() throws SQLException {
        assertTrue(dbClient.isEventDeleted(id));
    }

}
