package financetracker.util;

import financetracker.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Handles all persistent file operations.
 * Users, transactions, wallets, and budgets are stored in structured text files.
 */
public class FileManager {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";

    static {
        // Ensure the data directory exists before any file operations.
        new File(DATA_DIR).mkdirs();
    }

    // ─────────────────────────────── USERS ───────────────────────────────────

    /**
     * Load all registered users from the users file.
     * Each line is expected to contain a username and password separated by '|'.
     */
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File f = new File(USERS_FILE);
        if (!f.exists()) return users;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    users.add(User.fromFile(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Save the current user list back to the users file.
     * Existing user data is overwritten.
     */
    public static void saveUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) pw.println(u.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // ──────────────────────────── TRANSACTIONS ────────────────────────────────

    private static String txFile(String username) {
        return DATA_DIR + "/" + username + "_transactions.txt";
    }

    /**
     * Append a transaction record for the given user.
     * Uses append mode so existing transaction history is preserved.
     */
    public static void appendTransaction(String username, Transaction t) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(txFile(username), true))) {
            pw.println(t.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    /**
     * Load all transactions for a user from the transaction file.
     * Malformed lines are skipped and logged.
     */
    public static List<Transaction> loadTransactions(String username) {
        List<Transaction> list = new ArrayList<>();
        File f = new File(txFile(username));
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Transaction t = parseTransaction(line);
                if (t != null) list.add(t);
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return list;
    }

    private static Transaction parseTransaction(String line) {
        try {
            String[] p = line.split("\\|");
            // FORMAT: TYPE|id|amount|category_or_source|description|date
            String type = p[0];
            String id = p[1];
            double amount = Double.parseDouble(p[2]);
            String cat = p[3];
            String desc = p[4];
            LocalDate date = LocalDate.parse(p[5]);

            if ("INCOME".equals(type)) return new Income(id, amount, cat, desc, date);
            if ("EXPENSE".equals(type)) return new Expense(id, amount, cat, desc, date);
        } catch (Exception e) {
            System.err.println("Skipping malformed transaction: " + line);
        }
        return null;
    }

    // ────────────────────────────── WALLET ───────────────────────────────────

    private static String walletFile(String username) {
        return DATA_DIR + "/" + username + "_wallet.txt";
    }

    /**
     * Load the wallet balance for a user.
     * Returns 0.0 if the wallet file does not exist or contains invalid data.
     */
    public static double loadBalance(String username) {
        File f = new File(walletFile(username));
        if (!f.exists()) return 0.0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            if (line != null && !line.trim().isEmpty())
                return Double.parseDouble(line.trim());
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading balance: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Save the current wallet balance for a user.
     * Overwrites any previous balance stored in the file.
     */
    public static void saveBalance(String username, double balance) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(walletFile(username)))) {
            pw.println(balance);
        } catch (IOException e) {
            System.err.println("Error saving balance: " + e.getMessage());
        }
    }

    // ────────────────────────────── BUDGETS ──────────────────────────────────

    private static String budgetFile(String username) {
        return DATA_DIR + "/" + username + "_budgets.txt";
    }

    /**
     * Load budget entries for a user and reset budgets if a new month has begun.
     */
    public static Map<String, Budget> loadBudgets(String username) {
        Map<String, Budget> map = new LinkedHashMap<>();
        File f = new File(budgetFile(username));
        if (!f.exists()) return map;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length == 3) {
                    String cat = p[0];
                    double limit = Double.parseDouble(p[1]);
                    YearMonth month = YearMonth.parse(p[2]);
                    Budget b = new Budget(cat, limit, month);
                    b.resetIfNewMonth();   // reset if a new month has started
                    map.put(cat, b);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading budgets: " + e.getMessage());
        }
        return map;
    }

    /**
     * Save all budget records for a user.
     */
    public static void saveBudgets(String username, Map<String, Budget> budgets) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(budgetFile(username)))) {
            for (Budget b : budgets.values()) pw.println(b.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving budgets: " + e.getMessage());
        }
    }
}
