package main.controller;
import main.model.SurveyModel;
import main.model.UserModel;
import main.dao.SurveyDAO;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
public class UserMenuController {
    private SurveyDAO surveyDAO;
    public List<String> getUniqueSurveyCategories() {
        return surveyDAO.getUniqueSurveyCategories();
    }
    public int getChoiceCount(int surveyId, String choice) {
        return surveyDAO.getChoiceCount(surveyId, choice);
    }
    private Scanner scanner;
    private UserModel user;
    public UserMenuController(UserModel user, SurveyDAO surveyDAO) {
        this.user = user;
        this.surveyDAO = surveyDAO;
        this.scanner = new Scanner(System.in);
    }
    public void showUserMenu() {
        boolean continueMenu = true;
        while (continueMenu) {
            System.out.println("Welcome to the User Menu, " + user.getUsername() + "!");
            System.out.println("1. View List of Survey Available");
            System.out.println("2. See Survey Results");
            System.out.println("3. See Survey Reports/Decision");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        displaySurveyCategories();
                        break;
                    case 2:
                        surveyResult();
                        break;
                    case 3:
                        surveyReports();
                        break;
                    case 4:
                        System.out.println("Logging out. " + getMotivationalQuote() + " Goodbye, " + user.getUsername() + "!");
                        continueMenu = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
    private void displaySurveyCategories() {
    List<String> categories = surveyDAO.getUniqueSurveyCategories();
    if (categories == null || categories.isEmpty()) {
        System.out.println("No survey categories available.");
    } else {
        System.out.println("Available Survey Categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.print("Enter the number of the category you are interested in(Only Number): ");
        int categoryChoice = getUserChoice(categories.size());
        if (categoryChoice > 0 && categoryChoice <= categories.size()) {
            String selectedCategory = categories.get(categoryChoice - 1);
            displaySurveyDetails(selectedCategory);
        } else {
            System.out.println("Invalid category choice. Please try again.");
        }
    }
}
    private void displaySurveyDetails(String selectedCategory) {
        List<SurveyModel> surveys = surveyDAO.getSurveysByCategory(selectedCategory);
        if (surveys == null || surveys.isEmpty()) {
            System.out.println("No surveys available for the selected category.");
        } else {
            System.out.println("Available Surveys:");
            for (int i = 0; i < surveys.size(); i++) {
                System.out.println((i + 1) + ". " + surveys.get(i).getQuestion());
            }
            System.out.print("Enter the survey you are interested in (Only Number): ");
            try {
                int surveyChoice = Integer.parseInt(scanner.nextLine());
                if (surveyChoice > 0 && surveyChoice <= surveys.size()) {
                    SurveyModel selectedSurvey = surveys.get(surveyChoice - 1);
                    voteForChoices(selectedSurvey);
                } else {
                    System.out.println("Invalid survey choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    private void voteForChoices(SurveyModel selectedSurvey) {
        if (selectedSurvey == null) {
            System.out.println("No survey selected.");
            return;
        }
        List<String> choices = surveyDAO.getSurveyChoices(selectedSurvey.getId());
        if (choices == null || choices.isEmpty()) {
            System.out.println("No choices available for the selected survey.");
            return;
        }
        System.out.println("Choices:");
        for (int i = 0; i < choices.size(); i++) {
            System.out.println((i + 1) + ". " + choices.get(i));
        }
        System.out.print("Enter the choice you want to vote for(Type As its): ");
        String userVote = scanner.nextLine();
        System.out.println(" YourVote = " + userVote);
        if (choices.contains(userVote)) {
            System.out.println(" Choice is valid.");
            boolean success = surveyDAO.incrementChoiceCount(selectedSurvey.getId(), userVote);
            if (success) {
                System.out.println("Vote received for choice: " + userVote);
            } else {
                System.out.println("Failed to update vote. Please try again.");
            }
        } else {
            System.out.println(" Invalid choice. Please try again.");
        }
    }
    private void surveyReports() {
        List<String> uniqueCategories = surveyDAO.getUniqueSurveyCategories();
        if (uniqueCategories.isEmpty()) {
            System.out.println("No survey categories available.");
        } else {
            System.out.println("Available Survey Categories:");
            for (int i = 0; i < uniqueCategories.size(); i++) {
                System.out.println((i + 1) + ". " + uniqueCategories.get(i));
            }
            System.out.print("Enter the number of the category for which you want to see survey reports: ");
            int categoryChoice = getUserChoice(uniqueCategories.size());
            if (categoryChoice > 0 && categoryChoice <= uniqueCategories.size()) {
                String selectedCategory = uniqueCategories.get(categoryChoice - 1);
                List<SurveyModel> surveysByCategory = surveyDAO.getSurveysByCategory(selectedCategory);
                if (surveysByCategory.isEmpty()) {
                    System.out.println("No surveys available for the selected category.");
                } else {
                    System.out.println("Survey Reports/Decisions for Category: " + selectedCategory);
                    for (SurveyModel survey : surveysByCategory) {
                        int totalChoiceCount = surveyDAO.getTotalChoiceCount(survey.getId());
                        if (totalChoiceCount >= survey.getSurveyLimit()) {
                            System.out.println("Survey Question: " + survey.getQuestion());
                            System.out.println("Total Choice Count: " + totalChoiceCount);
//                            System.out.println("Survey Limit: " + survey.getSurveyLimit());
                            System.out.println("Survey Limit: ****");

                            System.out.println();
                        }
                    }
                }
            } else {
                System.out.println("Invalid category choice. Please try again.");
            }
        }
    }
    private int getUserChoice(int maxChoice) {
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 1 && choice <= maxChoice) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and " + maxChoice + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }

        return choice;
    }
    private void surveyResult() {
        List<String> categories = surveyDAO.getUniqueSurveyCategories();

        if (categories == null || categories.isEmpty()) {
            System.out.println("No survey categories available.");
        } else {
            System.out.println("Available Survey Categories:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i));
            }
            System.out.print("Enter the number of the category you are interested in(Only Number): ");
            int categoryChoice = getUserChoice(categories.size());

            if (categoryChoice > 0 && categoryChoice <= categories.size()) {
                String selectedCategory = categories.get(categoryChoice - 1);
                displaySurveyResultsForCategory(selectedCategory);
            } else {
                System.out.println("Invalid category choice. Please try again.");
            }
        }
    }
    private void displaySurveyResultsForCategory(String category) {
        List<SurveyModel> surveys = surveyDAO.getAllSurveysWithChoices(category);
        if (surveys.isEmpty()) {
            System.out.println("No surveys available for the selected category.");
        } else {
            System.out.println("Survey Results for Category: " + category);
            for (SurveyModel survey : surveys) {
                System.out.println("Survey Question: " + survey.getQuestion());
                System.out.println("Choices and Counts:");

                for (String choice : survey.getChoices()) {
                    int count = surveyDAO.getChoiceCount(survey.getId(), choice);
                    System.out.println(choice + ": " + count);
                }
                //if you wish you can print limit
//               System.out.println("Survey Limit: " + survey.getSurveyLimit());
                System.out.println("Survey Limit: ****");
                System.out.println("------------------------------");
            }
        }
    }
    private String getMotivationalQuote() {
        String[] quotes = {
                "Your opinions matter! Every vote counts.",
                "Together, we can make a difference.",
                "Empower yourself through surveys.",
                "Be the change you want to see.",
                "Your voice shapes the future.",
                "Take a survey, shape a better tomorrow.",
                "One survey at a time, building a brighter future.",
                "Your insights fuel positive change.",
                "Survey by survey, making the world a better place.",
                "In the diversity of opinions lies the strength of progress.",
                "Let your voice echo for generations to come.",
                "Every survey is a step towards a more informed world.",
                "As diverse as our nation, as powerful as our unity.",
                "India's strength is in the voice of its people.",
        };

        int randomIndex = (int) (Math.random() * quotes.length);
        return quotes[randomIndex];
    }
}
