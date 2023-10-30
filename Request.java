
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Request {
    public RequestType requestType;
    public String requestBody;

    /**
     * Constructor that tells the Jackson parser how to serialize and deserialize the object and json
     */
    @JsonCreator
    public Request(@JsonProperty("RequestType") RequestType type, @JsonProperty("RequestBody") String requestBody) {
        this.requestType = type;
        this.requestBody = requestBody;
    }

}
