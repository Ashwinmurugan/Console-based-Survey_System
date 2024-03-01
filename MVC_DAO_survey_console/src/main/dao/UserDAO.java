package main.dao;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import main.model.UserModel;
public class UserDAO {
    private Connection connection;
    public UserDAO(){
        initializeDatabaseConnection();
    }
    private void initializeDatabaseConnection() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/survey_console";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error connecting to the database..");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean registerUser(UserModel user) {
        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean loginUser(UserModel user) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
