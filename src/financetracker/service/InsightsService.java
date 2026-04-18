package financetracker.service;

import financetracker.model.Budget;
import financetracker.model.Expense;
import financetracker.model.Transaction;
import financetracker.util.ConsoleHelper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Generates financial insights: summaries, category breakdowns,
 * budget violations, and the daily spending guide.
 */
public class InsightsService {

    private final WalletService walletService;

    public InsightsService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void printInsights() {
        ConsoleHelper.printHeader("📊  FINANCIAL INSIGHTS");

        // ── 1. Summary ──────────────────────────────────────────────────────
        double income   = walletService.getTotalIncome();
        double expenses = walletService.getTotalExpenses();
        double net      = walletService.getNetBalance();
        double balance  = walletService.getBalance();

        System.out.printf("  %-22s %s%.2f PKR%s%n", "Total Income:",   ConsoleHelper.GREEN,  income,   ConsoleHelper.RESET);
        System.out.printf("  %-22s %s%.2f PKR%s%n", "Total Expenses:", ConsoleHelper.RED,    expenses, ConsoleHelper.RESET);
        System.out.printf("  %-22s %s%.2f PKR%s%n", "Net Balance:",    net >= 0 ? ConsoleHelper.GREEN : ConsoleHelper.RED, net, ConsoleHelper.RESET);
        System.out.printf("  %-22s %s%.2f PKR%s%n", "Wallet Balance:", ConsoleHelper.CYAN,   balance,  ConsoleHelper.RESET);
        ConsoleHelper.printSeparator();

        // ── 2. Category-wise Spending (current month) ─────────────────────
        System.out.println(ConsoleHelper.BOLD + "  Category Spending — " + YearMonth.now() + ConsoleHelper.RESET);
        Map<String, Double> catSpend = getCategorySpendingThisMonth();
        if (catSpend.isEmpty()) {
            ConsoleHelper.info("No expenses recorded this month.");
        } else {
            catSpend.forEach((cat, amt) ->
                    System.out.printf("    %-15s  %s%.2f PKR%s%n",
                            cat + ":", ConsoleHelper.RED, amt, ConsoleHelper.RESET));
        }
        ConsoleHelper.printSeparator();

        // ── 3. Budget Violations ─────────────────────────────────────────
        System.out.println(ConsoleHelper.BOLD + "  Budget Violations" + ConsoleHelper.RESET);
        Map<String, Budget> budgets = walletService.getBudgets();
        boolean anyViolation = false;
        for (Map.Entry<String, Budget> e : budgets.entrySet()) {
            String  cat   = e.getKey();
            double  limit = e.getValue().getLimit();
            double  spent = walletService.getCategorySpending(cat);
            if (spent > limit) {
                System.out.printf("    %s⚠  %-14s Spent: %.2f / Limit: %.2f PKR (over by %.2f)%s%n",
                        ConsoleHelper.YELLOW, cat, spent, limit, spent - limit, ConsoleHelper.RESET);
                anyViolation = true;
            }
        }
        if (!anyViolation) ConsoleHelper.success("All categories within budget!");
        ConsoleHelper.printSeparator();

        // ── 4. Daily Spending Guide ───────────────────────────────────────
        System.out.println(ConsoleHelper.BOLD + "  Daily Spending Guide" + ConsoleHelper.RESET);
        LocalDate today = LocalDate.now();
        int daysLeft = today.lengthOfMonth() - today.getDayOfMonth() + 1;
        if (daysLeft > 0 && balance > 0) {
            double dailyBudget = balance / daysLeft;
            System.out.printf("    Wallet Balance  : %.2f PKR%n", balance);
            System.out.printf("    Days Left       : %d day(s)%n", daysLeft);
            System.out.printf("    %sRecommended/Day : %.2f PKR%s%n",
                    ConsoleHelper.GREEN, dailyBudget, ConsoleHelper.RESET);
        } else if (balance <= 0) {
            ConsoleHelper.error("No balance remaining — please add income.");
        }
        System.out.println();
    }

    // ─────────────────────── Helper ──────────────────────────────────────────

    private Map<String, Double> getCategorySpendingThisMonth() {
        YearMonth ym = YearMonth.now();
        Map<String, Double> map = new LinkedHashMap<>();
        for (Transaction t : walletService.getAllTransactions()) {
            if (t instanceof Expense && YearMonth.from(t.getDate()).equals(ym)) {
                map.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }
        return map;
    }
}
