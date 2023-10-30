import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.ser.Serializers;

public class Client {

    public static void main(String[] args) {
        try {
            WriteJsonObject json = new WriteJsonObject(); //Serializer and Deserializer object
            Scanner scan = new Scanner(System.in); // Scanner for client input
            ArrayList<Event> EVENTS = new ArrayList<Event>(); // List of all Events from the database
            Socket server;
            BufferedReader in; //from server
            DataOutputStream out; //to server

            // While loop for getting a connection to the server
            while (true) {
                try {
                    server = connectToServer(); //Method prompts for host ip and port
                    out = new DataOutputStream(server.getOutputStream()); //dataouputstream created from socket
                    in = new BufferedReader(new InputStreamReader(server.getInputStream())); //bufferedreader created from socket
                    break;

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            //MAIN Logic Loop
            while (true) {
                //User is prompted for an action
                System.out.printf("%-4s    %-30s%n%-4s    %-30s%n%-4s    %-30s%n%-4s    %-30s%n%-4s    %-30s%n%-4s    %-30s\n> ",
                        "(1):", "List the current events", "(2):", "Create a new event", "(3):", "Donate to an event",
                        "(4):", "Update an event", "(5):", "List ALL events", "(q):", "Quit");
                String answer = scan.nextLine();

                // LIST CURRENT EVENTS
                if (answer.startsWith("1")) {
                    EVENTS = getEvents(in, out);
                    EVENTS = currentEvents(EVENTS);
                    listEvents(EVENTS);
                    System.out.println();
                }
                // CREATE A NEW EVENT
                else if (answer.startsWith("2")) {
                    CreateEventRequest newEvent = formEvent();
                    createEvent(newEvent, out, in, json);
                }
                // DONATE AN AMOUNT TO AN EVENT
                else if (answer.startsWith("3")) {
                    EVENTS = getEvents(in, out);
                    donateToEvent(EVENTS, in, out, json);
                }
                // UPDATES AN EVENT FROM ALL EVENTS
                else if (answer.startsWith("4")) {
                    EVENTS = getEvents(in, out);
                    listEvents(EVENTS);
                    updateEvent(EVENTS, in, out, json);
                }
                // PRINTS OUT CURRENT EVENTS AND PAST EVENTS
                else if (answer.startsWith("5")) {
                    EVENTS = getEvents(in, out);
                    listAllEvents(EVENTS);
                    System.out.println();
                }
                // QUITS THE PROGRAM
                else if (answer.toLowerCase().startsWith("q")) {
                    break;
                }
                // INVALID INPUT DETECTED, TRY AGAIN
                else {
                    System.out.println("Invalid input.");
                }
            }

            //Always close the connection when done
            server.close();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    // -------------------------------------------------------------------------------------------------------------------------------------//
    // -------------------------------------------------------------------------------------------------------------------------------------//

    /**
     * Gets the active events from all events
     */
    private static ArrayList<Event> currentEvents(ArrayList<Event> EVENTS) {
        return EVENTS
                .stream()
                .filter(event -> !event.hasEnded())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets the past events from all events
     */
    private static ArrayList<Event> pastEvents(ArrayList<Event> EVENTS) {
        return EVENTS
                .stream()
                .filter(Event::hasEnded)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Sends information to create a new event
     */
    private static void createEvent(CreateEventRequest newEvent, DataOutputStream out,
            BufferedReader in,
            WriteJsonObject json) {
        try {
            String body = json.serialize(newEvent);
            out.writeBytes(json.serialize(new Request(RequestType.CREATE, body)) + "\n");
            in.readLine();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Creates a new Event object to be sent
     */
    private static CreateEventRequest formEvent() {
        Scanner scan = new Scanner(System.in);
        String title = null;
        String description =null;
        double target = -1;
        String deadline = null;
        try {
            while(title == null) {
                System.out.print("Enter the title for Event: ");
                title = scan.nextLine();
            }
            while(description == null) {
                System.out.print("Enter the description for the Event: ");

                description = scan.nextLine();
            }
            while(target < 0) {
                System.out.print("Enter the target amount as a double (####.##): ");
                String temp = scan.nextLine();
                Scanner tempScan = new Scanner(temp);
                if (tempScan.hasNextDouble())
                    target = tempScan.nextDouble();
            }
            while (deadline == null) {
                System.out.println(
                        "Enter the deadline in the form YYYY-MM-DDTHH:MM:ss.SSSZ\ne.g. Oct 25, 2023 at 1:45:30.30PM would be 2023-10-25T13:45:30.300Z: ");
                deadline = scan.nextLine();
            }

            return (new CreateEventRequest(title, description, target, deadline));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }

    }

    /**
     * tries to connect to the server
     */
    private static Socket connectToServer() {
        try {
            return new Socket(getHost(), getPort(), null, 0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * helper method of connectToServer to prompt the client for a server host
     */
    private static String getHost() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter IP address: ");
        try {
            String host = scan.nextLine();
            return host;
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    /**
     * helper method of connectToServer to prompt the client for a port number
     */
    private static int getPort() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter port number: ");
        try {
            int port = scan.nextInt();
            return port;
        } catch (Exception e) {
            System.err.println(e);
        }
        return 6789;
    }

    /**
     * gets all events from server and returns them as an ArrayList
     */
    private static ArrayList<Event> getEvents(BufferedReader in, DataOutputStream out) {
        ArrayList<Event> events = new ArrayList<Event>();
        WriteJsonObject json = new WriteJsonObject();
        try {
            out.writeBytes(json.serialize(new Request(RequestType.EVENTS, json.serialize(new EventsRequest()))) + "\n");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            Response response = json.deserialize(in.readLine(), Response.class);
            if (response.responseType == RequestType.EVENTS)
                events = json.deserialize(response.responseBody, new TypeReference<ArrayList<Event>>() {});
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        return events;
    }

    /**
     * lists all the current events and prompts user to choose one
     */
    private static void updateEvent(ArrayList<Event> EVENTS, BufferedReader in, DataOutputStream out,
            WriteJsonObject json) {
        Scanner scan = new Scanner(System.in);
        try {
            System.out.println("choose an event or -1 to go back: ");
            int index = scan.nextInt() - 1;
            if (index < EVENTS.size()  && index >= 0) {
                Event newEvent = EVENTS.get(index);
                String update = changeEvent(newEvent, json);
                out.writeBytes(json.serialize(new Request(RequestType.UPDATE, update)) + "\n");
                in.readLine();
            } else if (index == -2) {

            } else {
                System.out.println("That index is not available.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Prompts the user to pick an event and donates money to it
     */
    private static void donateToEvent(ArrayList<Event> EVENTS, BufferedReader in, DataOutputStream out,
            WriteJsonObject json) {
        Scanner scan = new Scanner(System.in);
        try {
            ArrayList<Event> curEvents = currentEvents(EVENTS);
            listEvents(curEvents);
            int index = -2;
            String temp;
            while (index < -1){
                System.out.print("Choose and active event or 0 to go back: ");
                temp = scan.nextLine();
                Scanner scanTemp = new Scanner(temp);
                if (scanTemp.hasNextInt())
                    index = scanTemp.nextInt() - 1;

            }

            if (index >= 0 && index < curEvents.size()) {
                double amount = -1.0;

                while (amount < 0) {
                    System.out.print("Enter a donation amount (####.##): ");
                    temp = scan.nextLine();
                    Scanner scanTemp = new Scanner(temp);
                    if (scanTemp.hasNextDouble())
                        amount = scanTemp.nextDouble();
                }
                String donate = json.serialize(new DonateRequest(curEvents.get(index).getId(), amount));
                out.writeBytes(json.serialize(new Request(RequestType.DONATE, donate)) + "\n");
            } else if (index == -1) {
                return;
            } else {
                System.out.println("That index is not available.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            Response response = json.deserialize(in.readLine(), Response.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Prints current variables and prompts for new variable
     */
    private static String changeEvent(Event event, WriteJsonObject json) {
        Scanner scan = new Scanner(System.in);
        String title = null;
        while (title == null){
            System.out.printf("Current Title: %25s%n", event.getTitle());
            System.out.print("New Title: ");
            title = scan.nextLine();
        }
        if (!title.isEmpty())
            event.setTitle(title);

        String description = null;
        while(description == null) {
            System.out.printf("Current Description: %s%n", event.getDescription());
            System.out.print("New Description: ");
            description = scan.nextLine();
        }
        if (!description.isEmpty())
            event.setDescription(description);

        double target = -1.0;
        String temp = "";

        while(target < 0) {
            System.out.printf("Current Target: %-15s%n", getCurrency(event.getTarget()));
            System.out.print("New Target: ");
            temp = scan.nextLine();
            Scanner scantemp = new Scanner(temp);
            if(temp.isEmpty()){
                break;
            }
            if(scantemp.hasNextDouble())
                target = scantemp.nextDouble();
        }
        if (!temp.isEmpty())
            event.setGoal(target);

        double balance = -1.0;

        while(balance < 0) {
            System.out.printf("Current Balance: %-15s%n", getCurrency(event.getBalance()));
            System.out.print("New Balance: ");
            temp = scan.nextLine();
            Scanner scantemp = new Scanner(temp);
            if(temp.isEmpty()){
                break;
            }
            if(scantemp.hasNextDouble())
                balance = scantemp.nextDouble();
        }
        if (!temp.isEmpty())
            event.setCurrentPool(balance);

        String date = null;
        while(date == null) {
            System.out.printf("Current Date: %-20s%n", event.getDeadlineString());
            System.out.print("New Date (YYYY-MM-DDTHH:MM:ss.SSSZ): ");
            date = scan.nextLine();
        }
        if (!date.isEmpty())
            event.setEndDate(date);
        return json.serialize(event);
    }

    /**
     * helper method of chooseEvent that lists all events
     */
    private static void listEvents(ArrayList<Event> events) {
        int i = 1;
        for (Event event : events) {
            System.out.printf("%8d.    %-35s  Ends: %-12s%n               = %-1s =%n----------------------------------------------------%n", i++, event.getTitle(), event.getDeadlineString().substring(0,10), event.getDescription());
        }
    }

    /**
     * Prints all events, past and current, in a table
     */
    private static void listAllEvents(ArrayList<Event> EVENTS){
        ArrayList<Event> curEvents = currentEvents(EVENTS);
        ArrayList<Event> pastEvents = pastEvents(EVENTS);
        System.out.println("--------------------------------------------------------------------------|=|--------------------------------------------------------------------------");
        System.out.printf("|  %-25s | %-15s | %-8s | %-12s  |=|  %-25s | %-15s | %-8s | %-12s  |%n", "Current Events", "Target", "Percent", "Deadline", "Past Events", "Target", "Percent", "Deadline");
        System.out.println("--------------------------------------------------------------------------|=|--------------------------------------------------------------------------");
        for (int i = 0; i < curEvents.size() || i < pastEvents.size(); i++) {
            String ce = "";
            String cet = "";
            String cep = "";
            String ced = "";
            String pe = "";
            String pet = "";
            String pep = "";
            String ped = "";
            if (i < curEvents.size()) {
                ce = curEvents.get(i).getTitle();
                cet = getCurrency(curEvents.get(i).getTarget());
                cep = getPercent(curEvents.get(i).getTarget(), curEvents.get(i).getBalance());
                ced = curEvents.get(i).getDeadlineString().substring(0, 10);
            }
            if (i < pastEvents.size()) {
                pe = pastEvents.get(i).getTitle();
                pet = getCurrency(pastEvents.get(i).getTarget());
                pep = getPercent(pastEvents.get(i).getTarget(), pastEvents.get(i).getBalance());
                ped = pastEvents.get(i).getDeadlineString().substring(0, 10);
            }

            System.out.printf("|  %-25s | %-15s | %-8s | %-12s  |=|  %-25s | %-15s | %-8s | %-12s  |%n", ce, cet, cep, ced, pe, pet, pep, ped);
            System.out.println("--------------------------------------------------------------------------|=|--------------------------------------------------------------------------");

        }
        System.out.println();
    }

    /**
     * gets the percent completeness of an event and returns it as a String with 2 decimal places
     */
    private static String getPercent(double target, double balance){
        double percent = (balance/target);
        NumberFormat percentage = NumberFormat.getPercentInstance();
        return percentage.format(percent);
    }

    /**
     * gets the currency formatted String of a value
     */
    private static String getCurrency(double value){
        DecimalFormat currency = new DecimalFormat("$###,##0.00");
        return currency.format(value);
    }
}
