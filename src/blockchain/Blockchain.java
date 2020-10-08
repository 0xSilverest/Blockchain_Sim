package blockchain;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;
import java.util.List;

class Block implements Serializable {
    private static final long serialVersionUID = 1337L;
    private final int minerId;
    private final int id;
    private final long timeStamp;
    private final String prevBlockHash;
    private final String hash;
    private final long nonce;
    private List<String> data = List.of("No transactions");
    private final int timeSpent;

    public Block(int minerId, int id, long timeStamp, String prevHash, String hash, long nonce, List<String> data, int timeSpent) {
        this.minerId = minerId;
        this.id = id;
        this.timeStamp = timeStamp;
        this.prevBlockHash = prevHash;
        this.nonce = nonce;
        this.hash = hash;
        if(!data.isEmpty())
            this.data = data;
        this.timeSpent = timeSpent;
    }

    public void modifyData (List<String> data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public int getMinerId() {
        return minerId;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    @Override
    public String toString() {
        StringBuilder messages = new StringBuilder();
        if(data == null) {
            messages.append("No transactions");
        } else
            data.forEach(x -> {if(x != null) messages.append(x).append("\n");});

        return "Block: \n" + "Created by miner" + minerId + "\n" +
                "miner" + minerId + " gets 100 VC\n" +
                "Id: " + id + "\n" +
                "Timestamp: " + timeStamp + "\n" +
                "Magic number: " + nonce + "\n" +
                "Hash of the previous block: \n" + prevBlockHash + "\n" +
                "Hash of the block: \n" + hash + "\n" +
                "Block data:\n" + messages +
                "Block was generating for " + timeSpent + " seconds";

    }
}

public class Blockchain implements Serializable {
    private static final long serialVersionUID = 1337L;
    private int validL;
    private int lastId;
    private final LinkedList<Block> chain;

    public Blockchain() {
        chain = new LinkedList<>();
        validL = 0;
        lastId = 1;
    }

    private Blockchain (LinkedList<Block> chain, int lastId, int validL) {
        this.validL = validL;
        this.lastId = lastId;
        this.chain = chain;
    }

    public Blockchain copy() {
        return new Blockchain(this.chain, this.lastId, this.validL);
    }

    public synchronized void pushBlock (Block b) {
        chain.add(b);
        System.out.println(b);
        lastId ++;
        setDiff(b.getTimeSpent());
    }


    public Block getLastBlockData () {
        if (chain.size() == 0)
            return new Block(0, 0, 0, "0", "0", 0, List.of("0"), 0);
        return chain.getLast();
    }

    private void setDiff(int time) {
        if(time < 20) {
            validL ++;
            System.out.println("N was increased to " + validL + "\n");
            return;
        } else if (time >= 60) {
            validL --;
            System.out.println("N was decreased by 1 \n");
            return;
        }
        System.out.println("N stays the same\n");
    }

    public Block get(int i) {
        return chain.get(i);
    }

    public boolean validateMessage(List<byte[]> message, Client sender, String s) throws NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException, IOException {
        return new VerifyMessage(message, sender.clientName + "PublicKey").getValidation();
    }

    public boolean validateMessage(List<byte[]> message, AccountClient sender) throws NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException, IOException {
        return new VerifyMessage(message, sender.getAccountId() + "PublicKey").getValidation();
    }

    public boolean validateHash(String hash) {
        return hash.matches("0{" + validL + "}[a-bA-B1-9].{" + (64 - validL - 1) + "}");
    }

    public int getLastId() {
        return lastId;
    }

    public boolean isEmpty() {
        return chain.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();

        chain.forEach(strBuilder::append);

        return strBuilder.toString();
    }
}
