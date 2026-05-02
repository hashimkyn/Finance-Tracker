package financetracker;

import financetracker.model.*;
import financetracker.service.*;
import financetracker.util.ConsoleHelper;

import java.util.*;

public class Main {

    // Shared console input scanner for all menu prompts.
    private static final Scanner sc = new Scanner(System.in);

    // Services and state used while the application is running.
    private static AuthService auth;
    private static User currentUser;
    private static WalletService walletSvc;
    private static InsightsService insightsSvc;

    public static void main(String[] args) {
        auth = new AuthService();
        printBanner();
        mainMenu();
        System.out.println(ConsoleHelper.CYAN + "\n  Goodbye!\n" + ConsoleHelper.RESET);
    }

    // ═══════════════════════════ MENUS ═══════════════════════════════════════

    /**
     * Shows the initial menu where users can register or log in.
     * This loop continues until the user chooses to exit.
     */
    private static void mainMenu() {
        while (true) {
            ConsoleHelper.printHeader("Personal Finance & Expense Tracker");
            ConsoleHelper.printMenu(new String[]{"Register", "Login"});
            int choice = ConsoleHelper.promptInt(sc, "Choice:", 0, 2);
            switch (choice) {
                case 1 -> doRegister();
                case 2 -> { if (doLogin()) dashboardMenu(); }
                case 0 -> { return; }
            }
        }
    }

    /**
     * Displays the authenticated user's dashboard and main finance actions.
     * Initializes wallet and insights services for the logged-in user.
     */
    private static void dashboardMenu() {
        walletSvc = new WalletService(currentUser.getUsername());
        insightsSvc = new InsightsService(walletSvc);

        while (true) {
            ConsoleHelper.printHeader("Dashboard -- Welcome, " + currentUser.getUsername());
            System.out.printf("  %sWallet Balance: %.2f PKR%s%n%n",
                    ConsoleHelper.GREEN, walletSvc.getBalance(), ConsoleHelper.RESET);
            ConsoleHelper.printMenu(new String[]{
                    "Add Income",
                    "Add Expense",
                    "View Transactions",
                    "Manage Budgets",
                    "Financial Insights",
                    "Logout"
            });
            int choice = ConsoleHelper.promptInt(sc, "Choice:", 0, 6);
            switch (choice) {
                case 1 -> doAddIncome();
                case 2 -> doAddExpense();
                case 3 -> transactionMenu();
                case 4 -> budgetMenu();
                case 5 -> insightsSvc.printInsights();
                case 6 -> { currentUser = null; ConsoleHelper.success("Logged out."); return; }
                case 0 -> { return; }
            }
        }
    }

    // ═══════════════════════════ AUTH ════════════════════════════════════════

    /**
     * Prompts the user to create a new account and validates username availability.
     */
    private static void doRegister() {
        ConsoleHelper.printHeader("Register");
        String username = ConsoleHelper.prompt(sc, "Username:");
        if (auth.usernameExists(username)) {
            ConsoleHelper.error("Username already taken."); return;
        }
        String password = ConsoleHelper.prompt(sc, "Password (min 4 chars):");
        if (auth.register(username, password)) {
            ConsoleHelper.success("Registration successful! You can now log in.");
        } else {
            ConsoleHelper.error("Registration failed. Ensure username is non-empty and password ≥ 4 characters.");
        }
    }

    /**
     * Handles user login and stores the authenticated user on success.
     */
    private static boolean doLogin() {
        ConsoleHelper.printHeader("Login");
        String username = ConsoleHelper.prompt(sc, "Username:");
        String password = ConsoleHelper.prompt(sc, "Password:");
        User user = auth.login(username, password);
        if (user != null) {
            currentUser = user;
            ConsoleHelper.success("Login successful! Welcome, " + username + ".");
            return true;
        } else {
            ConsoleHelper.error("Invalid username or password.");
            return false;
        }
    }

    // ═══════════════════════ INCOME / EXPENSE ════════════════════════════════

    /**
     * Prompts the user to enter income details and records the income.
     */
    private static void doAddIncome() {
        ConsoleHelper.printHeader("Add Income");
        double amount = ConsoleHelper.promptDouble(sc, "Amount (PKR):");
        String source = ConsoleHelper.prompt(sc, "Source (e.g. Salary, Freelance):");
        String desc = ConsoleHelper.prompt(sc, "Description:");
        walletSvc.addIncome(amount, source, desc);
        ConsoleHelper.success(String.format("Income of %.2f PKR recorded. New balance: %.2f PKR",
                amount, walletSvc.getBalance()));
    }

    /**
     * Guides the user through adding an expense and warns if the budget limit is exceeded.
     */
    private static void doAddExpense() {
        ConsoleHelper.printHeader("Add Expense");
        double amount = ConsoleHelper.promptDouble(sc, "Amount (PKR):");

        // Pick category
        List<String> cats = Expense.CATEGORIES;
        System.out.println(ConsoleHelper.BOLD + "\n  Select Category:" + ConsoleHelper.RESET);
        for (int i = 0; i < cats.size(); i++) {
            System.out.printf("  %s[%d]%s %s%n", ConsoleHelper.YELLOW, i + 1, ConsoleHelper.RESET, cats.get(i));
        }
        int catIdx = ConsoleHelper.promptInt(sc, "Category:", 1, cats.size());
        String category = cats.get(catIdx - 1);
        String desc = ConsoleHelper.prompt(sc, "Description:");

        // Budget warning before recording
        double spent = walletSvc.getCategorySpending(category);
        Budget budget = walletSvc.getBudgets().get(category);
        if (budget != null && (spent + amount) > budget.getLimit()) {
            ConsoleHelper.warn(String.format(
                    "This expense will exceed your %s budget! (Limit: %.2f, Spent: %.2f, New: %.2f)",
                    category, budget.getLimit(), spent, spent + amount));
            String confirm = ConsoleHelper.prompt(sc, "Proceed anyway? (yes/no):");
            if (!confirm.equalsIgnoreCase("yes")) {
                ConsoleHelper.info("Expense cancelled."); return;
            }
        }

        try {
            walletSvc.addExpense(amount, category, desc);
            ConsoleHelper.success(String.format("Expense of %.2f PKR [%s] recorded. New balance: %.2f PKR",
                    amount, category, walletSvc.getBalance()));
        } catch (IllegalStateException e) {
            ConsoleHelper.error(e.getMessage());
        }
    }

    // ═══════════════════════ TRANSACTIONS ════════════════════════════════════

    /**
     * Transaction menu for viewing all records or filtering by month.
     */
    private static void transactionMenu() {
        while (true) {
            ConsoleHelper.printHeader("View Transactions");
            ConsoleHelper.printMenu(new String[]{"All Transactions", "Filter by Month"});
            int choice = ConsoleHelper.promptInt(sc, "Choice:", 0, 2);
            switch (choice) {
                case 1 -> printTransactions(walletSvc.getAllTransactions(), "All Transactions");
                case 2 -> doMonthFilter();
                case 0 -> { return; }
            }
        }
    }

    /**
     * Prompts the user for a year and month, then displays matching transactions.
     */
    private static void doMonthFilter() {
        int year = ConsoleHelper.promptInt(sc, "Year (e.g. 2025):", 2000, 2100);
        int month = ConsoleHelper.promptInt(sc, "Month (1-12):", 1, 12);
        List<Transaction> txs = walletSvc.getTransactionsByMonth(year, month);
        printTransactions(txs, String.format("Transactions -- %04d-%02d", year, month));
    }

    /**
     * Prints a list of transactions with colored output and totals for income and expenses.
     */
    private static void printTransactions(List<Transaction> txs, String title) {
        ConsoleHelper.printHeader(title);
        if (txs.isEmpty()) {
            ConsoleHelper.info("No transactions found."); return;
        }
        double totalIn = 0, totalOut = 0;
        for (Transaction t : txs) {
            String color = t instanceof Income ? ConsoleHelper.GREEN : ConsoleHelper.RED;
            System.out.println(color + "  " + t + ConsoleHelper.RESET);
            if (t instanceof Income) totalIn += t.getAmount();
            else totalOut += t.getAmount();
        }
        ConsoleHelper.printSeparator();
        System.out.printf("  Total Income:   %s%.2f PKR%s%n", ConsoleHelper.GREEN, totalIn, ConsoleHelper.RESET);
        System.out.printf("  Total Expenses: %s%.2f PKR%s%n", ConsoleHelper.RED, totalOut, ConsoleHelper.RESET);
        System.out.println();
    }

    // ═══════════════════════════ BUDGETS ═════════════════════════════════════

    /**
     * Opens the budget management menu to set or view budgets.
     */
    private static void budgetMenu() {
        while (true) {
            ConsoleHelper.printHeader("Budget Manager");
            ConsoleHelper.printMenu(new String[]{"Set / Update Budget", "View All Budgets"});
            int choice = ConsoleHelper.promptInt(sc, "Choice:", 0, 2);
            switch (choice) {
                case 1 -> doSetBudget();
                case 2 -> viewBudgets();
                case 0 -> { return; }
            }
        }
    }

    /**
     * Prompts the user to set or update a monthly budget for a selected expense category.
     */
    private static void doSetBudget() {
        List<String> cats = Expense.CATEGORIES;
        System.out.println(ConsoleHelper.BOLD + "\n  Select Category:" + ConsoleHelper.RESET);
        for (int i = 0; i < cats.size(); i++)
            System.out.printf("  %s[%d]%s %s%n", ConsoleHelper.YELLOW, i + 1, ConsoleHelper.RESET, cats.get(i));
        int catIdx = ConsoleHelper.promptInt(sc, "Category:", 1, cats.size());
        String category = cats.get(catIdx - 1);
        double limit = ConsoleHelper.promptDouble(sc, "Monthly Limit (PKR):");
        walletSvc.setBudget(category, limit);
        ConsoleHelper.success(String.format("Budget for '%s' set to %.2f PKR/month.", category, limit));
    }

    /**
     * Displays all current budgets with a progress bar showing spending vs limit.
     */
    private static void viewBudgets() {
        ConsoleHelper.printHeader("Current Budgets");
        Map<String, Budget> budgets = walletSvc.getBudgets();
        if (budgets.isEmpty()) { ConsoleHelper.info("No budgets set yet."); return; }
        for (Map.Entry<String, Budget> e : budgets.entrySet()) {
            double spent = walletSvc.getCategorySpending(e.getKey());
            double limit = e.getValue().getLimit();
            String bar = buildProgressBar(spent, limit, 20);
            String color = spent > limit ? ConsoleHelper.RED : ConsoleHelper.GREEN;
            System.out.printf("  %-15s %s%s%s  %.2f / %.2f PKR%n",
                    e.getKey() + ":", color, bar, ConsoleHelper.RESET, spent, limit);
        }
        System.out.println();
    }

    private static String buildProgressBar(double spent, double limit, int width) {
        int filled = (limit > 0) ? (int) Math.min((spent / limit) * width, width) : 0;
        return "[" + "█".repeat(filled) + "░".repeat(width - filled) + "]";
    }

    // ═══════════════════════════ BANNER ══════════════════════════════════════
    /**
     * Prints the app banner shown at startup.
     */
    private static void printBanner() {
        System.out.println(ConsoleHelper.CYAN);
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║          PERSONAL FINANCE & EXPENSE TRACKER          ║");
        System.out.println("  ║          Console-Based Java OOP Application          ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println(ConsoleHelper.RESET);
    }
}
