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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class salesWindow {


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
    private Label totalPrice;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField refPicker;
    private ObservableList<refDB> data = FXCollections.observableArrayList();


    @FXML
    void search() {
        data.removeAll(data);
        double total = 0;
        totalPrice.setText("");
        System.out.println(refPicker.getText());
        try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("USE stockdb");
                if (refPicker.getText().equals("")){
                    ResultSet rs = stmt.executeQuery("SELECT pr.ref, size, price, label, color, payment_date\n" +
                            "FROM stock as s, payments as p, payment_references as pr\n" +
                            "WHERE s.ref = pr.ref AND p.payment_id = pr.payment_id\n" +
                            "AND p.payment_date = '" + datePicker.getValue() + "'");
                    while (rs.next()) {
                        String formattedPrice = String.format("%.3f", Double.parseDouble(rs.getString("price")));
                        data.add(new refDB(rs.getString("label"), rs.getString("size"), rs.getString("color"), formattedPrice, rs.getString("ref"), rs.getString("payment_date")));
                        tb.setItems(data);
                    }
                }
                else {
                    ResultSet rs = stmt.executeQuery("SELECT pr.ref, size, price, label, color, payment_date\n" +
                            "FROM stock as s, payments as p, payment_references as pr\n" +
                            "WHERE s.ref = pr.ref AND p.payment_id = pr.payment_id\n" +
                            "AND pr.ref = '" + refPicker.getText() + "' AND p.payment_date = '" + datePicker.getValue() + "'");
                    while (rs.next()) {
                        String formattedPrice = String.format("%.3f", Double.parseDouble(rs.getString("price")));
                        data.add(new refDB(rs.getString("label"), rs.getString("size"), rs.getString("color"), formattedPrice, rs.getString("ref"), rs.getString("payment_date")));
                        tb.setItems(data);
                    }
                }

                for (refDB refDB : data) {
                    total += Double.parseDouble(refDB.getPrice());
                    totalPrice.setText(String.format("Total : %.3fDT", total));
            }

            }
        }
        catch (SQLException e) {
            e.printStackTrace();

        }

    }
    @FXML
    public void addPayments(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Sales");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) {
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

    }



}
