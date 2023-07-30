package omar.retailstoremanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.*;

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
    private ObservableList<refDB> data = FXCollections.observableArrayList();


    @FXML
    void search() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("USE stockdb");
                ResultSet rs = stmt.executeQuery("SELECT label, size, color, total FROM stock WHERE ref");
                while (rs.next()) {
                    data.add(new refDB(rs.getString("label"), rs.getString("size"), rs.getString("color"), rs.getString("price")));
                    tb.setItems(data);
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
    }



}
