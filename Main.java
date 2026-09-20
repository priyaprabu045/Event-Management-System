import view.LoginView;

/**
 * Main application bootstrap for Event Management System.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Event Management Console Application...");
        LoginView loginView = new LoginView();
        loginView.start();
    }
}
