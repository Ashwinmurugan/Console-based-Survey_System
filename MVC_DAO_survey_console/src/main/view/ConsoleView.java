package main.view;

import main.model.UserModel;

import java.util.Scanner;

public class ConsoleView {
    private Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);

    }

    public UserModel getUserInput() {
        System.out.print("Enter Your UserName: ");
        String username = scanner.nextLine();
        System.out.print("Enter Your Password: ");
        String password = scanner.nextLine();

        return new UserModel(username, password);
    }

    public void showResult(boolean success, String action) {
        if (success) {
            System.out.println(action+" Successfully!! ");
        }
        else{
            System.out.println(action+" failed!!");
        }
    }
}
