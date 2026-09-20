package util;

import java.util.Scanner;

/**
 * Singleton Scanner utility providing a single, application-wide Scanner instance.
 * Reused by all views, services, and controllers to avoid duplicate System.in streams.
 */
public class AppScanner {

    private static final Scanner SCANNER = new Scanner(System.in);

    // Private constructor prevents instantiation from outside
    private AppScanner() {
    }

    /**
     * Returns the singleton Scanner instance.
     */
    public static Scanner getScanner() {
        return SCANNER;
    }

    /**
     * Reads a line of text entered by the user.
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /**
     * Reads an integer with safe parsing and error recovery.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a double with safe parsing and error recovery.
     */
    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal number. Please enter a valid amount.");
            }
        }
    }
}
