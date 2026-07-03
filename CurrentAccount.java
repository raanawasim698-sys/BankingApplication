package bank.model;

/** Current account: no interest, PKR 50 monthly service charge. No minimum balance. */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double SERVICE_CHARGE = 50.0;

    public CurrentAccount(double initialAmount, Client holder) {
        super(initialAmount, holder);
    }

    @Override
    public String getAccountType() { return "Current"; }

    @Override
    public void applyMonthlyPolicy() {
        double newBal = Math.max(0, getAmount() - SERVICE_CHARGE);
        setAmount(newBal);
    }
}
