package omar.retailstoremanagement;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.sql.*;

public class refDB {
    @FXML
    private TextField reftext;

    public String ref;
    public String label;
    public String price;
    public String size;
    public String color;
    public String date;
    public String user;
    public String quantity;

    public refDB(String Label, String size, String color, String price, String ref, String date, String quantity, String user) throws SQLException {
        this.label = Label;
        this.size = size;
        this.color = color;
        this.price = price;
        this.date = date;
        this.ref = ref;
        this.quantity = quantity;
        this.user = user;
    }

    @FXML
    public void setRef(String reftext) {
        this.ref = reftext;
    }

    public void setColor(String color) {
        this.color = color;
    }


    void setSize(String size) {
        this.size = size;
    }




    public int getData() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement statement = connection.createStatement()) {
                // Create and select a database for use
                statement.execute("USE stockdb");
                // Select users from the database
                ResultSet resultSet = statement.executeQuery("SELECT * FROM stock_mgmt");
                while (resultSet.next()) {
                    String orRef = resultSet.getString("ref");
                    String orLabel = resultSet.getString("label");
                    String orSize = resultSet.getString("size");
                    String orColor = resultSet.getString("color");

                    // Check if the username and password match any of the users
                    if (ref.equals(orRef)) {
                        this.label = orLabel;
                        this.size = orSize;
                        this.color = orColor;
                        ResultSet resultPrice = statement.executeQuery("SELECT price from stock WHERE ref = '" + ref + "'");
                        if (resultPrice.next()) {
                            String orPrice = resultPrice.getString("price");
                            this.price = String.format("%.3f", Double.parseDouble(orPrice));
                        } else {
                            this.price = "0";
                        }
                        return 1; // Exit the method after updating the values
                    }
                }
                return 0; // If no matching user found, display an error message or take appropriate action
                // If no matching user found, display an error message or take appropriate action
                // You may consider adding a label or dialog to inform the user that the reference was not found.
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
        return 0;
    }

    public int getMainData() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement statement = connection.createStatement()) {
                // Create and select a database for use
                statement.execute("USE stockdb");
                // Select users from the database
                ResultSet resultSet = statement.executeQuery("SELECT * FROM stock_mgmt");
                while (resultSet.next()) {
                    String orRef = resultSet.getString("ref");
                    String orLabel = resultSet.getString("label");
                    String orSize = resultSet.getString("size");
                    String orColor = resultSet.getString("color");


                    if (ref.equals(orRef) && this.color.equals(orColor) && this.size.equals(orSize)) {
                        this.label = orLabel;
                        this.size = orSize;
                        this.color = orColor;
                        ResultSet resultPrice = statement.executeQuery("SELECT price from stock WHERE ref = '" + ref + "'");
                        if (resultPrice.next()) {
                            String orPrice = resultPrice.getString("price");
                            this.price = String.format("%.3f", Double.parseDouble(orPrice));
                        } else {
                            this.price = "0";
                        }
                        return 1; // Exit the method after updating the values
                    }
                }
                return 0; // If no matching user found, display an error message or take appropriate action
                // If no matching user found, display an error message or take appropriate action
                // You may consider adding a label or dialog to inform the user that the reference was not found.
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
        return 0;
    }

    public String getLabel() {
        return label;
    }

    public String getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String getDate() {
        return date;
    }

    public String getRef() {
        return ref;
    }
    public String getQuantity() {
        return quantity;
    }
    public String getUser() {
        return user;
    }

}
