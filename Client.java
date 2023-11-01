import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.ser.Serializers;

import Requests.*;
import Event.Event;
import JSON.WriteJsonObject;
import Responses.Response;

public class Client {

    public static void main(String[] args) {
        try {
            WriteJsonObject json = new WriteJsonObject(); // Serializer and Deserializer object
            Scanner scan = new Scanner(System.in); // Scanner for client input
            ArrayList<Event> EVENTS = new ArrayList<Event>(); // List of all Events from the database
            DatagramSocket server; // Send and recieves packets
            InetAddress Ip; // the server's Ip should stay the same when running
            byte buf[]; // Storage for the json string
            int port; // Port number should also stay the same

            // GETS THE PLACE TO SEND THE DATA TO
            while (true) {
                try {
                    server = connectToServer(); // Method prompts for host ip and port
                    Ip = InetAddress.getByName(getHost());
                    buf = null;
                    port = getPort();
                    break;

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            // MAIN LOGIC LOOP
            while (true) {
                // User is prompted for an action
                System.out.printf(
                        "%-4s    %-30s%n%-4s    %-30s%n%-4s    %-30s%n%-4s    %-30s%n%-4s    %-30s%n%-4s    %-30s\n> ",
                        "(1):", "List the current events", "(2):", "Create a new event", "(3):", "Donate to an event",
                        "(4):", "Update an event", "(5):", "List ALL events", "(q):", "Quit");
                String answer = scan.nextLine();

                // LIST CURRENT EVENTS
                if (answer.startsWith("1")) {
                    EVENTS = getEvents(server, buf, Ip, port, json);
                    EVENTS = currentEvents(EVENTS);
                    listEvents(EVENTS);
                    System.out.println();
                }
                // CREATE A NEW EVENT
                else if (answer.startsWith("2")) {
                    CreateEventRequest newEvent = formEvent();
                    createEvent(newEvent, server, buf, Ip, port, json);
                }
                // DONATE AN AMOUNT TO AN EVENT
                else if (answer.startsWith("3")) {
                    EVENTS = getEvents(server, buf, Ip, port, json);
                    donateToEvent(EVENTS, server, buf, Ip, port, json);
                }
                // UPDATES AN EVENT FROM ALL EVENTS
                else if (answer.startsWith("4")) {
                    EVENTS = getEvents(server, buf, Ip, port, json);
                    listEvents(EVENTS);
                    updateEvent(EVENTS, server, buf, Ip, port, json);
                }
                // PRINTS OUT CURRENT EVENTS AND PAST EVENTS
                else if (answer.startsWith("5")) {
                    EVENTS = getEvents(server, buf, Ip, port, json);
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

            // Always close the connection when done
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
    private static void createEvent(CreateEventRequest newEvent, DatagramSocket server,
            byte buf[], InetAddress Ip, int port,
            WriteJsonObject json) {
        try {
            // Send to server
            String body = json.serialize(newEvent);
            buf = (json.serialize(new Request(RequestType.CREATE, body))).getBytes();
            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, Ip, port);
            server.send(DpSend);

            // Recieve from server
            byte[] recieve = new byte[65535];
            DatagramPacket DpRecieve = new DatagramPacket(recieve, recieve.length);
            server.receive(DpRecieve);

            // Deserialize packet data
            Response response = json.deserialize(data(recieve).toString(), Response.class);

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
        String description = null;
        double target = -1;
        String deadline = null;
        try {
            while (title == null) {
                System.out.print("Enter the title for Event: ");
                title = scan.nextLine();
            }
            while (description == null) {
                System.out.print("Enter the description for the Event: ");

                description = scan.nextLine();
            }
            while (target < 0) {
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
    private static DatagramSocket connectToServer() {
        try {
            return new DatagramSocket();
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
    private static ArrayList<Event> getEvents(DatagramSocket server, byte buf[], InetAddress Ip, int port,
            WriteJsonObject json) {
        ArrayList<Event> events = new ArrayList<Event>();
        try {
            // Send to server
            buf = (json.serialize(new Request(RequestType.EVENTS, json.serialize(new EventsRequest()))))
                    .getBytes();
            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, Ip, port);
            server.send(DpSend);

            // Recive from server
            byte[] recieve = new byte[65535];
            DatagramPacket DpRecieve = new DatagramPacket(recieve, recieve.length);
            server.receive(DpRecieve); // Waits for packet from server

            // Deserialize packet data
            Response response = json.deserialize(data(recieve).toString(),
                    Response.class);
            if (response.responseType == RequestType.EVENTS)
                events = json.deserialize(response.responseBody, new TypeReference<ArrayList<Event>>() {
                });
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return events;
    }

    /**
     * Makes byte array into string
     */
    public static StringBuilder data(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }

    /**
     * lists all the current events and prompts user to choose one
     */
    private static void updateEvent(ArrayList<Event> EVENTS, DatagramSocket server, byte buf[], InetAddress Ip,
            int port, WriteJsonObject json) {
        Scanner scan = new Scanner(System.in);
        try {
            System.out.println("choose an event or -1 to go back: ");
            int index = scan.nextInt() - 1;
            if (index < EVENTS.size() && index >= 0) {
                Event newEvent = EVENTS.get(index);
                String update = changeEvent(newEvent, json);

                // Send to server
                buf = (json.serialize(new Request(RequestType.UPDATE, update))).getBytes();
                DatagramPacket DpSend = new DatagramPacket(buf, buf.length, Ip, port);
                server.send(DpSend);

                // Recive from server
                byte[] recieve = new byte[65535];
                DatagramPacket DpRecieve = new DatagramPacket(recieve, recieve.length);
                server.receive(DpRecieve);

                // Deserialize packet data
                Response response = json.deserialize(data(recieve).toString(),
                        Response.class);
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
    private static void donateToEvent(ArrayList<Event> EVENTS, DatagramSocket server, byte buf[], InetAddress Ip,
            int port, WriteJsonObject json) {
        Scanner scan = new Scanner(System.in);
        try {
            ArrayList<Event> curEvents = currentEvents(EVENTS);
            listEvents(curEvents);
            int index = -2;
            String temp;
            while (index < -1) {
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

                // Send to server
                buf = (json.serialize(new Request(RequestType.DONATE, donate))).getBytes();
                DatagramPacket DpSend = new DatagramPacket(buf, buf.length, Ip, port);
                server.send(DpSend);
            } else if (index == -1) {
                return;
            } else {
                System.out.println("That index is not available.");
            }

            // Recieve from server
            byte[] recieve = new byte[65535];
            DatagramPacket DpRecieve = new DatagramPacket(recieve, recieve.length);
            server.receive(DpRecieve);

            // Deserialize packet data
            Response response = json.deserialize(data(recieve).toString(), Response.class);
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
        while (title == null) {
            System.out.printf("Current Title: %25s%n", event.getTitle());
            System.out.print("New Title: ");
            title = scan.nextLine();
        }
        if (!title.isEmpty())
            event.setTitle(title);

        String description = null;
        while (description == null) {
            System.out.printf("Current Description: %s%n", event.getDescription());
            System.out.print("New Description: ");
            description = scan.nextLine();
        }
        if (!description.isEmpty())
            event.setDescription(description);

        double target = -1.0;
        String temp = "";

        while (target < 0) {
            System.out.printf("Current Target: %-15s%n", getCurrency(event.getTarget()));
            System.out.print("New Target: ");
            temp = scan.nextLine();
            Scanner scantemp = new Scanner(temp);
            if (temp.isEmpty()) {
                break;
            }
            if (scantemp.hasNextDouble())
                target = scantemp.nextDouble();
        }
        if (!temp.isEmpty())
            event.setGoal(target);

        double balance = -1.0;

        while (balance < 0) {
            System.out.printf("Current Balance: %-15s%n", getCurrency(event.getBalance()));
            System.out.print("New Balance: ");
            temp = scan.nextLine();
            Scanner scantemp = new Scanner(temp);
            if (temp.isEmpty()) {
                break;
            }
            if (scantemp.hasNextDouble())
                balance = scantemp.nextDouble();
        }
        if (!temp.isEmpty())
            event.setCurrentPool(balance);

        String date = null;
        while (date == null) {
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
            System.out.printf(
                    "%8d.    %-35s  Ends: %-12s%n               = %-1s =%n----------------------------------------------------%n",
                    i++, event.getTitle(), event.getDeadlineString().substring(0, 10), event.getDescription());
        }
    }

    /**
     * Prints all events, past and current, in a table
     */
    private static void listAllEvents(ArrayList<Event> EVENTS) {
        ArrayList<Event> curEvents = currentEvents(EVENTS);
        ArrayList<Event> pastEvents = pastEvents(EVENTS);
        System.out.println(
                "--------------------------------------------------------------------------|=|--------------------------------------------------------------------------");
        System.out.printf("|  %-25s | %-15s | %-8s | %-12s  |=|  %-25s | %-15s | %-8s | %-12s  |%n", "Current Events",
                "Target", "Percent", "Deadline", "Past Events", "Target", "Percent", "Deadline");
        System.out.println(
                "--------------------------------------------------------------------------|=|--------------------------------------------------------------------------");
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

            System.out.printf("|  %-25s | %-15s | %-8s | %-12s  |=|  %-25s | %-15s | %-8s | %-12s  |%n", ce, cet, cep,
                    ced, pe, pet, pep, ped);
            System.out.println(
                    "--------------------------------------------------------------------------|=|--------------------------------------------------------------------------");

        }
        System.out.println();
    }

    /**
     * gets the percent completeness of an event and returns it as a String with 2
     * decimal places
     */
    private static String getPercent(double target, double balance) {
        double percent = (balance / target);
        NumberFormat percentage = NumberFormat.getPercentInstance();
        return percentage.format(percent);
    }

    /**
     * gets the currency formatted String of a value
     */
    private static String getCurrency(double value) {
        DecimalFormat currency = new DecimalFormat("$###,##0.00");
        return currency.format(value);
    }
}
