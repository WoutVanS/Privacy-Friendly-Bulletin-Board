import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class Server extends UnicastRemoteObject implements BulletinBoard{
    //private final ConcurrentHashMap <Integer, Map<Integer, byte[]>> bulletinBoard;
    private final ConcurrentLinkedDeque<ConcurrentHashMap <Integer, Map<Integer, byte[]>>> bulletinBoards;

    private static final int BOARD_THRESHOLD = 4;

    public Server() throws RemoteException {
        //bulletinBoard = new ConcurrentHashMap<>();
        bulletinBoards = new ConcurrentLinkedDeque<>();
        bulletinBoards.add(new ConcurrentHashMap<>());

    }

    @Override
    public void add(int index, byte[] value, int tag) throws RemoteException {
        if(!bulletinBoards.getLast().containsKey(index) || bulletinBoards.getLast().get(index) == null){
            Map<Integer, byte[]> tmp = new HashMap<>();
            tmp.put(tag, value);
            bulletinBoards.getLast().put(index, tmp);

        }else{
            bulletinBoards.getLast().get(index).put(tag, value);
        }
        if (bulletinBoards.getLast().size() >= BOARD_THRESHOLD) {
            createNewBulletinBoard();
        }

        removeEmptyBulletinBoards();
    }


    @Override
    public byte[] get(int index, int tag) throws RemoteException {
        for (ConcurrentHashMap<Integer, Map<Integer, byte[]>> bulletinBoard : bulletinBoards) {
            if (bulletinBoard.containsKey(index) && bulletinBoard.get(index).containsKey(tag))
                return bulletinBoard.remove(index).remove(tag);
        }
        return null;
    }

    private void removeEmptyBulletinBoards() {
        while (bulletinBoards.getFirst().isEmpty()) {
            bulletinBoards.removeFirst();
        }
    }

    private void createNewBulletinBoard() {
        ConcurrentHashMap<Integer, Map<Integer, byte[]>> newBulletinBoard = new ConcurrentHashMap<>();
        bulletinBoards.add(newBulletinBoard);
    }
}
