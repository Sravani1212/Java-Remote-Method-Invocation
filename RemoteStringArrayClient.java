import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
//import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

//import javax.swing.text.html.parser.Element;

public class RemoteStringArrayClient {
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: RemoteStringArrayClient <config_file>");
            System.exit(1);
        }

        String configFile = args[0];
        String serverHost = "";
        String bindName = "";
        int client_id = 1;

        Map<Integer,Integer> tempr = new HashMap<Integer, Integer>();
        Map<Integer,Integer> tempw = new HashMap<Integer, Integer>();

        int rCount = 0, wCount = 0;

        Map<Integer, String> elementRead = new HashMap<Integer, String> ();
        Map<Integer, String> elementWrite = new HashMap<Integer, String>();


        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            serverHost = reader.readLine();
            bindName = reader.readLine();
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading the configuration file: " + e.getMessage());
            System.exit(1);
        }

        try {
            // Construct the RMI URL for the server
            String serverURL = "rmi://" + serverHost + "/" + bindName;

            // Look up the remote object by its RMI URL
            RemoteStringArray remoteArray = (RemoteStringArray) Naming.lookup(serverURL);

            // Initialize a scanner to read user input
            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Display menu options to the user
                System.out.println("Choose an option:");
                System.out.println("1. Get_Array_Capacity");
                System.out.println("2. Fetch_Element_Read <i>");
                System.out.println("3. Fetch_Element_Write <i>");
                System.out.println("4. Print_Element <i>");
                System.out.println("5. Concatenate <i> <Str>");
                System.out.println("6. Writeback <i>");
                System.out.println("7. Release_Lock <i>");
                System.out.println("8. Exit");
                //System.out.println(tempr);



                // Read user input
                String userInput = scanner.nextLine();
                String[] commandParts = userInput.split(" ");

                int index;
                switch (commandParts[0]) {
                    case "1":
                        // Get_Array_Capacity
                        try {
                            int capacity = remoteArray.getArrayCapacity();
                            System.out.println("Capacity: " + capacity);
                            for(int i =0; i < capacity; i++){
                                elementRead.put(i,"");
                                elementWrite.put(i,"");
                                tempr.put(i, 0);
                                tempw.put(i,0);
                            }
                        } catch (RemoteException e) {
                            System.err.println("Remote exception: " + e.getMessage());
                        }
                        break;
                    case "2":
                        // Fetch_Element_Read <i>
                        if (commandParts.length < 2) {
                            System.out.println("Invalid command. Usage: Fetch_Element_Read <i>");
                            break;
                        }
                    try{
                        index = Integer.parseInt(commandParts[1]);
                        rCount = remoteArray.getrCount(index);
                        wCount = remoteArray.getwCount(index);

                        if ( wCount == 0 || tempr.get(index) == 1 || tempw.get(index) == 1) {
                        try {
                            boolean readSuccess = remoteArray.requestReadLock(index, client_id);
                            if (readSuccess) {
                                System.out.println("Read Success");
                                elementRead.put(index,remoteArray.fetchElementRead(index, client_id));
                                remoteArray.releaseLock(index, client_id); // Release the lock after reading
                                rCount = remoteArray.getrCount(index);
                                wCount = remoteArray.getwCount(index);
                                tempr.put(index,1);
                                tempw.put(index, 0);
                                
                            } else {
                                System.out.println("Read Failure: Unable to obtain read lock.");
                            }
                        } catch (RemoteException e) {
                            System.err.println("Remote exception: " + e.getMessage());
                        }
                    }
                    else {
                        System.out.println("Other client has the Read/Write lock");
                    }
                }
                catch (Exception e) {
                    System.err.println("Other user has Write lock Read lock cannot be obtained");
                }
                        break;
                    case "3":
                        // Fetch_Element_Write <i>
                        if (commandParts.length < 2) {
                            System.out.println("Invalid command. Usage: Fetch_Element_Write <i>");
                            break;
                        }
                        try{
                        index = Integer.parseInt(commandParts[1]);
                        rCount = remoteArray.getrCount(index);
                        wCount = remoteArray.getwCount(index);
                        if (wCount == 0 || tempw.get(index) == 1){
                        try {
                            boolean writeSuccess = remoteArray.requestWriteLock(index, client_id);
                            if (writeSuccess) {
                                String temp = remoteArray.fetchElementWrite(index, client_id);
                                elementWrite.put(index,temp);
                                System.out.println("Write Success ");
                                remoteArray.releaseLock(index, client_id); // Release the lock after reading
                                rCount = remoteArray.getrCount(index);
                                wCount = remoteArray.getwCount(index);
                                tempw.put(index, 1);
                                tempr.put(index, 1);
                            } else {
                                System.out.println("Write Failure: Unable to obtain write lock.");
                            }
                        } catch (RemoteException e) {
                            System.err.println("Remote exception: " + e.getMessage());
                        }
                        }
                        else {
                            System.out.println("Other client has the Read/Write lock");
                        }
                        }
                        catch (Exception e){
                            System.err.println("Lock already in use");
                        }
                        break;
                        case "4":
                        // Print_Element <i>
                        if (commandParts.length < 2) {
                            System.out.println("Invalid command. Usage: Print_Element <i>");
                            break;
                        }
                    try{
                        index = Integer.parseInt(commandParts[1]);
                        rCount = remoteArray.getrCount(index);
                        wCount = remoteArray.getwCount(index);
                        if (rCount == 1 || wCount == 1 || tempr.get(index) == 1){
                        try {
                            //boolean readSuccess = remoteArray.requestReadLock(index, client_id);
                            if (rCount == 1) {
                                System.out.println("Printed Element: " + elementRead.get(index));
                            } else {
                                System.out.println("Print Failure: Unable to obtain read lock.");
                            }
                        } catch (Exception e) {
                            System.err.println("Remote exception: " + e.getMessage());
                        }
                        }
                        else {
                            System.out.println("you dont have read/write lock");
                        }
                    }
                    catch(Exception e) {
                        System.err.println("Other user has write lock so Read cannot be obtained");
                    }
                        break;
                    case "5":
                        // Concatenate <i> <Str>
                        if (commandParts.length < 3) {
                            System.out.println("Invalid command. Usage: Concatenate <i> <Str>");
                            break;
                        }
                        index = Integer.parseInt(commandParts[1]);
                        String strToConcatenate = commandParts[2];
                        rCount = remoteArray.getrCount(index);
                        wCount = remoteArray.getwCount(index);
                        try {
                            //boolean writeSuccess = remoteArray.requestWriteLock(index, client_id);
                            if (wCount == 1 || tempw.get(index) == 1) {
                                String element = remoteArray.fetchElementWrite(index, client_id);
                                element += strToConcatenate;
                                //remoteArray.releaseLock(index, client_id); 
                                //remoteArray.writeBackElement(element, index, client_id);
                                elementWrite.put(index,element);
                                System.out.println("Concatenation Success: " + element);
                                rCount = remoteArray.getrCount(index);
                                wCount = remoteArray.getwCount(index);
                                tempw.put(index, 1);
                                tempr.put(index, 1);
                                // Release the lock after writing
                            } else {
                                System.out.println("Concatenation Failure: Unable to obtain write lock.");
                            }
                        } catch (RemoteException e) {
                            System.err.println("Remote exception: " + e.getMessage());
                        }
                        break;
                    case "6":
                        // Writeback <i>
                        if (commandParts.length < 2) {
                            System.out.println("Invalid command. Usage: Writeback <i>");
                            break;
                        }
                        index = Integer.parseInt(commandParts[1]);
                        rCount = remoteArray.getrCount(index);
                        wCount = remoteArray.getwCount(index);
                        if (wCount == 1 || tempw.get(index) == 1 ){
                        try {
                            if (tempw.get(index) == 1) {
                                boolean writebackSuccess = remoteArray.writeBackElement(elementWrite.get(index), index, client_id);
                                if (writebackSuccess) {
                                    System.out.println("Writeback Success");
                                } else {
                                    System.out.println("Writeback Failure: Unable to write back the element.");
                                }
                                //remoteArray.releaseLock(index, client_id); // Release the lock after writing
                            } else {
                                System.out.println("Writeback Failure: Unable to obtain write lock.");
                            }
                        } catch (RemoteException e) {
                            System.err.println("Remote exception: " + e.getMessage());
                        }
                    }
                    else {
                         System.out.println("you dont have read/write lock");
                    }
                        break;
                    case "7":
                        // Release_Lock <i>
                        if (commandParts.length < 2) {
                            System.out.println("Invalid command. Usage: Release_Lock <i>");
                            break;
                        }
                        index = Integer.parseInt(commandParts[1]);
                        try {
                            if(tempr.get(index) == 1) {
                                tempr.put(index, 0);
                            }
                            else if (tempw.get(index) == 1){
                                tempw.put(index,0);
                            }
                            System.out.println("Lock Released for Element ");
                            remoteArray.releaseLock(index, client_id);
                        } catch (Exception e) {
                            //continue;
                            continue;
                        }
                        break;
                    case "8":
                        // Exit
                        System.out.println("Exiting client program.");
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid command.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
