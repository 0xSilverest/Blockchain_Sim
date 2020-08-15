package blockchain;

public class Message {
    String senderName;
    String message;

    public Message (String sender, String message) {
        senderName = sender;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }

    @Override
    public String toString() {
        if (senderName.equals(""))
            return message;
        return senderName + ": " + message;
    }
}
