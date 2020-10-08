package blockchain;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {

    private static boolean accountContains(String id, LinkedList<Account> accountsList) {
        for(Account acc : accountsList) {
            if (acc.getAccountId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static Account getFromList(String id, LinkedList<Account> accountsList) {
        for(Account acc : accountsList) {
            if (acc.getAccountId().equals(id)) {
                return acc;
            }
        }
        return null;
    }

    private static void minerSim (Blockchain blockchain, LinkedList<Account> accountsList, List<String> namesList, List<String> shopsList) throws IOException, NoSuchAlgorithmException {
        ExecutorService exec =  Executors.newCachedThreadPool();

        List<Miner> minerList = new ArrayList<>();
        List<String> data = new LinkedList<>();
        List<AccountClient> clientList = new LinkedList<>();
        Block block = null;

        for (int i = 0; i < 8; i++) {
            Miner miner = new Miner(blockchain, i + 1, data);
            minerList.add(miner);

            if(!accountContains("miner"+(i+1), accountsList))
                accountsList.add(new Account("miner"+(i+1)));
            AccountClient client = new AccountClient(getFromList("miner"+(i+1), accountsList), blockchain, accountsList, data);
            clientList.add(client);
        }

        for (String name : namesList) {
            if(!accountContains(name, accountsList))
                accountsList.add(new Account(name));
            AccountClient client = new AccountClient(getFromList(name, accountsList), blockchain, accountsList, data);
            clientList.add(client);
        }

        for(String shopName : shopsList) {
            if(!accountContains(shopName, accountsList))
                accountsList.add(new Account(shopName));
            AccountClient client = new AccountClient(getFromList(shopName, accountsList), blockchain, accountsList, data);
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

            clientList.forEach(x -> {
                for(String str: x.getDataStream()) {
                    if (!data.contains(str)) data.add(str);
                }
            });

            while (!clientList.isEmpty()) {
                clientList = clientList.stream().filter(AccountClient::isAlive).collect(Collectors.toList());
                clientList.forEach(AccountClient::interrupt);
            }

            if(!data.isEmpty())
                block.modifyData(data);

            Objects.requireNonNull(getFromList("miner" + block.getMinerId(), accountsList)).addBalance(100);
            blockchain.pushBlock(block);

            SerialUtils.serialize(blockchain, "Blockchain.txt");
            SerialUtils.serialize(accountsList, "AccountsList.txt");
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        Blockchain blockchain = new Blockchain();
        LinkedList<Account> accountsList = new LinkedList<>();

        final List<String> namesList = List.of("Tom", "Sara", "John", "Abel");
        final List<String> shopsList = List.of("ShoesShop", "FastFood", "CarShop", "GamingShop");

        final String fileName = "Blockchain.txt";
        if(new File(fileName).exists()) {
            blockchain = (Blockchain) SerialUtils.deserialize(fileName);
        }

        final String accountFile = "AccountsList.txt";
        if(new File(accountFile).exists()) {
            accountsList = (LinkedList<Account>) SerialUtils.deserialize(accountFile);
        }

        for(int i = 0; i < 5; i++) {
            minerSim(blockchain, accountsList, namesList, shopsList);
            accountsList.forEach(x -> System.out.println(x.getAccountId() + ": " + x.getBalance()));
            System.out.println();
        }
    }
}
