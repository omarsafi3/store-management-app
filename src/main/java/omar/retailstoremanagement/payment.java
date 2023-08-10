package omar.retailstoremanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import net.synedra.validatorfx.Check;

import java.time.LocalDate;
import java.sql.*;
import java.util.List;


public class payment
{

    private Stage stage;
    public boolean selected = true;

    @FXML
    private Button cancel;

    @FXML
    private RadioButton cash;
    @FXML
    private RadioButton amex;

    @FXML
    private Button confirm;

    @FXML
    private RadioButton mcard;
    private double totalAmount;
    private List<String> refList;
    private List<String> colorList;
    private List<String> sizeList;
    private List<String> labelList;
    private double discount = 0;

    @FXML
    private RadioButton visa;
    // The SQL statements to insert a payment record and its reference
    private static final String INSERT_PAYMENT_SQL = "INSERT INTO payments (payment_date ,total, pay_method, discount) VALUES (?, ?, ?, ?)";
    private static final String INSERT_PAYMENT_REFERENCE_SQL = "INSERT INTO payment_references (payment_id, ref, color, size, label, store_user) VALUES (?, ?, ? ,?, ?, ?)";
    private String user;

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void cancel(ActionEvent event) {
        if (stage != null) {
            stage.close();
        }
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public void setRefList(List<String> refList) {
        this.refList = refList;
    }
    public void setColorList(List<String> colorList) {
        this.colorList = colorList;
    }
    public void setSizeList(List<String> sizeList) {
        this.sizeList = sizeList;
    }
    public void setLabelList(List<String> labelList) {
        this.labelList = labelList;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    private static int insertPayment(Connection connection, LocalDate paymentDate , double total, String pay_method, Double discount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PAYMENT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the SQL statement
            statement.setObject(1, paymentDate);
            statement.setDouble(2, total);
            statement.setString(3, pay_method);
            statement.setDouble(4, discount);

            // Execute the SQL statement to insert the payment record
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                // Get the generated payment_id
                ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

            return -1; // Return -1 if the payment record insertion failed
        }

    private static int CheckQuantity(Connection connection, List<String> references, List<String> colors, List<String> sizes, List<String> labels) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("USE stockdb");
            for (int i = 0; i < references.size(); i++) {
                String ref = references.get(i);
                String color = colors.get(i);
                String size = sizes.get(i);
                String label = labels.get(i);
                ResultSet rs = stmt.executeQuery("SELECT quantity FROM stock_mgmt WHERE ref = '" + ref + "' AND color = '" + color + "' AND size = '" + size + "' AND label = '" + label + "'");
                while (rs.next()) {
                    int quantity = rs.getInt("quantity");
                    if (quantity == 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error");
                        alert.setContentText("There is no more stock for item " + ref + " " + color + " " + size + " " + label);
                        alert.showAndWait();
                        return 0;
                    }
                }
            }
        }
        return 1;
    }
    private static void insertPaymentReferences(Connection connection, int paymentId, List<String> references, List<String> colors, List<String> sizes, List<String> labels, String user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PAYMENT_REFERENCE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Loop through all the references and add them as a batch  to the SQL statement to insert payment references
            // This is more efficient than inserting one by one{
            for (int i = 0; i < references.size(); i++)
            {
                String ref = references.get(i);
                String color = colors.get(i);
                String size = sizes.get(i);
                String label = labels.get(i);


                // Set the parameters for the SQL statement
                statement.setInt(1, paymentId);
                statement.setString(2, ref);
                statement.setString(3, color);
                statement.setString(4, size);
                statement.setString(5, label);
                statement.setString(6, user);

                // Add the batch for execution
                statement.addBatch();
            }

            // Execute the batch of SQL statements to insert payment references
            statement.executeBatch();
        }

    }
    public void setUser(String user) {
        this.user = user;
    }
    public void updateQuantityInDatabase(Connection connection, List<String> references, List<String> colors, List<String> sizes, List<String> labels) {
        int newQuantity = 0;
        System.out.println("Updating quantity in database");
            try (Statement stmt = connection.createStatement()) {
                System.out.println("here");
                System.out.println("Connecting to database...");
                stmt.executeUpdate("USE stockdb");
                for (int i = 0; i < references.size(); i++) {
                    System.out.println("Updating quantity for item " + i);
                    String ref = references.get(i);
                    String color = colors.get(i);
                    String size = sizes.get(i);
                    String label = labels.get(i);
                    ResultSet rs = stmt.executeQuery("SELECT quantity FROM stock_mgmt WHERE ref = '" + ref + "'\n" +
                            "AND color = '" + color + "'\n" +
                            "AND size = '" + size + "'\n" +
                            "AND label = '" + label + "'"
                    );


                    if (rs.next()) {
                        int oldQuantity = rs.getInt("quantity");
                        System.out.println(oldQuantity);
                        newQuantity = oldQuantity - 1;
                        System.out.println(newQuantity);
                        String updateQuery = "UPDATE stock_mgmt SET quantity = " + newQuantity + " WHERE ref = '" + ref + "'\n" +
                                "AND color = '" + color + "'\n" +
                                "AND size = '" + size + "'\n" +
                                "AND label = '" + label + "'";
                        stmt.executeUpdate(updateQuery);
                    }
                    else{
                        System.out.println("No such item in stock");
                    }
                    // Assuming "stock" is the name of the table where the quantity is stored
                    // and "quantity" is the name of the column holding the quantity value

                }
            }
         catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void confirm(ActionEvent event) {
        LocalDate paymentDate = LocalDate.now();

        // References data (assuming you have a list of references)

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306/stockdb", "omar", "Amrou2009@")) {
            selected = true;
            String pay_method = "";
            if (cash.isSelected()) {
                pay_method = "cash";
            } else if (mcard.isSelected()) {
                pay_method = "mcard";
            } else if (visa.isSelected()) {
                pay_method = "visa";
            } else if (amex.isSelected()) {
                pay_method = "amex";
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Payment method not selected");
                alert.setContentText("Please select a payment method");
                alert.showAndWait();
                selected = false;

            }
            if (CheckQuantity(connection, refList, colorList, sizeList, labelList) == 1) {


            // Insert payment record
            int paymentId = insertPayment(connection, paymentDate, totalAmount, pay_method, discount);

            if (paymentId != -1 && selected) {
                updateQuantityInDatabase(connection, refList, colorList, sizeList, labelList);
                // Insert payment references
                insertPaymentReferences(connection, paymentId, refList, colorList, sizeList, labelList, user);
                System.out.println("Payment record and references inserted successfully.");

                if (stage != null) {
                    stage.close();
                }
                } else {
                    System.out.println("Failed to insert payment record.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

