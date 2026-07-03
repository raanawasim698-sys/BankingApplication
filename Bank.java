package bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bank owns Accounts (composition) — if Bank is gone, accounts go too.
 * Clients exist independently (aggregation).
 */
public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;

    private String        name;
    private List<Client>  clList;
    private List<Account> acList;
    private List<BillPayment> billHistory;

    public Bank(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Bank name cannot be empty.");
        this.name        = name.trim();
        this.clList      = new ArrayList<>();
        this.acList      = new ArrayList<>();
        this.billHistory = new ArrayList<>();
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public String            getName()        { return name; }
    public List<Client>      getClList()      { return clList; }
    public List<Account>     getAcList()      { return acList; }
    public List<BillPayment> getBillHistory() { return billHistory; }

    // ── Client Operations ──────────────────────────────────────────────────────
    public Client addClient(Person p) {
        if (searchClientByCnic(p.getCnic()) != null)
            throw new IllegalArgumentException("A client with CNIC " + p.getCnic() + " already exists.");
        Client c = new Client(p);
        clList.add(c);
        return c;
    }

    public boolean removeClient(String id) {
        Client c = searchClientById(id);
        if (c == null) return false;
        // Composition: removing client destroys their accounts from the bank
        acList.removeIf(a -> a.getAcHolder().getId().equals(id));
        c.getAcList().clear();
        clList.remove(c);
        return true;
    }

    public Client searchClientById(String id) {
        return clList.stream()
                .filter(c -> c.getId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

    public Client searchCustomerDetail(String cnic) {
        return clList.stream()
                .filter(c -> c.getPersonDetails().getCnic().equals(cnic))
                .findFirst().orElse(null);
    }

    private Client searchClientByCnic(String cnic) {
        return clList.stream()
                .filter(c -> c.getPersonDetails().getCnic().equals(cnic))
                .findFirst().orElse(null);
    }

    // ── Account Operations ─────────────────────────────────────────────────────
    public Account addAccount(String clientId, double amount, String type) {
        Client c = searchClientById(clientId);
        if (c == null) throw new IllegalArgumentException("Client not found: " + clientId);

        Account acc;
        switch (type.toLowerCase()) {
            case "savings":  acc = new SavingsAccount(amount, c);  break;
            case "current":  acc = new CurrentAccount(amount, c);  break;
            case "loan":     acc = new LoanAccount(amount, c);     break;
            default: throw new IllegalArgumentException("Unknown account type: " + type);
        }

        acList.add(acc);
        c.addAccount(acc);
        return acc;
    }

    public Account searchAccount(String accNo) {
        return acList.stream()
                .filter(a -> a.getNumber().equalsIgnoreCase(accNo))
                .findFirst().orElse(null);
    }

    // ── Transfer Operation ─────────────────────────────────────────────────────
    public void transfer(String fromAccNo, String toAccNo, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Transfer amount must be positive.");
        if (fromAccNo.equalsIgnoreCase(toAccNo))
            throw new IllegalArgumentException("Source and destination accounts must be different.");

        Account from = searchAccount(fromAccNo);
        Account to   = searchAccount(toAccNo);

        if (from == null) throw new IllegalArgumentException("Source account not found: " + fromAccNo);
        if (to == null)   throw new IllegalArgumentException("Destination account not found: " + toAccNo);
        if (from instanceof LoanAccount)
            throw new IllegalArgumentException("Cannot transfer from a Loan account.");

        from.withdraw(amount);
        to.deposit(amount);
    }

    // ── Bill Payment ───────────────────────────────────────────────────────────
    public BillPayment payBill(String accNo, BillPayment.BillType type,
                               String consumerRef, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Bill amount must be positive.");

        Account acc = searchAccount(accNo);
        if (acc == null) throw new IllegalArgumentException("Account not found: " + accNo);
        if (acc instanceof LoanAccount)
            throw new IllegalArgumentException("Cannot pay bills from a Loan account.");

        acc.withdraw(amount);   // deducts from account
        BillPayment bp = new BillPayment(accNo, type, consumerRef, amount);
        billHistory.add(bp);
        return bp;
    }

    // ── Aggregate ─────────────────────────────────────────────────────────────
    public double totalAmount() {
        return acList.stream()
                .filter(a -> !(a instanceof LoanAccount))
                .mapToDouble(Account::getAmount)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("Bank: %s | Clients: %d | Accounts: %d | Total Deposits: PKR %.2f",
                name, clList.size(), acList.size(), totalAmount());
    }
}
