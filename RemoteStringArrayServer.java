import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RemoteStringArrayServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: RemoteStringArrayServer <config_file>");
            System.exit(1);
        }

        String configFile = args[0];
        String bindName = "";
        int capacity = 0;
        List<String> initialStrings = new ArrayList<>();

        try {
            // Read the configuration file to get the bind name, capacity, and initial strings
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            bindName = reader.readLine();
            capacity = Integer.parseInt(reader.readLine());

            // Read the list of strings needed to initialize the array
            for (int i = 0; i < capacity; i++) {
                String line = reader.readLine();
                if (line != null) {
                    initialStrings.add(line);
                } else {
                    System.err.println("Insufficient strings in the configuration file.");
                    reader.close();
                    System.exit(1);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading the configuration file: " + e.getMessage());
            System.exit(1);
        }

        try {
            // Create the remote object instance and initialize the array
            RemoteStringArrayImpl remoteArray = new RemoteStringArrayImpl(capacity);
            for (int i = 0; i < capacity; i++) {
                remoteArray.insertArrayElement(i, initialStrings.get(i));
            }

            // Create and export the remote object
            RemoteStringArray stub = (RemoteStringArray) UnicastRemoteObject.exportObject(remoteArray, 0);

            // Get the RMI Registry
            Registry registry = LocateRegistry.getRegistry();

            // Bind the remote object to the registry with the specified name
            registry.rebind(bindName, stub);

            System.out.println("RemoteStringArrayServer is running...");
        } catch (Exception e) {
            System.err.println("RemoteStringArrayServer exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
