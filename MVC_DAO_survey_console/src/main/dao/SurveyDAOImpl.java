package main.dao;
import main.model.SurveyModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class SurveyDAOImpl implements SurveyDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/survey_console";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    @Override
    public boolean addSurvey(SurveyModel survey) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO survey (question, number_of_choices, category, published, survey_limit) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, survey.getQuestion());
            statement.setInt(2, survey.getNumberOfChoices());
            statement.setString(3, survey.getCategory());
            statement.setBoolean(4, survey.isPublished());
            statement.setInt(5, survey.getSurveyLimit());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    survey.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }
            addChoicesForSurvey(survey.getId(), survey.getChoices());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private void addChoicesForSurvey(int surveyId, List<String> choices) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO survey_choices (survey_id, choice_text) VALUES (?, ?)")) {

            for (String choice : choices) {
                statement.setInt(1, surveyId);
                statement.setString(2, choice);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean removeSurvey(int surveyId) {
        return false;
    }
    @Override
    public List<SurveyModel> getAllSurveys() {

        return new ArrayList<>();
    }
    @Override
    public SurveyModel getSurveyById(int surveyId) {
        return null;
    }
    @Override
    public boolean publishSurvey(int surveyId) {

        return false;
    }

    @Override
    public List<String> getSurveyChoices(int surveyId) {
        List<String> choices = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT choice_text FROM survey_choices WHERE survey_id = ?");
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

    @Override
    public String getSurveyCategory(int surveyId) {
        return null;
    }
    @Override
    public List<String> getUniqueSurveyCategories() {
        List<String> categories = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT category FROM survey");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    @Override
    public boolean incrementChoiceCount(int surveyId, List<Integer> choiceIndexes) {
        return false;
    }
    @Override
    public boolean incrementChoiceCount(int surveyId, String userVote) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String updateQuery = "UPDATE survey_choices SET count_choice = count_choice + 1 " +
                    "WHERE choice_text = ? AND survey_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, userVote);
                statement.setInt(2, surveyId);

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    System.out.println("UserVote '" + userVote + "' not found. Please try again.");
                    return false;
                }
            }

            System.out.println("Vote received successfully for choice: " + userVote);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public List<SurveyModel> getSurveysByCategory(String category) {
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
                surveys.add(survey);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return surveys;
    }
    @Override
    public int getSurveyIdByCategory(String selectedCategory) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM survey WHERE category = ?");
        ) {
            preparedStatement.setString(1, selectedCategory);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    public List<SurveyModel> getAllSurveysWithChoices() {
        return null;
    }
    private String getChoiceTextByIndex(int surveyId, int choiceIndex) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT choice_text FROM survey_choices WHERE survey_id = ? AND id = ?");
        ) {
            preparedStatement.setInt(1, surveyId);
            preparedStatement.setInt(2, choiceIndex);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("choice_text");
            } else {
                System.out.println("Choice text not found for surveyId: " + surveyId + ", choiceIndex: " + choiceIndex);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<SurveyModel> getAllSurveysWithChoices(String category) {
        List<SurveyModel> surveys = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM survey WHERE category = ?");
        ) {
            preparedStatement.setString(1, category);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    SurveyModel survey = new SurveyModel();
                    survey.setId(resultSet.getInt("id"));
                    survey.setQuestion(resultSet.getString("question"));
                    survey.setNumberOfChoices(resultSet.getInt("number_of_choices"));
                    survey.setCategory(resultSet.getString("category"));
                    survey.setPublished(resultSet.getBoolean("published"));
                    survey.setSurveyLimit(resultSet.getInt("survey_limit"));
                    List<String> choices = getSurveyChoices(survey.getId());
                    survey.setChoices(choices);

                    surveys.add(survey);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return surveys;
    }
    @Override
    public int getChoiceCount(int surveyId, String choice) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT count_choice FROM survey_choices WHERE survey_id = ? AND choice_text = ?");
        ) {
            preparedStatement.setInt(1, surveyId);
            preparedStatement.setString(2, choice);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("count_choice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    @Override
    public int getSurveyLimit(int surveyId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT survey_limit FROM survey WHERE id = ?");
        ) {
            preparedStatement.setInt(1, surveyId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("survey_limit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    @Override
    public List<SurveyModel> getSurveysWithReports() {
        List<SurveyModel> surveysWithReports = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM survey WHERE survey_limit = (SELECT COUNT(DISTINCT choice_text) FROM survey_choices WHERE survey_id = survey.id)";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                SurveyModel survey = new SurveyModel();
                survey.setId(resultSet.getInt("id"));
                survey.setQuestion(resultSet.getString("question"));
                survey.setSurveyLimit(resultSet.getInt("survey_limit"));
                survey.setChoices(getSurveyChoices(survey.getId()));
                surveysWithReports.add(survey);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return surveysWithReports;
    }
    @Override
    public int getTotalChoiceCount(int surveyId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(count_choice) FROM survey_choices WHERE survey_id = ?");
        ) {
            preparedStatement.setInt(1, surveyId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
