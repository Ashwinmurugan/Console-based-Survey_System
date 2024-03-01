package main;
import main.controller.UserController;
import main.dao.SurveyDAO;
import main.dao.SurveyDAOImpl;
import main.dao.UserDAO;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        SurveyDAO surveyDAO = new SurveyDAOImpl();
        UserController userController = new UserController(userDAO, surveyDAO);
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.printf("*********************************\n");
        System.out.println("*   Welcome To Survey System   *");
        System.out.printf("*********************************\n");
        System.out.println();
        System.out.println("1. SignIn");
        System.out.println("2. SignUp");
        System.out.println("Enter Choice:");
        int x = sc.nextInt();
        switch (x) {
            case 1:
                userController.loginUser();
                break;
            case 2:
                userController.registerUser();
                break;
            default:
                System.out.println("-----Invalid Choice!-----");
        }
    }
}
