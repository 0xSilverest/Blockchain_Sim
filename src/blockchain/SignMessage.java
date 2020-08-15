package blockchain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class SignMessage {

    List<byte[]> list;

    public SignMessage (String data, String keyFile) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException, InvalidKeySpecException {
        list = new ArrayList<>();
        list.add(data.getBytes());
        list.add(sign(data, keyFile));
    }

    private byte[] sign(String data, String keyFile) throws NoSuchAlgorithmException, SignatureException, IOException, InvalidKeyException, InvalidKeySpecException {
        Signature dsa = Signature.getInstance("SHA1withRSA");
        dsa.initSign(getPrivate(keyFile));
        dsa.update(data.getBytes());
        return dsa.sign();
    }

    public PrivateKey getPrivate(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(new File(fileName).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public List<byte[]> getBytesList() {
        return list;
    }
}
