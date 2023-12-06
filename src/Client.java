import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class Client {
    private final String name;

    private final Map<String, User> ownInformation;
    private final Map<String, User> friendsInformation;
    private BulletinBoard server;

    private final String ip;
    private final int port;

    public Client(String name, String ip, int port){

        ownInformation = new HashMap<>();
        friendsInformation = new HashMap<>();

        this.name = name;
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

    public void bump(Client friend) throws Exception {
        User user =  friend.getUser(this.name);
        friendsInformation.put(friend.name, user);
    }

    public User getUser(String name) throws Exception {
        User user = makeUser();
        ownInformation.put(name, user);
        return user.cloneUser();
    }


    public void send(String friendName, String message) throws Exception {sendMessage(friendName, message);}
    public String receive(String friendName) throws Exception {return receiveMessage(friendName);}

    protected void sendMessage(String friendName, String messsage) throws Exception {
        User client = ownInformation.get(friendName);

        if(client != null) {
            Random random = new Random();

            int newIndex = random.nextInt();
            int newTag = random.nextInt();

            messsage += ";:;" + newIndex + ";:;" + newTag;

            byte[] encryptedMessage = encrypt(messsage, client.secretKey);
            server.add(client.index, encryptedMessage, client.hashedTag());

            client.index = newIndex;
            client.tag = newTag;
            client.hashSecretKey();

        }else System.err.println("friend wasn't found");
    }

    protected String receiveMessage(String friendName) throws Exception {
        String message = "";
        User friend = friendsInformation.get(friendName);

        if(friend != null){

           byte[] encryptedMessage = server.get(friend.index, friend.hashedTag());
           String decryptedMessage = decrypt(encryptedMessage, friend.secretKey);

           String[] splittedMessage = decryptedMessage.split(";:;");
           message = splittedMessage[0];
           friend.index = Integer.parseInt(splittedMessage[1]);
           friend.tag = Integer.parseInt(splittedMessage[2]);

           friend.hashSecretKey();

        }else System.err.println("friend wasn't found");

        return message;
    }

    private byte[] encrypt(String dataToEncrypt, SecretKey key) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, key);
        return aesCipher.doFinal(dataToEncrypt.getBytes());
    }

    private String decrypt(byte[] dataToDecrypt, SecretKey key) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = aesCipher.doFinal(dataToDecrypt);
        return new String(decryptedData);
    }

    private SecretKey generateKey(int keySize) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keySize, new SecureRandom());
        return keyGenerator.generateKey();
    }

    private User makeUser() throws Exception {
        Random random = new Random();

        SecretKey secretKey = generateKey(128);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        int index = random.nextInt();
        int tag = random.nextInt();

        return new User(secretKey, sha256, index, tag);
    }

}
