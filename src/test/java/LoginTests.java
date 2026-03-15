import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import models.response.LoginResponse;
import models.response.SignUpResponse;
import org.junit.Before;
import org.junit.Test;
import util.Configuration;

import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoginTests {

    private SignUpLoginRequest signUpLoginRequest;

    @Before
    public void setUp() {
        signUpLoginRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(Configuration.USER_MAIL)
                .setPassword(Configuration.USER_PASSWORD)
                .createRequest();

        Response response = new EVPrimeClient()
                .signUp(signUpLoginRequest);

        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);
    }

    @Test
    public void successfulLogin() {
        Response response = new EVPrimeClient()
                .login(signUpLoginRequest);

        LoginResponse loginResponse = response.body().as(LoginResponse.class);

        assertEquals(200, response.statusCode());
        assertNotNull(loginResponse.getToken());
        assertNotNull(loginResponse.getExpirationTime());
    }

    @Test
    public void unsuccessfulLoginInvalidOrEmptyEmail() {
        SignUpLoginRequest loginRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail("invalidemail.com")
                .setPassword(Configuration.USER_PASSWORD)
                .createRequest();

        Response response = new EVPrimeClient()
                .login(loginRequest);

        LoginResponse loginResponse = response.body().as(LoginResponse.class);

        assertEquals(401, response.statusCode());
        assertEquals("Authentication failed.", loginResponse.getMessage());
    }

    @Test
    public void unsuccessfulLoginInvalidPassword() {
        SignUpLoginRequest loginRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(Configuration.USER_MAIL)
                .setPassword("123456")
                .createRequest();

        Response response = new EVPrimeClient()
                .login(loginRequest);

        LoginResponse loginResponse = response.body().as(LoginResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("Invalid email or password entered.", loginResponse.getErrors().getCredentials());
    }

    @Test
    public void unsuccessfulLoginEmptyPassword() {
        SignUpLoginRequest loginRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(Configuration.USER_MAIL)
                .setPassword("")
                .createRequest();

        Response response = new EVPrimeClient()
                .login(loginRequest);

        LoginResponse loginResponse = response.body().as(LoginResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("Invalid credentials.", loginResponse.getMessage());
        assertEquals("Invalid email or password entered.", loginResponse.getErrors().getCredentials());
    }
}
