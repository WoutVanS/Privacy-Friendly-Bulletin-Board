import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static String IP = "localhost";
    public static int PORT = 13000;
    public static void main(String[] args) throws Exception {
          startServer();

          Client alice = makeClient("alice");
          Client bob = makeClient("bob");

          bump(alice, bob);

          alice.send("hey hoe gaat het");
          System.out.println(bob.receive("alice"));


          alice.send("lang geleden dat we elkaar hebben gezien");
          System.out.println(bob.receive("alice"));

        bob.send("ja inderdaad");
        System.out.println(alice.receive("bob"));
    }

    public static void startServer(){
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind("whatsapp", new Server());
        }catch (Exception e){e.printStackTrace();}
    }

    public static Client makeClient(String name) throws Exception {
        Random random = new Random();

        SecretKey secretKey = generateKey(128);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        int index = random.nextInt();
        int tag = random.nextInt();

        return new Client(name, secretKey, sha256, index, tag, IP, PORT);
    }

    public static void bump(Client a, Client b){
        a.bump(b);
        b.bump(a);
    }

    public static SecretKey generateKey(int keySize) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keySize, new SecureRandom());
        return keyGenerator.generateKey();
    }
    }
