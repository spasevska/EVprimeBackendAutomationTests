package objectBuilder;

import models.request.SignUpLoginRequest;

public class SignUpLoginObjectBuilder {

    public static SignUpLoginRequest createBodyForSignUpLogin() {
        return SignUpLoginRequest.builder()
                .email("evprime@gmail.com")
                .password("password")
                .build();
    }

}
