import com.fasterxml.jackson.annotation.JsonCreator;

public class EventsRequest {
    /**
     * Constructor that tells the Jackson parser how to serialize and deserialize the object and json
     */
    @JsonCreator
    public EventsRequest() {

    }
}
