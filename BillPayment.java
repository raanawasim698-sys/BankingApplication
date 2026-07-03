package bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a utility/bill payment transaction. */
public class BillPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum BillType {
        ELECTRICITY("WAPDA / Electricity"),
        GAS("SNGPL / Gas"),
        WATER("WASA / Water"),
        INTERNET("Internet / Broadband"),
        MOBILE("Mobile Top-up"),
        SCHOOL_FEE("School / University Fee");

        private final String label;
        BillType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private final String    accountNumber;
    private final BillType  billType;
    private final String    consumerRef;   // e.g. meter number, roll number
    private final double    amount;
    private final String    timestamp;

    public BillPayment(String accountNumber, BillType billType, String consumerRef, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Bill amount must be positive.");
        if (consumerRef == null || consumerRef.trim().isEmpty())
            throw new IllegalArgumentException("Consumer reference number is required.");

        this.accountNumber = accountNumber;
        this.billType      = billType;
        this.consumerRef   = consumerRef.trim();
        this.amount        = amount;
        this.timestamp     = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    public String getAccountNumber() { return accountNumber; }
    public BillType getBillType()    { return billType; }
    public String getConsumerRef()   { return consumerRef; }
    public double getAmount()        { return amount; }
    public String getTimestamp()     { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Ref: %s | PKR %.2f | Paid from: %s",
                timestamp, billType.getLabel(), consumerRef, amount, accountNumber);
    }
}
