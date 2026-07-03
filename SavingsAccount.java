package bank.model;

/** Savings account: earns 5% monthly interest. Minimum balance PKR 500. */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double MIN_BALANCE     = 500.0;
    private static final double INTEREST_RATE   = 0.05;

    public SavingsAccount(double initialAmount, Client holder) {
        super(initialAmount, holder);
        if (initialAmount < MIN_BALANCE)
            throw new IllegalArgumentException(
                "Savings account requires a minimum opening balance of PKR 500.");
    }

    @Override
    public String getAccountType() { return "Savings"; }

    @Override
    public double withdraw(double amount) {
        double projected = getAmount() - amount;
        if (projected < MIN_BALANCE)
            throw new IllegalArgumentException(
                String.format("Savings accounts must maintain PKR %.2f minimum balance. " +
                              "Max withdrawable: PKR %.2f", MIN_BALANCE, getAmount() - MIN_BALANCE));
        return super.withdraw(amount);
    }

    @Override
    public void applyMonthlyPolicy() {
        double interest = getAmount() * INTEREST_RATE;
        setAmount(getAmount() + interest);
    }
}
