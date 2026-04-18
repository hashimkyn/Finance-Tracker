package financetracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wallet maintains the user's current balance and complete transaction history.
 * Demonstrates composition (Transaction objects inside Wallet).
 */
public class Wallet {
    private double balance;
    private final List<Transaction> transactions;

    public Wallet() {
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public Wallet(double initialBalance) {
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }

    public double getBalance() { return balance; }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);   // defensive copy
    }

    /**
     * Add income: increases balance.
     */
    public void addIncome(Income income) {
        balance += income.getAmount();
        transactions.add(income);
    }

    /**
     * Add expense: decreases balance.
     * @throws IllegalStateException if insufficient funds.
     */
    public void addExpense(Expense expense) {
        if (expense.getAmount() > balance) {
            throw new IllegalStateException(
                    "Insufficient balance! Current balance: " + String.format("%.2f", balance) + " PKR");
        }
        balance -= expense.getAmount();
        transactions.add(expense);
    }

    /**
     * Load a transaction directly from file without modifying balance
     * (balance is stored separately).
     */
    public void loadTransaction(Transaction t) {
        transactions.add(t);
    }

    /** All transactions of a given month (1-indexed). */
    public List<Transaction> getByMonth(int year, int month) {
        return transactions.stream()
                .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    /** Total spending in a given category for the current month. */
    public double getCategorySpendingThisMonth(String category) {
        java.time.YearMonth ym = java.time.YearMonth.now();
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .filter(t -> java.time.YearMonth.from(t.getDate()).equals(ym))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /** Total income across all time. */
    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t instanceof Income)
                .mapToDouble(Transaction::getAmount).sum();
    }

    /** Total expenses across all time. */
    public double getTotalExpenses() {
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .mapToDouble(Transaction::getAmount).sum();
    }
}
