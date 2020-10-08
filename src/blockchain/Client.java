package blockchain;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Random;

public class Client extends Thread{

    final String clientName;
    private final List<Message> dataStream;
    private final Random rand = new Random();
    private final Blockchain localCopy;
    private boolean stop = false;

    public Client(String name, List<Message> dataStream, Blockchain chain) throws IOException, NoSuchAlgorithmException {
        clientName = name;
        this.dataStream = dataStream;
        localCopy = chain.copy();
        generateClientKeys();
    }

    private void generateClientKeys () throws IOException, NoSuchAlgorithmException {
       if(!new File(clientName + "PublicKey").exists() || !new File(clientName + "PrivateKey").exists()) {
            GenerateKeys clientKeys = new GenerateKeys(1024);
            clientKeys.createKeys();
            clientKeys.writeToFile(clientName + "PublicKey", clientKeys.getPublicKey().getEncoded());
            clientKeys.writeToFile(clientName + "PrivateKey", clientKeys.getPrivateKey().getEncoded());
       }
    }

    private Message sendMessage() {
        return new Message(clientName, StringUtil.GenerateRandomMessage());
    }

    private List<byte[]> signMessage(String message) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
            return new SignMessage(message, clientName + "PrivateKey").getBytesList();
    }

    public void disconnect() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            if (rand.nextBoolean()) {
                Message clientMess = sendMessage();

                try {
                    if(localCopy.validateMessage(signMessage(clientMess.message), this, this.clientName + "PublicKey")) {
                        synchronized (dataStream) {
                            dataStream.add(clientMess);
                        }
                    } else {
                        System.out.println("Error: Key Unrecognized");
                    }
                } catch (NoSuchAlgorithmException | SignatureException | InvalidKeySpecException | InvalidKeyException | IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                this.sleep(rand.nextInt(500));
            } catch (InterruptedException ignored) {}
        }
    }
}
