package omar.retailstoremanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.sql.*;
import java.io.IOException;


public class login {
    @FXML
    TextField user;
    @FXML
    TextField pass;
    @FXML
    Label wrong;
    double width = 1600;
    double height = 979;


    public void confirm(ActionEvent event) throws IOException {
        String username = user.getText();
        String password = pass.getText();


        // Connect to the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement statement = connection.createStatement()) {

                // Create and select a database for use

                statement.execute("USE usersdb");
                // Select users from the database
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
                while (resultSet.next()) {
                    String name = resultSet.getString("username");
                    String storedPassword = resultSet.getString("pass");
                    // Check if the username and password match any of the users
                    if (username.equals(name) && password.equals(storedPassword)) {
                        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(fxmlLoader.load());
                        MainWindow mainWindow = fxmlLoader.getController();
                        mainWindow.setUser(name);
                        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                        stage.setX((screenBounds.getWidth() - width) / 2);
                        stage.setY((screenBounds.getHeight() - height) / 2);
                        stage.setScene(scene);
                        stage.show();
                        return; // Return after successful login
                    }

                }
                // If no matching user found, display an error message or take appropriate action
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid username or password.");
                alert.setContentText("Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }}