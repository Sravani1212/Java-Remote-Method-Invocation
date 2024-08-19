Distributed String Array Management Application using Java RMI

# Overview

This project involves the development of a distributed application using Java Remote Method Invocation (RMI) for managing a remote string array. The application enables client-server communication, allowing clients to remotely interact with a shared string array. The project incorporates multithreading to handle multiple client requests concurrently and ensures thread safety using synchronization mechanisms like locks.

# Features

- Remote String Array Management: Clients can perform various operations on a remote string array, such as adding, retrieving, updating, and removing strings.
- Client-Server Communication: Utilizes Java RMI for seamless communication between clients and the server.
- Multithreading: Supports concurrent client requests, allowing multiple clients to interact with the server simultaneously.
- Thread Safety: Ensures safe access to the shared string array using synchronization mechanisms like locks.

# Project Structure

- `src/`
  - `server/`
    - `StringArrayServer.java`: Contains the server implementation that hosts the remote string array.
    - `StringArrayImplementation.java`: Implements the remote methods defined in the `StringArrayInterface`.
  - `client/`
    - `StringArrayClient.java`: Contains the client-side implementation to connect and interact with the remote string array.
  - `common/`
    - `StringArrayInterface.java`: Defines the remote interface that declares methods for managing the string array.
  - `util/`
    - `LockingMechanism.java`: Provides utility methods for locking and synchronization to ensure thread safety.
  
# Prerequisites

- Java Development Kit (JDK): Ensure JDK is installed on your system.
- RMI Registry: The RMI registry must be running to allow clients to locate the remote objects.

# Setup and Execution

1. Compile the Project:
   - Navigate to the `src/` directory.
   - Run the following command to compile all Java files:
     ```sh
     javac server/*.java client/*.java common/*.java util/*.java
     ```

2. Start the RMI Registry:
   - Open a terminal and start the RMI registry using the command:
     ```sh
     rmiregistry
     ```
   - The registry must be running before starting the server.

3. Run the Server:
   - In a new terminal window, navigate to the `src/server/` directory.
   - Start the server by executing:
     ```sh
     java server.StringArrayServer
     ```

4. Run the Client:
   - Open another terminal window and navigate to the `src/client/` directory.
   - Start the client by executing:
     ```sh
     java client.StringArrayClient
     ```
   - The client will connect to the server and provide options to manage the remote string array.

# Usage

Upon running the client, you will be presented with a menu to perform the following operations on the remote string array:

- Add a string: Append a string to the remote array.
- Retrieve a string: Retrieve a string from the array by index.
- Update a string: Update a string at a specific index.
- Remove a string: Remove a string from the array.
- View all strings: Display all strings currently in the array.
