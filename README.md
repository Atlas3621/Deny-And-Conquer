# Deny and Conquer - Due August 6

Deny and Conquer is a multiplayer real-time game written in Java using socket programing.

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


  
