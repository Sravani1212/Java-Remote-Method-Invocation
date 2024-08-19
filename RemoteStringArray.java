import java.rmi.Remote; 
import java.rmi.RemoteException;

public interface RemoteStringArray extends Remote{
     
    void initialize(int n) throws RemoteException;

    void insertArrayElement(int l, String str) throws RemoteException;

    boolean requestReadLock(int l, int client_id) throws RemoteException;

    boolean requestWriteLock(int l, int client_id) throws RemoteException;

    void releaseLock(int l, int client_id) throws RemoteException;

    String fetchElementRead(int l, int client_id) throws RemoteException;

    String fetchElementWrite(int l, int client_id) throws RemoteException;

    boolean writeBackElement(String str, int l, int client_id) throws RemoteException;

    int getArrayCapacity() throws RemoteException;

    public int getwCount(int l) throws RemoteException;

    public int getrCount(int l) throws RemoteException;

}
