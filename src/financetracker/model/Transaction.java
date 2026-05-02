package financetracker.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Abstract base class for all financial transactions.
 * Demonstrates abstraction and encapsulation.
 */
public abstract class Transaction {
    // Attributes
    private final String id;
    private final double amount;
    private final LocalDate date;
    private final String description;
    // Constructor for Transaction
    public Transaction(double amount, String description) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.amount = amount;
        this.description = description;
        this.date = LocalDate.now();
    }

    // Constructor for loading from file
    public Transaction(String id, double amount, String description, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }
    // Getter functions
    public String getId()          { return id; }
    public double getAmount()      { return amount; }
    public LocalDate getDate()     { return date; }
    public String getDescription() { return description; }
    // Abstract functions to be implemented by child classes
    public abstract String getType();
    public abstract String getCategory();

    /** Serialize to a pipe-delimited string for file storage. */
    public abstract String toFileString();
    // Function for transaction format
    @Override
    public String toString() {
        return String.format("[%s] %-8s | %s | %10.2f PKR | %s | %s",
                id, getType(), date, amount, getCategory(), description);
    }
}
