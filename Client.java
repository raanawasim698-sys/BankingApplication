package bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client extends Person — demonstrates inheritance.
 * A Client owns a list of Accounts (composition with Account).
 */
public class Client extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int counter = 100;

    private String        id;
    private Person        personDetails; // composition
    private List<Account> acList;

    public Client(Person personDetails) {
        super(personDetails.getName(), personDetails.getCnic(), personDetails.getPhoneNo());
        this.id            = "CL-" + (++counter);
        this.personDetails = personDetails;
        this.acList        = new ArrayList<>();
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public String        getId()            { return id; }
    public Person        getPersonDetails() { return personDetails; }
    public List<Account> getAcList()        { return acList; }

    // ── Account Operations ─────────────────────────────────────────────────────
    public void addAccount(Account a) {
        if (a == null) throw new IllegalArgumentException("Account cannot be null.");
        acList.add(a);
    }

    public void removeAccount(String accNo) {
        acList.removeIf(a -> a.getNumber().equals(accNo));
    }

    public Account findAccount(String accNo) {
        return acList.stream()
                .filter(a -> a.getNumber().equalsIgnoreCase(accNo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account " + accNo + " not found for this client."));
    }

    public double totalAmount() {
        return acList.stream()
                .filter(a -> !(a instanceof LoanAccount))
                .mapToDouble(Account::getAmount)
                .sum();
    }

    public void withdraw(double amount, String accNo) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        findAccount(accNo).withdraw(amount);
    }

    public void deposit(double amount, String accNo) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        findAccount(accNo).deposit(amount);
    }

    // ── Static counter persistence ─────────────────────────────────────────────
    public static void setCounter(int c) { counter = c; }
    public static int  getCounter()      { return counter; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ID: %s | %s\n", id, personDetails));
        if (acList.isEmpty()) {
            sb.append("  No accounts.\n");
        } else {
            for (Account a : acList) sb.append("  ").append(a).append("\n");
        }
        sb.append(String.format("  Total Holdings: PKR %.2f", totalAmount()));
        return sb.toString();
    }
}
