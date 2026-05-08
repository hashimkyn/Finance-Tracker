package financetracker.model;

import java.time.LocalDate;

/**
 * Represents an income transaction.
 * Inherits from Transaction – demonstrates inheritance.
 */
public class Income extends Transaction {
    //Attributes
    private final String source;

    // Constructor for income
    public Income(double amount, String source, String description) {
        super(amount, description);
        this.source = source;
    }

    // For file loading
    public Income(String id, double amount, String source, String description, LocalDate date) {
        super(id, amount, description, date);
        this.source = source;
    }

    // Getter Functions
    public String getSource() { return source; }

    @Override public String getType()     { return "INCOME"; }
    @Override public String getCategory() { return source; }
    @Override
    public String toFileString() {
        // FORMAT: INCOME|id|amount|source|description|date
        return String.join("|", "INCOME", getId(), String.valueOf(getAmount()),
                source, getDescription(), getDate().toString());
    }
}
