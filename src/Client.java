import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client {
    private final User client;
    private List<User> friends;
    private BulletinBoard server;

    private final String ip;
    private final int port;

    public Client(String name, SecretKey secretKey, MessageDigest hash, int index, int tag, String ip, int port){
        client = new User(name, secretKey,hash, index, tag);

        friends = new ArrayList<>();

        this.ip = ip;
        this.port = port;

        startConnection();
    }
    private void startConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            server = (BulletinBoard) registry.lookup("whatsapp");

            if(server == null) System.err.println("server is null");
        } catch (Exception e) {
            System.err.println("Error while starting connection: " + e.getMessage());
        }
    }

    public void bump(Client friend){
        friends.add(friend.getClient());
    }

    public User getClient() {
        return client.cloneUser();
    }

    protected void sendMessage(String messsage) throws Exception {
        Random random = new Random();

        int newIndex = random.nextInt();
        int newTag = random.nextInt();

        messsage += ";:;" +  newIndex + ";:;" + newTag;

        byte [] encryptedMessage = encrypt(messsage, client.secretKey);
        server.add(client.index, encryptedMessage, client.tag);

        client.index = newIndex;
        client.tag = newTag;
        client.hashSecretKey();

    }

    public void send(String message) throws Exception {sendMessage(message);}
    public String receive(String friendName) throws Exception {return receiveMessage(friendName);}

    protected String receiveMessage(String friendName) throws Exception {
        User friend = null;
        String message = "";

        for(User user: friends){
            if(user.name.equals(friendName)){
                friend = user;
                break;
            }
        }

        if(friend != null){

           byte[] encryptedMessage = server.get(friend.index, friend.tag);
           String decryptedMessage = decrypt(encryptedMessage, friend.secretKey);

           String[] splittedMessage = decryptedMessage.split(";:;");
           message = splittedMessage[0];
           friend.index = Integer.parseInt(splittedMessage[1]);
           friend.tag = Integer.parseInt(splittedMessage[2]);

           friend.hashSecretKey();

        }else System.err.println("friend wasn't found");

        return message;
    }

    private static byte[] encrypt(String dataToEncrypt, SecretKey key) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, key);
        return aesCipher.doFinal(dataToEncrypt.getBytes());
    }

    private static String decrypt(byte[] dataToDecrypt, SecretKey key) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = aesCipher.doFinal(dataToDecrypt);
        return new String(decryptedData);
    }

}
