package blockchain;

public class Account {
    private final String accountId;
    private long balance = 0;

    public Account(String accountId) {
        this.accountId = accountId;
    }

    public void addBalance (long balance) {
        this.balance += balance;
    }

    private void reduceBalance (long balance) {
        this.balance -= balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public long getBalance() {
        return balance;
    }

    public void sendMoneyTo(Account destination, long amount) {
        reduceBalance(amount);
        destination.addBalance(amount);
    }
}
