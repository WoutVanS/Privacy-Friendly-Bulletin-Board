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

          Client alice = new Client("alice" , IP, PORT);
          Client bob = new Client("bob", IP, PORT);

          bump(alice, bob);

          alice.send("bob","hey hoe gaat het");
          System.out.println(bob.receive("alice"));


          alice.send("bob","lang geleden dat we elkaar hebben gezien");
          System.out.println(bob.receive("alice"));

        bob.send("alice","ja inderdaad");
        System.out.println(alice.receive("bob"));
    }

    public static void startServer(){
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind("whatsapp", new Server());
        }catch (Exception e){e.printStackTrace();}
    }



    public static void bump(Client a, Client b) throws Exception {
        a.bump(b);
        b.bump(a);
    }


    }
