package main.controller;
import java.util.Scanner;
import main.dao.SurveyDAOImpl;
import main.model.UserModel;
import main.view.ConsoleView;
import main.dao.UserDAO;
import main.dao.SurveyDAO;
public class UserController {
    private UserDAO userDAO;
    private ConsoleView consoleView;
    private static final String ADMIN_USERNAME = "adminofsurvey";
    private static final String ADMIN_PASSWORD = "adminofsurvey";
    private static final String PRIVATE_COMPANY_KEY = "adminsurvey$!123";
    private SurveyDAO surveyDAO;
    public UserController(UserDAO userDAO, SurveyDAO surveyDAO) {
        this.userDAO = userDAO;
        this.consoleView = new ConsoleView();
        this.surveyDAO = surveyDAO;
    }
    public void registerUser() {
        UserModel user = consoleView.getUserInput();
        if (containsAdminKeyword(user.getUsername()) || containsAdminKeyword(user.getPassword())) {
            System.out.println("Invalid username or password. Please choose a different one.");
            registerUser();
        } else {
            boolean success = userDAO.registerUser(user);
            consoleView.showResult(success, "Registration");
            if (!success) {
                System.out.println("Please try registering again..");
                registerUser();
            } else {
                loginUser();
            }
        }
    }
    private boolean containsAdminKeyword(String input) {
        return input.toLowerCase().contains("admin");
    }

    public void loginUser() {
        UserModel user = consoleView.getUserInput();
        if (user.getUsername().equals(ADMIN_USERNAME)) {
            validateAdminCredentials(user);
        } else {
            boolean success = userDAO.loginUser(user);
            consoleView.showResult(success, "Login");
            if (!success) {
                System.out.println("Please try logging in again.");
                loginUser();
            } else {
                UserMenuController userMenuController = new UserMenuController(user,surveyDAO);
                userMenuController.showUserMenu();
            }
        }
    }
    private void validateAdminCredentials(UserModel user) {
        Scanner scanner = new Scanner(System.in);
        if (!user.getPassword().equals(ADMIN_PASSWORD)) {
            System.out.println("Invalid admin password. Login failed.");
            return;
        }
        System.out.print("Enter private company key: ");
        String companyKey = scanner.nextLine();
        if (!companyKey.equals(PRIVATE_COMPANY_KEY)) {
            System.out.println("Invalid private company key. Login failed.");
            return;
        }
        System.out.println("Admin login successful.");
        AdminController adminController = new AdminController();
        adminController.displayAdminMenu();
    }
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        SurveyDAO surveyDAO = new SurveyDAOImpl();
        UserController userController = new UserController(userDAO, surveyDAO);
        userController.registerUser();
    }
}
