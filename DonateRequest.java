import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DonateRequest {
    public double amount;
    public int eventid;

    /**
     * Constructor that tells the Jackson parser how to serialize and deserialize the object and json
     */
    @JsonCreator
    public DonateRequest(@JsonProperty("id") int eventid,
            @JsonProperty("amount") double amount) {
        if (eventid > 0)
            this.eventid = eventid;
        else
            this.eventid = Math.abs(eventid);

        if (amount > 0.0)
            this.amount = amount;
        else
            this.amount = Math.abs(amount);
    }
}
