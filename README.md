# Notes for token_change branch
1. on game end, the winning/draw UI is only displayed on one client,not all


# Deny and Conquer - Due August 6

Deny and Conquer is a multiplayer real-time game written in Java using socket programing.

# Running:
To run outside of IDE:
1. Make sure javafx is installed on your machine - [Guide](https://openjfx.io/openjfx-docs/#install-javafx)

2. Modify Makefile - set JAVAFX_LIB to path to your javafx lib.
e.g.,
```shell
JAVAFX_LIB = /opt/javafx-sdk-20.0.1/lib
```

3. start server:
```shell 
$ make start_server
```

4. start client1:
```shell
$ make gui1
```

5. start client2:
```shell
$ make gui2
```

# July 14 Meeting Notes: 

# Roles: 
Communication: (George & Niall)
- handles client-to-server communication
- message passing

GUI: (Teeya, Benjamin, Joaquin)
- handles game development
- will be done with java swift

# Plans:
- will support 2 to 4 players

Server Side: 
- will receive every stroke made by any user
- will send a message to all other clients when a box is in use (drawing)
- will handle the locking/unlocking (shared object)
- play until all squares are locked
- server will relay the winning results to the clients
- terminate all connections at the end of the game
- will check if the box is over 50% filled
  - if filled, keep the cell locked and fully set colour
  - if not, unlock the box and fully set the colour back to white

Client Side:
- handles the colouring
- set grid size
- on box click, send a lock message
- on box release, send a "check" message, the server will decide if the box is done or not

Others:
- UI for the starting player to create the server
- Remember to comment your code as you go!

# July 25 Meeting Notes
- Project is mostly done, just a matter of splitting remaining duties now.
- Goal is to have code mostly done by friday, hard deadline of next tuesday.
## Token-Related Duties (Teeya, Niall):
- Changing the win logic to allow for more players and allow for a draw state, not necessarily a win. Needs to check when all squares filled not majority.
- Changing the logic with the FillToken to fill when cursor is lifted, rather than auto-filling when majority of square is filled.
## UI-Related Duties (Joaquin):
- Showing the Server IP Address on the main game menu.
- Modifying the win screen to accomodate the logic described above.
## If There's Time (George):
- Programatically setting the size of the grid: changing size with some sort of GameStart Token that sends game options at the start.
## Other Misc. Stuff:
- Report needs to be written after the tuesday deadline, alongside a video for the project as well.

# Basic JavaFX Branch Instructions
To run this, you want to run the **Server** first, then *GUI*, and then *ClientGUI*. I have tested it in both ways, and in my testing, it should not matter which one (of GUI and ClientGUI) you run first.
  
