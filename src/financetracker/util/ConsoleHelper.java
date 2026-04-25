package financetracker.util;

import java.util.Scanner;

// Utility class for formatted console UI helpers.

public class ConsoleHelper {
    // Storing colors for a decent UI
    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";
    public static final String GREEN  = "\u001B[32m";
    public static final String RED    = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN   = "\u001B[36m";
    public static final String BLUE   = "\u001B[34m";

    // Function for printing titles 
    public static void printHeader(String title) {
        String line = "═".repeat(75); // storing lines
        System.out.println(CYAN + "\n" + line);
        System.out.printf("  %s%s%s%s%n", BOLD, YELLOW, title, RESET + CYAN);
        System.out.println(line + RESET);
    }

    public static void printSeparator() {
        System.out.println(CYAN + "─".repeat(75) + RESET);
    }
    //Messages in different colors depending on the type
    public static void success(String msg) {
        System.out.println(GREEN + msg + RESET);
    }

    public static void error(String msg) {
        System.out.println(RED + msg + RESET);
    }

    public static void info(String msg) {
        System.out.println(BLUE + msg + RESET);
    }

    public static void warn(String msg) {
        System.out.println(YELLOW + msg + RESET);
    }
    // Function for taking input as string from users
    public static String prompt(Scanner sc, String label) {
        System.out.print(BOLD + label + RESET + " ");
        return sc.nextLine().trim(); //trim.() handles extra space before or after text
    }

    public static double promptDouble(Scanner sc, String label) {
        while (true) {
            try {
                String raw = prompt(sc, label);
                double v = Double.parseDouble(raw); // converting string to double
                if (v <= 0) { error("Amount must be positive."); continue; }
                return v;
            } catch (NumberFormatException e) {
                error("Please enter a valid number.");
            }
        }
    }
    
    public static int promptInt(Scanner sc, String label, int min, int max) {
        while (true) {
            try {
                String raw = prompt(sc, label);
                int v = Integer.parseInt(raw); // converting string to int
                if (v < min || v > max) {
                    error("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                error("Please enter a valid integer.");
            }
        }
    }
    //Function for printing menus
    public static void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.printf("  %s[%d]%s %s%n", YELLOW, i + 1, RESET, options[i]);
        }
        System.out.printf("  %s[0]%s Back / Exit%n%n", YELLOW, RESET);
    }
}
