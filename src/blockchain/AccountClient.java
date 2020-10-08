package blockchain;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Random;

public class AccountClient extends Thread{
    private final Account acc;
    private final List<Account> accountList;
    private final Random rand = new Random();
    private final List<String> dataStream;
    private boolean stop = false;
    private final Blockchain localChain;

    public AccountClient(Account acc, Blockchain localChain, List<Account> accountList, List<String> dataStream) throws IOException, NoSuchAlgorithmException {
        this.acc = acc;
        this.localChain = localChain.copy();
        this.accountList = accountList;
        this.dataStream = dataStream;
        generateClientKeys();
    }

    private void generateClientKeys () throws IOException, NoSuchAlgorithmException {
        if(!new File(acc.getAccountId() + "PublicKey").exists() || !new File(acc.getAccountId() + "PrivateKey").exists()) {
            GenerateKeys clientKeys = new GenerateKeys(1024);
            clientKeys.createKeys();
            clientKeys.writeToFile(acc.getAccountId() + "PublicKey", clientKeys.getPublicKey().getEncoded());
            clientKeys.writeToFile(acc.getAccountId() + "PrivateKey", clientKeys.getPrivateKey().getEncoded());
        }
    }

    public String getAccountId() {
        return acc.getAccountId();
    }

    private String doTransaction (Account destination, long amount) {
        acc.sendMoneyTo(destination, amount);
        return acc.getAccountId() + " sent " + amount + " VC to " + destination.getAccountId();
    }

    private List<byte[]> signTransaction(String message) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return new SignMessage(message, acc.getAccountId() + "PrivateKey").getBytesList();
    }

    public void disconnect() {
        this.stop = true;
    }

    public List<String> getDataStream() {
        return dataStream;
    }

    @Override
    public void run() {
        while (!stop && acc.getBalance() > 0) {
            int n = rand.nextInt(accountList.size());
            Account dest = accountList.get(n);
            int amount = rand.nextInt((int) acc.getBalance());
            try {
                if(!dest.equals(acc)
                        && amount != 0
                        && amount <= acc.getBalance()) {
                    if (localChain.validateMessage(signTransaction(String.valueOf(amount)), this)) {
                        synchronized (dataStream) {
                            dataStream.add(doTransaction(dest, amount));
                        }
                    } else {
                        System.out.println(getAccountId() + " Error: Key unrecognized");
                    }
                }
            } catch (InvalidKeySpecException | IOException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                e.printStackTrace();
            }

            try {
                sleep(rand.nextInt(1500) + 250);
            } catch (InterruptedException ignored) {}
        }
    }
}
