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

# Basic JavaFX Branch Instructions
To run this, you want to run the **Server** first, then *GUI*, and then *GUI2*. I have tested it in both ways, and in my testing, it should not matter which one (of GUI and GUI2) you run first.
  
