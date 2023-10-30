import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventRequest {
    public int eventId;

    /**
     * Constructor that tells the Jackson parser how to serialize and deserialize the object and json
     */
    @JsonCreator
    public EventRequest(@JsonProperty("id") int id) {
        this.eventId = id;
    }

}
