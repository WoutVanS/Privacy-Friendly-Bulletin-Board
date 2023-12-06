import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class User {
    protected SecretKey secretKey;
    protected MessageDigest hash;
    protected int index;
    protected int tag;

    protected User(SecretKey secretKey, MessageDigest hash, int index, int tag){
        this.secretKey = secretKey;
        this.hash = hash;
        this.index = index;
        this.tag = tag;
    }

    protected User cloneUser(){
        return new User(this.secretKey, this.hash, this.index, this.tag);
    }

    protected void hashSecretKey(){

        byte[] byteKey = this.secretKey.getEncoded();
        String algorithm = this.secretKey.getAlgorithm();
        byte[] hashedKey = hash.digest(byteKey);

        this.secretKey = new SecretKeySpec(hashedKey, algorithm);
    }

    protected int hashedTag(){
        byte[] bytes = ByteBuffer.allocate(Integer.BYTES).putInt(tag).array();
        hash.digest(bytes);
        return ByteBuffer.wrap(bytes).getInt();
    }
}


