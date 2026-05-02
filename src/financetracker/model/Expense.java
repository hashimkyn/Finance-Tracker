package financetracker.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an expense transaction with a predefined category.
 * Inherits from Transaction – demonstrates inheritance.
 */
public class Expense extends Transaction {
    // List for storing categories of expense
    public static final List<String> CATEGORIES = Arrays.asList(
            "Food", "Transport", "Housing", "Health",
            "Education", "Entertainment", "Shopping", "Utilities", "Other"
    );

    private final String category;
    // Constructor
    public Expense(double amount, String category, String description) {
        super(amount, description);
        if (!CATEGORIES.contains(category))
            throw new IllegalArgumentException("Invalid category: " + category);
        this.category = category;
    }

    // For file loading
    public Expense(String id, double amount, String category, String description, LocalDate date) {
        super(id, amount, description, date);
        this.category = category;
    }

    @Override public String getType()     { return "EXPENSE"; }
    @Override public String getCategory() { return category; }

    @Override
    public String toFileString() {
        // FORMAT: EXPENSE|id|amount|category|description|date
        return String.join("|", "EXPENSE", getId(), String.valueOf(getAmount()),
                category, getDescription(), getDate().toString());
    }
}
