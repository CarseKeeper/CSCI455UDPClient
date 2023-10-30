import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/*
 * Makes a package to be sent to the server to create a new event
 */
public class CreateEventRequest {
    public String title;
    public String description;
    public double target;
    public Date deadline;
    public double balance;

    /**
     * Constructor that tells the Jackson parser how to serialize and deserialize the object and json
     */
    @JsonCreator
    public CreateEventRequest(@JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("target") double target,
            @JsonSetter("deadline") String deadline) {

        this.title = title;
        this.description = description;
        if (target > 0)
            this.target = target;
        else
            this.target = Math.abs(target);

        try {
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            if (date.parse(deadline).after(new Date()))
                this.deadline = date.parse(deadline);
            else
                this.deadline = new Date();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.balance = 0.0;

    }

    /**
     * gets the formatted String representation of the date to be displayed
     */
    @JsonGetter("deadline")
    public String getDeadlineString() {
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            df.setTimeZone(tz);
            return df.format(this.deadline);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
