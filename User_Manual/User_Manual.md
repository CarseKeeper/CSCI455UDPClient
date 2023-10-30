# <ins>User Manual</ins>
## *Server-side Program*

### Press Play

That's right! Start the executable and give your clients your
IP that will show in your terminal.

### Logging

Standard logs will be in white and errors will appear in red.
Errors will be logged in the server and sent to the client.

### Request Response Model

The Server Client pair uses an in-house communication method
that passes serialized request and response objects to each other.
This helps protect data and makes error handling easy.

### Database

The database used for the server of this project is SQLlite which when opened up looks like this:

<div align="center"><p><img src=Pictures/Server.png alt="SQLlite Database" width=90% style="border: white solid 1px"></p>
The SQLlite Database</div>

## *Client-side Program*

### How to run the Client side:

1. Run the Executable for the Client

2. Enter the server computer's IP address when prompted

3. Enter the port number when prompted (our project uses 6789)

4. Enter the Option you want to use from the Option list (more information in the Options section)

5. Follow the Prompts as listed

<div align="center"><p><img src="Pictures/Prompts.jpg" alt="Picture of the Prompts on the Client-side" width="650" style="border: white solid 1px"></p>
Client Prompts</div>

### Options

1.  **List current events**

    a. Enter 1 at the Options prompt

    b. The current/active events will be listed out

<div align="center"><p><img src="Pictures/CurrentEvents.jpg" alt="Picture of the List current events action result" width="650" style="border: white solid 1px"></p>
List of current Events</div>

2. **Create a new Event**

   a. Enter 2 at the Options prompt

   b. Enter the Title for your event

   c. Enter the Description for your event

   d. Enter the Target amount for your event in the form ####.00

   e. Enter the Deadline of your event in the form YYYY-MM-DDTHH:MM:SSSZ

         e.g. Oct 25, 2023 at 1:45:30PM would be 2023-10-25T13:45:300Z


3.  **Donate to a current event**

    a. Enter 3 at the Options prompt

    b. Enter the number of the event you wish to donate to

    c. Enter the amount you wish to donate in the form ####.00

<div align="center"><p><img src="Pictures/DonateEvent.png" alt="Picture of the list of current events with the selected event highlighted" width="650" style="border: white solid 1px"></p>
Selecting an Event to Donate to</div>


4.  **Update an event**

    a. Enter 4 at the Options prompt

    b. Enter the number of the event you wish to update

    c. Enter the new title of the event or leave blank to keep the current

    d. Enter the new description of the event or leave blank to keep the current

    e. Enter the new target of the event or leave blank to keep the current

    f. Enter the new balance of the event or leave blank to keep the current

<div align="center"><p><img src="Pictures/UpdateEvent.png" alt="Picture of all events being listed with the selected event highlighted" width="650" style="border: white solid 1px"></p>
Selecting an Event to Update</div>

5.  **List all events**

    a. Enter 5 at the Options prompt

    b. The current and past events will be listed out

<div align="center"><p><img src="Pictures/ListAll.jpg" alt="Picture of ALL Events being listed out with some relevant information in a table" width="650" style="border: white solid 1px"></p>
Listing of All Events in a Table format</div>

6.  **Quit Client-side**

    a. Enter q at the Options prompt

    b. The program will end
<div align="center"><p><img src="Pictures/ServerIcon.png" alt="Picture of a server icon" width="150" style="margin-right: 15%; opacity: 30%"><img src="Pictures/ClientIcon.png" alt="" width="150" style="opacity: 30%; margin-bottom: 20px"></p></div>
