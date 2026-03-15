package objectBuilder;

import models.request.PostUpdateEventRequest;

public class PostUpdateEventObjectBuilder {

    public static PostUpdateEventRequest createBodyForPostEvent() {
        return PostUpdateEventRequest.builder()
                .title("default title")
                .image("default image")
                .date("2024-04-07")
                .location("default location")
                .description("default description")
                .build();
    }

}
