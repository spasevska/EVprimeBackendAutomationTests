import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import models.response.LoginResponse;
import models.response.SignUpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.Configuration;

import java.sql.SQLException;

import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;

public class GetAllEventsTests {

    private SignUpLoginRequest signUpRequest;
    private LoginResponse loginResponseBody;

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
        // create event (get if from the event)
    }

    @Test
    public void get() {
        // get event (update event with the taken id from before method)
    }

    @After
    public void deleteEvent() throws SQLException {
        // delete event
    }
}
