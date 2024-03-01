package main.controller;
import main.model.SurveyModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class AdminController {
    private final String JDBC_URL = "jdbc:mysql://localhost:3306/survey_console";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private final Scanner scanner = new Scanner(System.in);
    private SurveyController surveyController = new SurveyController();
    public void displayAdminMenu() {
        int choice;
        do {
            System.out.println("Admin Menu:");
            System.out.println("1. Add New Survey");
            System.out.println("2. Edit or remove Survey");
            System.out.println("3. Show Survey Result");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = getUserChoice(4);

            switch (choice) {
                case 1:
                    surveyController.createSurvey();
                    break;
                case 2:
                    System.out.println("Note: Surveys are non-editable as they are active daily!");
                    break;
                case 3:
                    displaySurveyResults();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);
    }
    private void displaySurveyResults() {
        List<String> categories = getUniqueSurveyCategories();

        if (categories == null || categories.isEmpty()) {
            System.out.println("No survey categories available.");
        } else {
            System.out.println("Available Survey Categories:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i));
            }
            System.out.print("Enter the number of the category you are interested in: ");
            int categoryChoice = getUserChoice(categories.size());

            if (categoryChoice > 0 && categoryChoice <= categories.size()) {
                String selectedCategory = categories.get(categoryChoice - 1);
                List<SurveyModel> surveys = getSurveysByCategory(selectedCategory);

                if (!surveys.isEmpty()) {
                    displaySurveyResultsForCategory(selectedCategory, surveys);
                } else {
                    System.out.println("No surveys available for the selected category.");
                }
            } else {
                System.out.println("Invalid category choice. Please try again.");
            }
        }
    }
    private void displaySurveyResultsForCategory(String category, List<SurveyModel> surveys) {
        System.out.println("Survey Results for Category: " + category);
        for (SurveyModel survey : surveys) {
            System.out.println("Survey Question: " + survey.getQuestion());
            System.out.println("Choices and Counts:");

            List<String> choices = getSurveyChoicesBySurveyId(survey.getId());

            for (String choice : choices) {
                int count = getChoiceCount(survey.getId(), choice);
                System.out.println(choice + ": " + count);
            }

            System.out.println("Survey Limit: " + survey.getSurveyLimit());
            System.out.println("------------------------------");
        }
    }
    private List<String> getSurveyChoicesBySurveyId(int surveyId) {
        List<String> choices = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT choice_text FROM survey_choices WHERE survey_id = ?");
        ) {
            preparedStatement.setInt(1, surveyId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                choices.add(resultSet.getString("choice_text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return choices;
    }
    private List<String> getUniqueSurveyCategories() {
        List<String> categories = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {
            String query = "SELECT DISTINCT category FROM survey";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        return categories;
    }
    private List<SurveyModel> getSurveysByCategory(String category) {
        List<SurveyModel> surveys = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM survey WHERE category = ?");
        ) {
            preparedStatement.setString(1, category);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                SurveyModel survey = new SurveyModel();
                survey.setId(resultSet.getInt("id"));
                survey.setQuestion(resultSet.getString("question"));
                survey.setSurveyLimit(resultSet.getInt("survey_limit"));

                surveys.add(survey);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        return surveys;
    }
    private int getChoiceCount(int surveyId, String choice) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT count_choice FROM survey_choices WHERE survey_id = ? AND choice_text = ?");
        ) {
            preparedStatement.setInt(1, surveyId);
            preparedStatement.setString(2, choice);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("count_choice");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        return 0;
    }
    private int getUserChoice(int max) {
        int userChoice;
        do {
            userChoice = scanner.nextInt();
        } while (userChoice < 1 || userChoice > max);
        return userChoice;
    }
    private void handleSQLException(SQLException e) {
        e.printStackTrace();
    }
    public static void main(String[] args) {
        AdminController adminController = new AdminController();
        adminController.displayAdminMenu();
    }
}
