package blockchain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class VerifyMessage {
    private byte[] data;
    private byte[] signature;
    private boolean validation;


    public VerifyMessage(List<byte[]> message, String keyFile) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        data = message.get(0);
        signature = message.get(1);
        validation = verifySignature(keyFile);
    }

    public boolean getValidation() {
        return validation;
    }

    private boolean verifySignature(String keyFile) throws NoSuchAlgorithmException, SignatureException, IOException, InvalidKeyException, InvalidKeySpecException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(getPublic(keyFile));
        sig.update(data);

        return sig.verify(signature);
    }

    private PublicKey getPublic(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(new File(fileName).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
