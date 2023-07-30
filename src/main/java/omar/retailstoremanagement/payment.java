package omar.retailstoremanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

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

    @FXML
    private RadioButton visa;
    // The SQL statements to insert a payment record and its reference
    private static final String INSERT_PAYMENT_SQL = "INSERT INTO payments (payment_date, total, pay_method) VALUES (?, ?, ?)";
    private static final String INSERT_PAYMENT_REFERENCE_SQL = "INSERT INTO payment_references (payment_id, ref) VALUES (?, ?)";


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
    private static int insertPayment(Connection connection, LocalDate paymentDate, double total, String pay_method) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PAYMENT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the SQL statement
            statement.setObject(1, paymentDate);
            statement.setDouble(2, total);
            statement.setString(3, pay_method);

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


    private static void insertPaymentReferences(Connection connection, int paymentId, List<String> references) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PAYMENT_REFERENCE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            for (String ref : references) {

                // Set the parameters for the SQL statement
                statement.setInt(1, paymentId);
                statement.setString(2, ref);

                // Add the batch for execution
                statement.addBatch();
            }

            // Execute the batch of SQL statements to insert payment references
            statement.executeBatch();
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
            // Insert payment record
            int paymentId = insertPayment(connection, paymentDate, totalAmount, pay_method);

            if (paymentId != -1 && selected) {
                // Insert payment references
                insertPaymentReferences(connection, paymentId, refList);
                System.out.println("Payment record and references inserted successfully.");
                if (stage != null) {
                    stage.close();
                }

            } else {
                System.out.println("Failed to insert payment record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

