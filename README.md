Notes for token_change branch

Modified Files: 
Board.java
- changed winnerExists() so that we return true if board is fully filled, false if not
- added isATie(), will check whether or not a draw has occured
- changed colorOfWinner() so that now we find the winner based of who has the most
  squares filled. Note: will only call this function if isATie() is false.
  Ie. There is a single winner.

Server.java
- in the -- if (outStr.startsWith("DRAW")) -- block, We will check if a winner exists.
  If a winner exists (ie. board is fully filled), you will check if the game has ended 
  in a tie or not.

  if we ended in a tie, send a "TIE" message to clients to inform them and allow them
  to make the necessary UI changes to accomadate a tie

  if we did not end in a tie, continue with the same logic we had before where there 
  was just a single winner. 

Client.java
- added a new if condition in -- while ((inStr = in.readLine()) != null) --
  called --> if (inStr.startsWith("TIE"))
  clients now can recieve a "TIE" message from the server, allowing clients to modify 
  their UI to reflect the event of a tie.

Note: winnerExists() work for one player

Issues: 
1.  winnerExists() doesn't work for multiplayer.
    Game will still terminate when >50% of the board is taken
    Players' GUI are out of sync after this termiation ^. 
    (ie. one user will have a finished screen, the other's will be frozen on the game board)
2. In the out of sync termination (mentioned above), the UI layout is incorrect


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
- will send a message to all other clients that the box is in use (drawing)
- locking/unlocking (shared object)
- play until all squares all locked
- server will decide on a winner
- terminate all connections at the game
- checks if the box is filled over 50%
  - if filled, keep lock and fully set colour
  - if not, unlock box and fully set to white

Client Side:
- handles the colouring
- set grid size
- on box click, send a lock message
- on box release, send a "check" message, the server will decide if the box is done or not

Others
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
  
