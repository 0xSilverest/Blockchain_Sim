package blockchain;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class Miner implements Callable<Block> {

    private final Blockchain localChain;
    private final int minerId;
    private final Random rand = new Random();
    private final List<String> messageList;
    private boolean modified = false;

    public Miner (Blockchain chain, int minerId, List<String> messageList) {
        localChain = chain.copy();
        this.minerId = minerId;
        this.messageList = messageList;
    }

    public int getMinerId() {
        return minerId;
    }

    private Block mine() {
        String prevBlockHash;

        prevBlockHash = localChain.getLastBlockData().getHash();
        StringBuilder dataBuilder = new StringBuilder();

        if(!messageList.isEmpty()) {
            messageList.forEach(x -> dataBuilder.append(x).append(" "));
        }

        long timeStamp = new Date().getTime();
        long startGen = System.currentTimeMillis();
        int length = (int) Math.pow(10, (int) Math.ceil((double) rand.nextInt(10) * 3 / 2));
        int id = localChain.getLastId();

        long nonce;
        String hash;

        do {
            nonce = rand.nextInt(length);
            hash = StringUtil.applySha256(id + prevBlockHash + dataBuilder.toString() + nonce);
        } while (!localChain.validateHash(hash) && !modified);

        if(modified)
            return null;
        long endGen = System.currentTimeMillis();
        int timeSpent = (int) Math.ceil((double)(endGen - startGen) / 1000);
        return  new Block(minerId, id, timeStamp, prevBlockHash, hash, nonce, messageList, timeSpent);
    }

    public void notifyMod () {
        this.modified = true;
    }

    @Override
    public Block call() {
        return mine();
    }
}
