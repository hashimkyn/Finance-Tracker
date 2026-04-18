package financetracker.service;

import financetracker.model.*;
import financetracker.util.FileManager;

import java.util.*;

/**
 * Core service that orchestrates wallet operations, budget management,
 * and transaction persistence for a logged-in user.
 */
public class WalletService {

    private final String username;
    private final Wallet wallet;
    private final Map<String, Budget> budgets;

    public WalletService(String username) {
        this.username = username;

        // Load balance first, then replay transactions into wallet
        double savedBalance = FileManager.loadBalance(username);
        this.wallet = new Wallet(savedBalance);
        for (Transaction t : FileManager.loadTransactions(username)) {
            wallet.loadTransaction(t);
        }

        // Load budgets (auto-resets if a new month has started)
        this.budgets = FileManager.loadBudgets(username);
    }

    // ─────────────────────────── INCOME ──────────────────────────────────────

    public void addIncome(double amount, String source, String description) {
        Income inc = new Income(amount, source, description);
        wallet.addIncome(inc);
        FileManager.appendTransaction(username, inc);
        FileManager.saveBalance(username, wallet.getBalance());
    }

    // ─────────────────────────── EXPENSE ─────────────────────────────────────

    /**
     * @throws IllegalStateException if insufficient balance.
     */
    public void addExpense(double amount, String category, String description) {
        Expense exp = new Expense(amount, category, description);
        wallet.addExpense(exp);          // may throw
        FileManager.appendTransaction(username, exp);
        FileManager.saveBalance(username, wallet.getBalance());
    }

    // ─────────────────────────── BUDGETS ─────────────────────────────────────

    public void setBudget(String category, double limit) {
        Budget b = budgets.getOrDefault(category, new Budget(category, limit));
        b.setLimit(limit);
        budgets.put(category, b);
        FileManager.saveBudgets(username, budgets);
    }

    public Map<String, Budget> getBudgets() { return Collections.unmodifiableMap(budgets); }

    public double getCategorySpending(String category) {
        return wallet.getCategorySpendingThisMonth(category);
    }

    // ─────────────────────────── QUERIES ─────────────────────────────────────

    public Wallet getWallet() { return wallet; }

    public double getBalance() { return wallet.getBalance(); }

    public List<Transaction> getAllTransactions() { return wallet.getTransactions(); }

    public List<Transaction> getTransactionsByMonth(int year, int month) {
        return wallet.getByMonth(year, month);
    }

    public double getTotalIncome()   { return wallet.getTotalIncome(); }
    public double getTotalExpenses() { return wallet.getTotalExpenses(); }
    public double getNetBalance()    { return wallet.getTotalIncome() - wallet.getTotalExpenses(); }
}
