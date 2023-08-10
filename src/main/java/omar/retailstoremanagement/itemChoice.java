package omar.retailstoremanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;

public class itemChoice {
    private Stage stage;
    @FXML
    private TableColumn<refDB, String> lb;
    @FXML
    private TableColumn<refDB, String> sz;
    @FXML
    private TableColumn<refDB, String> cl;
    private String size;
    private String user;
    private String color;

    @FXML
    private TableView<refDB> tb;
    private String ref;
    private ObservableList<refDB> data = FXCollections.observableArrayList();
    public void setMainStage(Stage stage) {
        this.stage = stage;
    }
    public void setRef(String ref){
        this.ref = ref;
        setDatabaseData();
    }
    public void initialize() {
        // Associate each TableColumn with the correct property of the refDB class
        lb.setCellValueFactory(new PropertyValueFactory<refDB, String>("label"));
        sz.setCellValueFactory(new PropertyValueFactory<refDB, String>("size"));
        cl.setCellValueFactory(new PropertyValueFactory<refDB, String>("color"));

    }
    public void setDatabaseData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            System.out.println("Connected to database");
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("USE stockdb");
                String query = "SELECT color, label, size FROM stock_mgmt WHERE ref = ?" + " AND store_user = '" + user + "'";
                try (PreparedStatement pstmt = con.prepareStatement(query)) {
                    pstmt.setString(1, ref);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        data.add(new refDB(rs.getString("label"), rs.getString("size"), rs.getString("color"), "", ref, "", "", ""));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tb.setItems(data);
    }
    @FXML
    void confirm(ActionEvent event) {
        if (tb.getSelectionModel().getSelectedItem()!= null){
            refDB selected = tb.getSelectionModel().getSelectedItem();
            System.out.println(selected.getLabel());
            this.size = selected.getSize();
            this.color = selected.getColor();
            stage.close();
        }
    }
    void cancel(ActionEvent event) {
        stage.close();
    }
    public String getSize() {
        return size;
    }
    public String getColor() {
        return color;
    }
    public void setUser(String user) {
        this.user = user;
    }


}


