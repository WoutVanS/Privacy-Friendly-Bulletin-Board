import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoard extends Remote {
    void add(int index, byte[] value, int tag) throws RemoteException;
    byte[] get(int index, int tag) throws RemoteException;
}
