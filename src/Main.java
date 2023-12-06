import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
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
    public static void main(String[] args) {
          startServer();

          Client alice = new Client("alice" , IP, PORT);
          Client bob = new Client("bob", IP, PORT);
          Client thomas = new Client("thomas" , IP, PORT);
          Client jeff = new Client("jeff", IP, PORT);

          bump(alice, bob);
          bump(alice, thomas);
          bump(alice, jeff);
          bump(bob, thomas);
          bump(thomas, jeff);


        SwingUtilities.invokeLater(() -> new ClientGUI(alice));
        SwingUtilities.invokeLater(() -> new ClientGUI(bob));
        SwingUtilities.invokeLater(() -> new ClientGUI(thomas));
        SwingUtilities.invokeLater(() -> new ClientGUI(jeff));

    }

    public static void startServer(){
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind("whatsapp", new Server());
        }catch (Exception e){e.printStackTrace();}
    }



    public static void bump(Client a, Client b) {
        a.bump(b);
        b.bump(a);
    }


    }
