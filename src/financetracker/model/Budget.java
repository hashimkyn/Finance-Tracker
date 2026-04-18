package financetracker.model;

import java.time.YearMonth;

/**
 * Holds a monthly budget limit for a specific expense category.
 */
public class Budget {
    private final String category;
    private double limit;
    private YearMonth month;

    public Budget(String category, double limit) {
        this.category = category;
        this.limit = limit;
        this.month = YearMonth.now();
    }

    // For file loading
    public Budget(String category, double limit, YearMonth month) {
        this.category = category;
        this.limit = limit;
        this.month = month;
    }

    public String getCategory() { return category; }
    public double getLimit()    { return limit; }
    public YearMonth getMonth() { return month; }

    public void setLimit(double limit) { this.limit = limit; }

    /** Reset to the current month (monthly reset mechanism). */
    public void resetIfNewMonth() {
        YearMonth now = YearMonth.now();
        if (!now.equals(this.month)) {
            this.month = now;
        }
    }

    public String toFileString() {
        // FORMAT: category|limit|month
        return String.join("|", category, String.valueOf(limit), month.toString());
    }

    @Override
    public String toString() {
        return String.format("%-15s | Limit: %10.2f PKR | Month: %s", category, limit, month);
    }
}
