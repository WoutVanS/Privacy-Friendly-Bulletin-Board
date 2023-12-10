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

    public void bump(Client friend){
        User user =  friend.getUser(this.name);
        friendsInformation.put(friend.name, user);
    }

    public User getUser(String name){
        User user = makeUser();
        ownInformation.put(name, user);
        assert user != null;
        return user.cloneUser();
    }

    protected Set<String> getFriends(){
        return friendsInformation.keySet();
    }
    protected String getName(){return this.name;}

    protected void sendMessage(String friendName, String messsage){
        User client = ownInformation.get(friendName);

        if(client != null) {
            Random random = new Random();

            int newIndex = random.nextInt();
            int newTag = random.nextInt();

            messsage += ";:;" + newIndex + ";:;" + newTag;

            byte[] encryptedMessage = encrypt(messsage, client.secretKey);

            try {
                server.add(client.index, encryptedMessage, client.hashedTag());
            }catch (Exception e){
                System.err.println("Error while sending message.");
            }

            client.index = newIndex;
            client.tag = newTag;
            client.hashSecretKey();

        }else System.err.println("friend wasn't found");
    }

    protected String receiveMessage(String friendName){
        String message = null;
        User friend = friendsInformation.get(friendName);

        if(friend != null){

           byte[] encryptedMessage = null;
           try{
               encryptedMessage = server.get(friend.index, friend.hashedTag());
           }catch (Exception e){
               System.err.println("Error while receiving message.");
           }

           if(encryptedMessage != null) {
               String decryptedMessage = decrypt(encryptedMessage, friend.secretKey);

               assert decryptedMessage != null;
               String[] splittedMessage = decryptedMessage.split(";:;");
               message = splittedMessage[0];
               friend.index = Integer.parseInt(splittedMessage[1]);
               friend.tag = Integer.parseInt(splittedMessage[2]);

               friend.hashSecretKey();
           }
        }else System.err.println("friend wasn't found");

        return message;
    }

    private byte[] encrypt(String dataToEncrypt, SecretKey key) {
        try {
            Cipher aesCipher = Cipher.getInstance(key.getAlgorithm());
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
            return aesCipher.doFinal(dataToEncrypt.getBytes());
        }catch (Exception e){
           System.err.println("Error during encrypting.");
        }
        return null;
    }

    private String decrypt(byte[] dataToDecrypt, SecretKey key){
        try {
            Cipher aesCipher = Cipher.getInstance(key.getAlgorithm());
            aesCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedData = aesCipher.doFinal(dataToDecrypt);
            return new String(decryptedData);
        }catch (Exception e){
            System.err.println("Error during decrypting.");
        }
        return null;
    }

    private SecretKey generateKey(int keySize){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize, new SecureRandom());
            return keyGenerator.generateKey();
        }catch (Exception e){
            System.err.println("Error during generation of key");
        }
        return null;
    }

    private User makeUser(){
        try{
        Random random = new Random();

        SecretKey secretKey = generateKey(128);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        int index = random.nextInt();
        int tag = random.nextInt();

        return new User(secretKey, sha256, index, tag);
        } catch (Exception e){
            System.err.println("Error while making new User");
        }
        return null;
    }

}
