
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {
    public RequestType responseType;
    public String responseBody;

    /**
     * Constructor that tells the Jackson parser how to serialize and deserialize the object and json
     */
    @JsonCreator
    public Response(@JsonProperty("RequestType") RequestType type, @JsonProperty("ResponseBody") String responseBody) {
        this.responseType = type;
        this.responseBody = responseBody;
    }

    /**
     * Event parser from json string
     */
    public static Event parseEvent(WriteJsonObject json, String response) {
        Response res = json.deserialize(response, Response.class);

        return json.deserialize(res.responseBody, Event.class);
    }

    /**
     * Event list parser from json string
     */
    public static ArrayList<Event> parseEvents(WriteJsonObject json, String response) {
        Response res = json.deserialize(response, Response.class);

        return (ArrayList<Event>) json.deserialize(res.responseBody, ArrayList.class);
    }
}
