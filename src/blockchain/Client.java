package blockchain;

import java.util.List;
import java.util.Random;

public class Client extends Thread{

    private final String clientName;
    private final List<Message> dataStream;
    private final Random rand = new Random();
    private boolean stop = false;

    public Client(String name, List<Message> dataStream) {
        clientName = name;
        this.dataStream = dataStream;
    }

    private Message sendMessage() {
        return new Message(clientName, StringUtil.GenerateRandomMessage());
    }

    public void disconnect() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            if (rand.nextBoolean()) {
                synchronized (dataStream) {
                    dataStream.add(sendMessage());
                }
            }

            try {
                this.sleep(500L);
            } catch (InterruptedException ignored) {}
        }
    }
}
