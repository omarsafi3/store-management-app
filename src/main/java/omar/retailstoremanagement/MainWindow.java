package omar.retailstoremanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.VBox;

public class MainWindow {
    private Stage stage;
    private Scene scene;



    @FXML
    private Button confirm;

    private double total;
    private ObservableList<refDB> data = FXCollections.observableArrayList();

    @FXML
    private Label nameLabel;

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
    public TextField reftext;

    @FXML
    private AnchorPane rootNode;

    @FXML
    private Button payment;
    @FXML
    private Button refGet;
    @FXML
    private Label totalPrice;
    @FXML
    private Button deleteLastAdded;
    List<String> refList = new ArrayList<>();



    public void initialize() {
        // Associate each TableColumn with the correct property of the refDB class
        lb.setCellValueFactory(new PropertyValueFactory<>("label"));
        sz.setCellValueFactory(new PropertyValueFactory<>("size"));
        cl.setCellValueFactory(new PropertyValueFactory<>("color"));
        pr.setCellValueFactory(new PropertyValueFactory<>("price"));
    }


    public void setTableValues(refDB refdb) throws Exception {
        try {


            // Create an ObservableList and add the TableProperties object to it

            data.add(refdb);
            tb.setItems(data);
        }
        catch(Exception e) {
            System.out.println("Error");
        }


    }
    @FXML
    void confirm(ActionEvent event) throws Exception {

        refDB refdb = new refDB(null, null, null, null);
        refdb.setRef(reftext.getText());
        if (refdb.getData() == 1) {
            total = total + Double.parseDouble(refdb.getPrice());
            totalPrice.setText(String.format("Total: %.3fDT", total));
            refList.add(reftext.getText());
            setTableValues(refdb);
            reftext.clear();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Reference not found");
            alert.setContentText("Please enter a valid reference");
            alert.showAndWait();
        }


    }
    @FXML
    void deleteLastAdded(ActionEvent event) {
        total = total - Double.parseDouble(data.get(data.size()-1).getPrice());
        totalPrice.setText(String.format("Total: %.3fDT", total));
        data.remove(data.size()-1);
        tb.refresh();
        refList.remove(refList.size()-1);
    }

    @FXML
    void pay(ActionEvent event) {
        try {
            // Load the FXML file for the new UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("payment.fxml"));
            Parent root = loader.load();

            // Get the controller for the new UI
            payment paymentWindow = loader.getController();
            paymentWindow.setTotalAmount(total);
            paymentWindow.setRefList(refList);



            // Create a new stage for the new UI
            Stage newStage = new Stage();
            newStage.setTitle("Payment");


            // Set the scene with the new UI content
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            paymentWindow.setMainStage(newStage);

            // Show the new stage
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}




