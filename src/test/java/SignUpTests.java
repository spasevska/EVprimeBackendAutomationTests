import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import models.response.SignUpResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import util.Configuration;

import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;
import static org.junit.Assert.*;

public class SignUpTests {

    private SignUpLoginRequest signUpRequest;

    @Test
    public void successfulSignUp() {
        /* creating a body */
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(RandomStringUtils.randomAlphanumeric(10) + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphanumeric(10))
                .createRequest();

        /* --------- */
        Response response = new EVPrimeClient()
                .signUp(signUpRequest);

        /* serialization */
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(201, response.getStatusCode());
        assertEquals(signUpRequest.getEmail(), signUpResponse.getUser().getEmail());
        assertEquals("User created.", signUpResponse.getMessage());
        assertNotNull(signUpResponse.getToken());
        assertNotNull(signUpResponse.getUser().getId());
    }

    @Test
    public void unsuccessfulSignUpExistingEmail() {
        /* creating a body */
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(Configuration.USER_MAIL)
                .setPassword(Configuration.USER_PASSWORD)
                .createRequest();

        Response response = new EVPrimeClient()
                .signUp(signUpRequest);

        /* serialization */
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(422, response.getStatusCode());
        assertEquals("User signup failed due to validation errors.", signUpResponse.getMessage());
        assertEquals("Email exists already.", signUpResponse.getErrors().getEmail());
    }

    @Test
    public void unsuccessfulSignUpInvalidEmail() {
        /* creating a body */
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(RandomStringUtils.randomAlphanumeric(10) + "mail.com")
                .setPassword(RandomStringUtils.randomAlphanumeric(10))
                .createRequest();

        Response response = new EVPrimeClient()
                .signUp(signUpRequest);

        /* serialization */
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(422, response.getStatusCode());
        assertEquals("User signup failed due to validation errors.", signUpResponse.getMessage());
        assertEquals("Invalid email.", signUpResponse.getErrors().getEmail());
    }

    @Test
    public void unsuccessfulSignUpInvalidPassword() {
        /* creating a body */
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(Configuration.USER_MAIL)
                .setPassword("pass")
                .createRequest();

        Response response = new EVPrimeClient()
                .signUp(signUpRequest);

        /* serialization */
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(422, response.getStatusCode());
        assertEquals("User signup failed due to validation errors.", signUpResponse.getMessage());
        assertEquals("Email exists already.", signUpResponse.getErrors().getEmail());
        assertEquals("Invalid password. Must be at least 6 characters long.", signUpResponse.getErrors().getPassword());
    }

    @Test
    public void unsuccessfulSignUpInvalidEmailAndPassword() {
        /* creating a body */
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUpLogin())
                .setEmail(RandomStringUtils.randomAlphanumeric(10) + "mail.com")
                .setPassword("pass")
                .createRequest();

        Response response = new EVPrimeClient()
                .signUp(signUpRequest);

        /* serialization */
        SignUpResponse signUpResponse = response.body().as(SignUpResponse.class);

        assertEquals(422, response.getStatusCode());
        assertEquals("User signup failed due to validation errors.", signUpResponse.getMessage());
        assertEquals("Invalid email.", signUpResponse.getErrors().getEmail());
        assertEquals("Invalid password. Must be at least 6 characters long.", signUpResponse.getErrors().getPassword());
    }
}