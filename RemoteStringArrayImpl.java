import java.rmi.*;
//import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RemoteStringArrayImpl implements RemoteStringArray {
    private String[] stringArray;
    private Map<Integer, Lock> lockMap;
    private int capacity;
    private Map<Integer, Integer> rCount;
    private Map<Integer,Integer> wCount;

    public RemoteStringArrayImpl(int n) throws RemoteException {
        super();
        initialize(n);
    }

    public synchronized void initialize(int n) throws RemoteException {
        capacity = n;
        stringArray = new String[capacity];
        lockMap = new HashMap<>();
        rCount = new HashMap<>();
        wCount = new HashMap<>();

        for (int i = 0; i < capacity; i++) {
            stringArray[i] = "";
            lockMap.put(i, new ReentrantReadWriteLock().writeLock());
            rCount.put(i, 0);
            wCount.put(i, 0);
        }
    }

    public synchronized int getArrayCapacity() {
        return capacity;
    }

    public synchronized void insertArrayElement(int l, String str) throws RemoteException {
        if (l < 0 || l >= capacity) {
            throw new IllegalArgumentException("Invalid index: " + l);
        }

        Lock writeLock = lockMap.get(l);

        if (writeLock != null) {
            writeLock.lock();
            try {
                stringArray[l] = str;
            } finally {
                writeLock.unlock();
            }
        }
    }

    public synchronized boolean requestReadLock(int l, int client_id) throws RemoteException {
        if (l < 0 || l >= capacity) {
            throw new IllegalArgumentException("Invalid index: " + l); 
        }

        Lock readLock = lockMap.get(l);

        if (readLock != null) {
            readLock.lock();
            rCount.put(l, 1);
            wCount.put(l, 0);
            return true;
        }

        return false;
    }

    public synchronized boolean requestWriteLock(int l, int client_id) throws RemoteException {
        if (l < 0 || l >= capacity) {
            throw new IllegalArgumentException("Invalid index: " + l);
        }

        Lock writeLock = lockMap.get(l);

        if (writeLock != null) {
            writeLock.lock();
            wCount.put(l, 2);
            rCount.put(l, 0);
            return true;
        }

        return false;
    }

    public synchronized void releaseLock(int l, int client_id) throws RemoteException {
        if (l < 0 || l >= capacity) {
            throw new IllegalArgumentException("Invalid index: " + l);
        }

        Lock lock = lockMap.get(l);
        
        if (wCount.get(l) > 0){
            wCount.put(l, wCount.get(l) - 1);
        }
        else if (rCount.get(l) > 0){
            rCount.put(l, rCount.get(l) - 1);
        }
        try {
        if (lock != null) {
            lock.unlock();
        }
    } 
    finally{
        //pass;
    }

    }

    public synchronized String fetchElementRead(int l, int client_id) throws RemoteException {
        if (l < 0 || l >= capacity) {
            throw new IllegalArgumentException("Invalid index: " + l);
        }

        Lock readLock = lockMap.get(l);

        if (readLock != null) {
            readLock.lock();
            rCount.put(l, 2);
            wCount.put(l, 0);
            try {
                return stringArray[l];
            } finally {
                readLock.unlock();
            }
        }

        return null;
    }

    public synchronized String fetchElementWrite(int l, int client_id) throws RemoteException {
        if (l < 0 || l >= capacity) {
            throw new IllegalArgumentException("Invalid index: " + l);
        }

        Lock writeLock = lockMap.get(l);

        if (writeLock != null) {
            writeLock.lock();
            wCount.put(l, 2);
            rCount.put(l, 0);
            try {
                return stringArray[l];
            } finally {
                writeLock.unlock();
            }
        }

        return null;
    }

    public synchronized boolean writeBackElement(String str, int l, int client_id) throws RemoteException {
        if (l < 0 || l >= capacity) {
            throw new IllegalArgumentException("Invalid index: " + l);
        }

        Lock writeLock = lockMap.get(l);

        if (writeLock != null) {
            writeLock.lock();
            wCount.put(l, 1);
            rCount.put(l, 0);
            try {
                stringArray[l] = str;
                return true;
            } finally {
                writeLock.unlock();
            }
        }

        return false;
    }

    public int getwCount(int l) throws RemoteException{
        return wCount.get(l);
    }
    public int getrCount(int l) throws RemoteException{
        return rCount.get(l);
    }
}
