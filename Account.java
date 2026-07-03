package bank.model;

import java.io.Serializable;

/**
 * Abstract Account — base class demonstrating polymorphism.
 * Concrete types: SavingsAccount, CurrentAccount, LoanAccount.
 */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int counter = 1000;

    private String   number;
    private double   amount;
    private Client   acHolder;

    public Account(double initialAmount, Client acHolder) {
        if (initialAmount < 0)
            throw new IllegalArgumentException("Initial amount cannot be negative.");
        if (acHolder == null)
            throw new IllegalArgumentException("Account must have a valid holder.");

        this.number   = "ACC-" + (++counter);
        this.amount   = initialAmount;
        this.acHolder = acHolder;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────────
    public String getNumber()   { return number; }
    public double getAmount()   { return amount; }
    public Client getAcHolder() { return acHolder; }

    protected void setAmount(double amount) { this.amount = amount; }

    // ── Core Operations ────────────────────────────────────────────────────────

    /** Returns remaining balance after withdrawal. */
    public double withdraw(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > this.amount)
            throw new IllegalArgumentException("Insufficient funds. Available: PKR " + String.format("%.2f", this.amount));
        this.amount -= amount;
        return this.amount;
    }

    /** Returns new balance after deposit. */
    public double deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive.");
        this.amount += amount;
        return this.amount;
    }

    /** Account type label — overridden by subclasses (polymorphism). */
    public abstract String getAccountType();

    /** Monthly interest or charge — each type behaves differently. */
    public abstract void applyMonthlyPolicy();

    @Override
    public String toString() {
        return String.format("[%s] %s | Holder: %s | Balance: PKR %.2f",
                getAccountType(), number, acHolder.getPersonDetails().getName(), amount);
    }

    // ── Static counter persistence helper ────────────────────────────────────
    public static void setCounter(int c) { counter = c; }
    public static int  getCounter()      { return counter; }
}
