package blockchain;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

class Block {
    private final int minerId;
    private final int id;
    private final long timeStamp;
    private final String prevBlockHash;
    private final String hash;
    private final long nonce;
    private final List<Message> data;
    private final int timeSpent;

    /*public Block(int id, String prevHash) {
        this.id = id;
        timeStamp = new Date().getTime();
        prevBlockHash = prevHash;
        hash = hashGenerator();
    }*/

    public Block(int minerId, int id, long timeStamp, String prevHash, String hash, long nonce, List<Message> data, int timeSpent) {
        this.minerId = minerId;
        this.id = id;
        this.timeStamp = timeStamp;
        this.prevBlockHash = prevHash;
        this.nonce = nonce;
        this.hash = hash;
        this.data = data;
        this.timeSpent = timeSpent;
    }

    public String getHash() {
        return hash;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    /*private String hashGenerator () {
        long startGen = System.currentTimeMillis();
        Random rand = new Random();
        int length = (int) Math.pow(10, (int) Math.ceil(rand.nextInt(20) * 3 / 2));
        String hash;
        do {
            nonce = rand.nextInt(length);
            hash = StringUtil.applySha256(id + prevBlockHash + nonce);
        } while (!validateHash(hash));
        long endGen = System.currentTimeMillis();
        timeSpent = (int) Math.ceil((endGen - startGen)/1000);
        return hash;
    }*/

    @Override
    public String toString() {
        StringBuilder messages = new StringBuilder();
        if (data.isEmpty())
            messages.append("no messages\n");
        else {
            data.forEach(x -> messages.append(x.toString()).append("\n"));
        }

        return "Block: \n" + "Created by miner # " + minerId + "\n" +
                "Id: " + id + "\n" +
                "Timestamp: " + timeStamp + "\n" +
                "Magic number: " + nonce + "\n" +
                "Hash of the previous block: \n" + prevBlockHash + "\n" +
                "Hash of the block: \n" + hash + "\n" +
                "Block data:\n" + messages.toString() +
                "Block was generating for " + timeSpent + " seconds";

    }
}

public class Blockchain {
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

    /*public void createNewBlock () {
        Block b;
        if (chain.size() == 0) {
            b = new Block(lastId ++, "0");
        } else {
            b = new Block(lastId++, chain.getLast().getHash());
        }

        chain.add(b);
        System.out.println(chain.getLast());
        setDiff(b.getTimeSpent());
    }*/

    public synchronized void pushBlock (Block b) {
        chain.add(b);
        System.out.println(b);
        lastId ++;
        setDiff(b.getTimeSpent());
    }


    public Block getLastBlockData () {
        return chain.getLast();
    }

    private void setDiff(int time) {
        if(time <= 10) {
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

    public Blockchain load(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Blockchain loadedChain = (Blockchain) ois.readObject();
            ois.close();
            fis.close();
            return loadedChain;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validateHash(String hash) {
        return hash.matches("0{" + validL + "}[a-bA-B1-9].{" + (64 - validL - 1) + "}");
    }

    public int getLastId() {
        return lastId;
    }

    public boolean isEmpty() {
        return chain.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();

        chain.forEach(strBuilder::append);

        return strBuilder.toString();
    }
}
