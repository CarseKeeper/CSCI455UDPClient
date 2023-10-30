import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@JsonPropertyOrder({ "id", "title", "description", "target", "deadline"})
public class Event {
    private final int id;
    private String title;
    private String description;
    private double target;
    private Date deadline;
    private double balance;

    /**
     * Constructor that tells the Jackson parser how to serialize and deserialize the object and json,
     * has validation so if wierd data gets into the constructor, it gets normalized.
     */
    @JsonCreator
    public Event(@JsonProperty("id") int id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("target") double target,
            @JsonProperty("currentAmount") double currentAmount,
            @JsonProperty("deadline") String deadline) throws ParseException {
        this.id = id;
        this.title = title;
        this.description = description;
        if (target > 0)
            this.target = target;
        else
            this.target = Math.abs(target);
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        if (isoFormat.parse(deadline).after(new Date()))
            this.deadline = isoFormat.parse(deadline);
        else
            this.deadline = new Date();
        this.balance = currentAmount;
    }

    // Getters
    @JsonGetter("id")
    public int getId() {
        return id;
    }

    @JsonGetter("title")
    public String getTitle() {
        return this.title;
    }

    @JsonGetter("description")
    public String getDescription() {
        return this.description;
    }

    @JsonGetter("target")
    public double getTarget() {
        return this.target;
    }

    @JsonGetter("currentAmount")
    public double getBalance() {
        return this.balance;
    }

    @JsonGetter("deadline")
    public String getDeadlineString() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        df.setTimeZone(tz);
        return df.format(this.deadline);
    }

    public Date getDeadline() {
        return this.deadline;
    }



    // Setters
    public void setTitle(String event) {
        this.title = event;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGoal(double goal) {
        if (goal > 0.0)
            this.target = goal;
        else
            this.target = 0.0;
    }

    public void setCurrentPool(double donation) {
        if (donation >= 0.0)
            this.balance = donation;
        else
            this.balance = Math.abs(donation);
    }

    public void setEndDate(Date endDate) {
        if ((endDate.after(this.deadline) && endDate.after(new Date())) || this.deadline == null)
            this.deadline = endDate;
    }

    public void setEndDate(String date) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            this.deadline = isoFormat.parse(date);
        } catch (Exception e) {

        }
    }

    // Methods
    public boolean goalReached() {
        return this.balance >= this.target;
    }

    public String timeLeft() {
        Date now = new Date();

        Date timeLeft = new Date((this.deadline.getTime() - now.getTime()));
        return timeLeft.toString();
    }

    public boolean hasEnded() {
        return !(new Date()).before(this.deadline);
    }

}
