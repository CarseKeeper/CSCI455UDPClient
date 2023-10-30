import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//NOT USED, WE SEND THE EVENT AS A STRING RATHER THAN AN EXTRA REQUEST BODY TYPE

public class UpdateEventRequest {
    String event;

    /**
     * Constructor that tells the Jackson parser how to serialize and deserialize the object and json
     */
    @JsonCreator
    UpdateEventRequest(@JsonProperty("Event") String event) {

    }
}
