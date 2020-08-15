package blockchain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final List<String> namesList = List.of("Tom", "Sara", "John", "Abel");

    private static void minerSim (Blockchain blockchain) {
        ExecutorService exec =  Executors.newCachedThreadPool();

        List<Miner> minerList = new ArrayList<>();
        List<Message> data = new LinkedList<>();
        List<Client> clientList = new LinkedList<>();
        Block block = null;



        for (int i = 0; i < 8; i++) {
            Miner miner = new Miner(blockchain, i + 1, data);
            minerList.add(miner);
        }

        if(!blockchain.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                Client client = new Client(namesList.get(i), data);
                clientList.add(client);
            }

            clientList.forEach(Thread::start);
        }

        try {
            block = exec.invokeAny(minerList);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        exec.shutdownNow();

        assert block != null;
        if(blockchain.validateHash(block.getHash())) {
            blockchain.pushBlock(block);
        }

        minerList.forEach(miner -> miner.notifyMod());

        clientList.forEach(Client::disconnect);
    }

    public static void main(String[] args) {

        Blockchain blockchain = new Blockchain();

        for(int i = 0; i < 5; i++) {
            minerSim(blockchain);
        }

    }
}
