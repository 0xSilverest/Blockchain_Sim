package blockchain;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final List<String> namesList = List.of("Tom", "Sara", "John", "Abel");
    private static final List<String> shopsList = List.of("ShoesShop", "FastFood", "CarShop", "GamingShop");
    private static final List<Account> accountsList = new LinkedList<>();

    private static boolean accountContains(String id) {
        for(Account acc : accountsList) {
            if (acc.getAccountId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static Account getFromList(String id) {
        for(Account acc : accountsList) {
            if (acc.getAccountId().equals(id)) {
                return acc;
            }
        }
        return null;
    }

    private static void minerSim (Blockchain blockchain) throws NoSuchAlgorithmException, IOException {
        ExecutorService exec =  Executors.newCachedThreadPool();

        List<Miner> minerList = new ArrayList<>();
        List<String> data = new LinkedList<>();
        List<AccountClient> clientList = new LinkedList<>();
        Block block = null;

        for (int i = 0; i < 8; i++) {
            Miner miner = new Miner(blockchain, i + 1, data);
            minerList.add(miner);

            if(!accountContains("miner"+(i+1)))
                accountsList.add(new Account("miner"+(i+1)));
            AccountClient client = new AccountClient(getFromList("miner"+(i+1)), accountsList, data);
            clientList.add(client);
        }

        for (String name : namesList) {
            if(!accountContains(name))
                accountsList.add(new Account(name));
            AccountClient client = new AccountClient(getFromList(name), accountsList, data);
            clientList.add(client);
        }

        for(String shopName : shopsList) {
            if(!accountContains(shopName))
                accountsList.add(new Account(shopName));
            AccountClient client = new AccountClient(getFromList(shopName), accountsList, data);
            clientList.add(client);
        }

        clientList.forEach(Thread::start);

        try {
            block = exec.invokeAny(minerList);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        exec.shutdownNow();

        assert block != null;
        if(blockchain.validateHash(block.getHash())) {
            clientList.forEach(AccountClient::disconnect);
            minerList.forEach(Miner::notifyMod);
            Objects.requireNonNull(getFromList("miner" + block.getMinerId())).addBalance(100);
            blockchain.pushBlock(block);
        }
    }

    public static void main(String[] args) throws  IOException, NoSuchAlgorithmException{

        Blockchain blockchain = new Blockchain();

        for(int i = 0; i < 5; i++) {
            minerSim(blockchain);
        }

    }
}
