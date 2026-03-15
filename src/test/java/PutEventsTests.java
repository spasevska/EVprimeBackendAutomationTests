import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import models.response.LoginResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;

public class PutEventsTests {
    private SignUpLoginRequest loginRequest;
    private LoginResponse loginResponseBody;

    @Before
    public void setUp() {
        // login
        // create event (get id from the event)

        loginRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail("evprime@gmail.com")
                .setPassword("password")
                .createRequest();

        // login
        Response loginResponse = new EVPrimeClient()
                .login(loginRequest);

        loginResponseBody = loginResponse.body().as(LoginResponse.class);

    }

    @Test
    public void update() {
        // update event (update event with the taken id from before method)
    }

    @After
    public void deleteEvent() throws SQLException{
        // delete event
    }

}
