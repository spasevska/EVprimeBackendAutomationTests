import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import database.DBClient;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import models.response.GetEventsResponse;
import models.response.LoginResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.Configuration;


import java.sql.SQLException;

import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;
import static org.junit.Assert.*;

public class GetEventsTests {

    private SignUpLoginRequest signUpRequest;
    private LoginResponse loginResponseBody;
    private static String id;
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
    }

    @Test
    public void getAllEvents() {
        Response responseGetAllEvents = new EVPrimeClient()
                .getAllEvents();

        GetEventsResponse getAllEvents = responseGetAllEvents.body().as(GetEventsResponse.class);

        assertEquals(200, responseGetAllEvents.statusCode());
        assertFalse(getAllEvents.getEvents().isEmpty());
    }

    @After
    public void deleteEvent() throws SQLException {
        if (id != null) {
            assertTrue(dbClient.isEventDeleted(id));
        }
    }

}
