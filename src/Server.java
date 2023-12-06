import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends UnicastRemoteObject implements BulletinBoard{
    private final ConcurrentHashMap <Integer, Map<Integer, byte[]>> bulletinBoard;

    public Server() throws RemoteException {
        bulletinBoard = new ConcurrentHashMap<>();
    }

    @Override
    public void add(int index, byte[] value, int tag) throws RemoteException {
        if(!bulletinBoard.containsKey(index) || bulletinBoard.get(index) == null){
            Map<Integer, byte[]> tmp = new HashMap<>();
            tmp.put(tag, value);
            bulletinBoard.put(index, tmp);

        }else{
            bulletinBoard.get(index).put(tag, value);
        }
    }

    @Override
    public byte[] get(int index, int tag) throws RemoteException {
        if(bulletinBoard.containsKey(index) && bulletinBoard.get(index).containsKey(tag))
            return bulletinBoard.get(index).remove(tag);
        return null;
    }
}
