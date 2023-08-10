package omar.retailstoremanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.*;
import java.time.LocalDate;

public class salesWindow {
    private String user;
    @FXML
    private Label sTotal;
    @FXML
    private TableView<refDB> tb;
    @FXML
    private TableColumn<refDB, String> lb;
    @FXML
    private TableColumn<refDB, String> sz;
    @FXML
    private TableColumn<refDB, String> cl;
    @FXML
    private TableColumn<refDB, String> pr;
    @FXML
    private TableColumn<refDB, String> rf;
    @FXML
    private TableColumn<refDB, String> date;
    @FXML
    private TableColumn<refDB, String> quant;
    @FXML
    private ComboBox<String> storeUser;
    @FXML
    private Label totalPrice;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField refPicker;
    private ObservableList<refDB> data = FXCollections.observableArrayList();


    @FXML
    void search() {
        data.removeAll(data);
        double supposedTotal = 0;
        double total = 0;
        totalPrice.setText("");
        System.out.println(refPicker.getText());
        if (storeUser.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No store selected");
            alert.setContentText("Please select a store");
            alert.showAndWait();
            return;
        }
        try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("USE stockdb");
                if (refPicker.getText().equals("")){
                    ResultSet rs = stmt.executeQuery("SELECT pr.ref, pr.size, price, pr.label, total, pr.color, payment_date\n" +
                            "FROM stock as s, payments as p, payment_references as pr\n" +
                            "WHERE s.ref = pr.ref AND p.payment_id = pr.payment_id\n" +
                            "AND store_user = '" + storeUser.getValue() + "'\n" +
                            "AND payment_date = '" + datePicker.getValue() + "'");
                    while (rs.next()) {
                        supposedTotal += rs.getDouble("price");
                        String formattedPrice = String.format("%.3f", Double.parseDouble(rs.getString("price")));
                        data.add(new refDB(rs.getString("label"), rs.getString("size"), rs.getString("color"), formattedPrice, rs.getString("ref"), rs.getString("payment_date"), "0",""));
                        total += rs.getDouble("total");
                        tb.setItems(data);
                    }
                    ResultSet rs2 = stmt.executeQuery("SELECT SUM(total) as total FROM payments, payment_references\n" +
                            "WHERE payment_date = '" + datePicker.getValue() + "'\n" +
                            "AND store_user = '" + storeUser.getValue() + "'\n" +
                            "AND payments.payment_id = payment_references.payment_id");
                    while (rs2.next()){
                        totalPrice.setText(String.format("Total (With Discounts): %.3fDT", rs2.getDouble("total")));
                    }
                }
                else {
                    ResultSet rs = stmt.executeQuery("SELECT pr.ref, pr.size, price, pr.label, total, pr.color, payment_date\n" +
                            "FROM stock as s, payments as p, payment_references as pr\n" +
                            "WHERE s.ref = pr.ref AND p.payment_id = pr.payment_id\n" +
                            "AND store_user = '" + storeUser.getValue() + "'\n" +
                            "AND pr.ref = '" + refPicker.getText() + "' AND p.payment_date = '" + datePicker.getValue() + "'");
                    while (rs.next()) {
                        supposedTotal += rs.getDouble("price");
                        String formattedPrice = String.format("%.3f", Double.parseDouble(rs.getString("price")));
                        data.add(new refDB(rs.getString("label"), rs.getString("size"), rs.getString("color"), formattedPrice, rs.getString("ref"), rs.getString("payment_date"), "0",""));
                        total += rs.getDouble("total");
                        tb.setItems(data);
                    }
                    ResultSet rs2 = stmt.executeQuery("SELECT SUM(total) as total FROM payments, payment_references\n" +
                            "WHERE payments.payment_id = payment_references.payment_id\n" +
                            "AND payment_references.ref = '" + refPicker.getText() + "\n'" +
                            "AND store_user = '" + storeUser.getValue() + "'\n" +
                            "AND payments.payment_date = '" + datePicker.getValue() + "'");
                    while (rs2.next()){
                        totalPrice.setText(String.format("Total (With Discounts): %.3fDT", rs2.getDouble("total")));
                    }
                }


            sTotal.setText(String.format("Total: %.3fDT", supposedTotal));


            }
        }
        catch (SQLException e) {
            e.printStackTrace();

        }

    }
    @FXML
    public void chartsWindow(MouseEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("charts.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            chartsWindow charts = loader.getController();
            charts.setUser(user);
            stage.setTitle("Charts");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void addPayments(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MainWindow main = loader.getController();
            main.setUser(user);
            stage.setTitle("Sales");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void SetBoxStore() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("USE usersdb");
                ResultSet rs = stmt.executeQuery("SELECT username FROM users where has_access = 'user'");
                while (rs.next()) {
                    storeUser.getItems().add(rs.getString("username"));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void initialize() {
        // Associate each TableColumn with the correct property of the refDB class
        lb.setCellValueFactory(new PropertyValueFactory<refDB, String>("label"));
        sz.setCellValueFactory(new PropertyValueFactory<refDB, String>("size"));
        cl.setCellValueFactory(new PropertyValueFactory<refDB, String>("color"));
        pr.setCellValueFactory(new PropertyValueFactory<refDB, String>("price"));
        rf.setCellValueFactory(new PropertyValueFactory<refDB, String>("ref"));
        date.setCellValueFactory(new PropertyValueFactory<refDB, String>("date"));
        datePicker.setValue(LocalDate.now());
        SetBoxStore();

    }
    @FXML
    void stockWindow(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("stock.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stockWindow stock = loader.getController();
            stock.setUser(user);
            stage.setTitle("Stock");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void setUser(String user) {
        this.user = user;
    }



}
