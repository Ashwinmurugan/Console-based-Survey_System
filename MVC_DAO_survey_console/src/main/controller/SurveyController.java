package main.controller;
import main.model.SurveyModel;
import main.dao.SurveyDAO;
import main.dao.SurveyDAOImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class SurveyController {
    private SurveyDAO surveyDAO = new SurveyDAOImpl();
    private Scanner scanner = new Scanner(System.in);
    public void createSurvey() {
        SurveyModel survey = new SurveyModel();
        System.out.print("Enter the survey question: ");
        survey.setQuestion(scanner.nextLine());
        System.out.print("Enter the number of choices: ");
        int numberOfChoices = scanner.nextInt();
        survey.setNumberOfChoices(numberOfChoices);
        List<String> choices = new ArrayList<>();
        scanner.nextLine();
        for (int i = 1; i <= numberOfChoices; i++) {
            System.out.println("Enter choice " + i + ": ");
            choices.add(scanner.nextLine());
        }
        survey.setChoices(choices);
        System.out.print("Enter the category: ");
        survey.setCategory(scanner.next());
        System.out.print("Enter the survey limit: ");
        survey.setSurveyLimit(scanner.nextInt());
        System.out.println("Survey Details:");
        System.out.println("Question: " + survey.getQuestion());
        System.out.println("Number of Choices: " + survey.getNumberOfChoices());
        System.out.println("Choices: " + survey.getChoices());
        System.out.println("Category: " + survey.getCategory());
        System.out.println("Survey Limit: " + survey.getSurveyLimit());
        System.out.print("Do you want to publish the survey? (1. Yes / 2. No): ");
        int publishChoice = scanner.nextInt();
        if (publishChoice == 1) {
            boolean success = surveyDAO.addSurvey(survey);
            if (success) {
                System.out.println("Survey added successfully!");
            } else {
                System.out.println("Failed to add the survey. Please try again.");
            }
        } else {
            System.out.print("Do you want to create another survey? (1. Yes / 2. No): ");
            int createAnotherSurveyChoice = scanner.nextInt();
            if (createAnotherSurveyChoice == 1) {
                createSurvey();
            } else {
                System.out.println("Survey creation cancelled.");
            }
        }
    }
    public static void main(String[] args) {
        SurveyController surveyController = new SurveyController();
        surveyController.createSurvey();
    }
}
