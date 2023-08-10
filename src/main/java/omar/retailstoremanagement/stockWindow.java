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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import java.io.IOException;
import java.sql.*;

public class stockWindow {
    private String user;
    private ObservableList<refDB> data = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> storeBox;
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
    private TableColumn<refDB, String> quant;
    @FXML
    private TableColumn<?, String> store;

    @FXML
    public void showStock(){
        data.removeAll(data);
        try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("USE stockdb");
                if (storeBox.getValue() == null ) {
                    ResultSet rs = stmt.executeQuery("SELECT * FROM stock_mgmt, stock WHERE stock.ref = stock_mgmt.ref");
                    while (rs.next()) {
                        data.add(new refDB(rs.getString("label"), rs.getString("size"), rs.getString("color"), rs.getString("price"), rs.getString("ref"), "0",rs.getString("quantity"),rs.getString("store_user")));
                        tb.setItems(data);
                    }
                }
                else {
                    ResultSet rs = stmt.executeQuery("SELECT * FROM stock_mgmt, stock WHERE stock.ref = stock_mgmt.ref" +
                            " AND stock_mgmt.store_user = '" + storeBox.getValue() + "'");
                    while (rs.next()) {
                        data.add(new refDB(rs.getString("label"), rs.getString("size"), rs.getString("color"), rs.getString("price"), rs.getString("ref"), "0", rs.getString("quantity"), rs.getString("store_user")));
                        tb.setItems(data);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void salesWindow(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sales.fxml"));
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
    private void SetBoxStore() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("USE usersdb");
                ResultSet rs = stmt.executeQuery("SELECT username FROM users where has_access = 'user'");
                while (rs.next()) {
                    storeBox.getItems().add(rs.getString("username"));
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

    public void initialize() {
        // Associate each TableColumn with the correct property of the refDB class
        lb.setCellValueFactory(new PropertyValueFactory<>("label"));
        sz.setCellValueFactory(new PropertyValueFactory<>("size"));
        cl.setCellValueFactory(new PropertyValueFactory<>("color"));
        pr.setCellValueFactory(new PropertyValueFactory<>("price"));
        rf.setCellValueFactory(new PropertyValueFactory<>("ref"));
        quant.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        store.setCellValueFactory(new PropertyValueFactory<>("user"));
        SetBoxStore();

    }
    public void setUser(String user) {
        this.user = user;
    }



}
