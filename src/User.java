import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class User {
    protected String name;
    protected SecretKey secretKey;

    protected MessageDigest hash;
    protected int index;
    protected int tag;

    public User(String name, SecretKey secretKey, MessageDigest hash, int index, int tag){
        this.name = name;
        this.secretKey = secretKey;
        this.hash = hash;
        this.index = index;
        this.tag = tag;
    }

    public User cloneUser(){
        return new User(this.name, this.secretKey, this.hash, this.index, this.tag);
    }

    public void hashSecretKey(){

        byte[] byteKey = this.secretKey.getEncoded();
        String algorithm = this.secretKey.getAlgorithm();
        byte[] hashedKey = hash.digest(byteKey);

        this.secretKey = new SecretKeySpec(hashedKey, algorithm);
    }
}


