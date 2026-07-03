package bank.model;

/**
 * Loan account: amount represents the outstanding loan balance (debt owed to bank).
 * Deposit = repayment. Withdraw = draw more from loan (increases debt).
 * 10% monthly interest applied on outstanding balance.
 */
public class LoanAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double INTEREST_RATE  = 0.10;
    private final double        loanLimit;

    public LoanAccount(double loanAmount, Client holder) {
        super(loanAmount, holder);
        if (loanAmount <= 0)
            throw new IllegalArgumentException("Loan amount must be greater than zero.");
        this.loanLimit = loanAmount;
    }

    @Override
    public String getAccountType() { return "Loan"; }

    /**
     * For a loan account, "withdraw" means drawing additional funds (increases debt).
     * This overrides the parent method completely.
     */
    @Override
    public double withdraw(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Draw amount must be positive.");
        if (getAmount() + amount > loanLimit)
            throw new IllegalArgumentException(
                String.format("Exceeds loan limit of PKR %.2f. Remaining limit: PKR %.2f",
                              loanLimit, loanLimit - getAmount()));
        setAmount(getAmount() + amount);
        return getAmount();
    }

    /** Repayment reduces outstanding balance. */
    @Override
    public double deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Repayment amount must be positive.");
        double newBal = Math.max(0, getAmount() - amount);
        setAmount(newBal);
        return newBal;
    }

    @Override
    public void applyMonthlyPolicy() {
        double interest = getAmount() * INTEREST_RATE;
        setAmount(getAmount() + interest);
    }

    public double getLoanLimit() { return loanLimit; }

    @Override
    public String toString() {
        return String.format("[Loan] %s | Holder: %s | Outstanding: PKR %.2f / Limit: PKR %.2f",
                getNumber(), getAcHolder().getPersonDetails().getName(), getAmount(), loanLimit);
    }
}
