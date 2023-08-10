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
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MainWindow {

    private double totalWithDiscount = 0;
    private double totalDiscount = 0;
    private String user;
    private double total;
    private ObservableList<refDB> data = FXCollections.observableArrayList();
    @FXML
    private TextField discountField;

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
    public TextField reftext;

    @FXML
    private Button payment;

    @FXML
    private Label totalPrice;

    List<String> refList = new ArrayList<>();
    private List<String> colorList = new ArrayList<>();
    private List<String> sizeList = new ArrayList<>();
    private List<String> labelList = new ArrayList<>();

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void initialize() {
        // Associate each TableColumn with the correct property of the refDB class
        lb.setCellValueFactory(new PropertyValueFactory<>("label"));
        sz.setCellValueFactory(new PropertyValueFactory<>("size"));
        cl.setCellValueFactory(new PropertyValueFactory<>("color"));
        pr.setCellValueFactory(new PropertyValueFactory<>("price"));
        rf.setCellValueFactory(new PropertyValueFactory<>("ref"));
        quant.setCellValueFactory(new PropertyValueFactory<>("quantity"));

    }
    void setTableValues(refDB refDB) {
        // Set the values of the table
        data.add(refDB);
        tb.setItems(data);
    }



    @FXML
    void confirm(ActionEvent event) throws Exception {
        System.out.println(reftext.getText());
        String refChoice = reftext.getText();

        refDB refdb = new refDB(null, null, null, null, null, null, "1", user);


        if (isNumeric(reftext.getText())) {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
                try (Statement stmt = con.createStatement()) {
                    stmt.executeUpdate("USE stockdb");
                    ResultSet rs = stmt.executeQuery("SELECT ref, color, size FROM stock_mgmt WHERE barcode = '" + reftext.getText() + "'" +
                            "AND store_user = '" + user + "'");
                    if (rs.next()) {
                        refChoice = rs.getString("ref");
                        refdb.setColor(rs.getString("color"));
                        refdb.setSize(rs.getString("size"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            refdb.setRef(refChoice);
            if (refdb.getMainData() == 1) {
                refdb.getMainData();
                total = total + Double.parseDouble(refdb.getPrice());
                totalPrice.setText(String.format("Total: %.3fDT", total));
                refList.add(refdb.getRef());
                colorList.add(refdb.getColor());
                sizeList.add(refdb.getSize());
                labelList.add(refdb.getLabel());
                setTableValues(refdb);
                payment.setDisable(false);
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Reference not found");
                alert.setContentText("Please enter a valid reference");
                alert.showAndWait();
            }


        }
        else {
            refdb.setRef(refChoice);

            if (refdb.getData() == 1) {

                try {
                    // Load the FXML file for the new UI
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("itemChoice.fxml"));
                    Parent root = loader.load();

                    itemChoice item = loader.getController();
                    item.setUser(user);
                    item.setRef(refChoice);
                    Stage newStage = new Stage();
                    newStage.setTitle("Payment");

                    Scene scene = new Scene(root);
                    newStage.setScene(scene);

                    item.setMainStage(newStage);


                    newStage.showAndWait();

                    refdb.setColor(item.getColor());
                    refdb.setSize(item.getSize());
                    refdb.getMainData();
                    total = total + Double.parseDouble(refdb.getPrice());
                    totalPrice.setText(String.format("Total: %.3fDT", total));
                    refList.add(refdb.getRef());
                    colorList.add(refdb.getColor());
                    sizeList.add(refdb.getSize());
                    labelList.add(refdb.getLabel());
                    setTableValues(refdb);
                    payment.setDisable(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /*  */

            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Reference not found");
                alert.setContentText("Please enter a valid reference");
                alert.showAndWait();
            }
        }


    }
    @FXML
    void applyDiscount(ActionEvent event) {
        if (discountField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Discount field is empty");
            alert.setContentText("Please enter a valid discount");
            alert.showAndWait();
        }
        else {
            int DiscountPercent = Integer.parseInt(discountField.getText());
            totalDiscount = (total * DiscountPercent) / 100;
            total = total - totalDiscount;
            totalPrice.setText(String.format("Total: %.3fDT(With Discount): ", total));
            discountField.clear();
        }

    }
    @FXML
    void deleteLastAdded(ActionEvent event) {
        total = total - Double.parseDouble(data.get(data.size()-1).getPrice());
        totalPrice.setText(String.format("Total: %.3fDT", total));
        data.remove(data.size()-1);
        tb.refresh();
        refList.remove(refList.size()-1);
        colorList.remove(colorList.size()-1);
        sizeList.remove(sizeList.size()-1);
        labelList.remove(labelList.size()-1);
        if (data.isEmpty()) {
            payment.setDisable(true);
            total = 0;
            totalPrice.setText(String.format("Total: %.3fDT", total));
        }
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
            paymentWindow.setColorList(colorList);
            paymentWindow.setSizeList(sizeList);
            paymentWindow.setLabelList(labelList);
            System.out.println(totalDiscount);
            paymentWindow.setDiscount(totalDiscount);
            paymentWindow.setUser(user);




            // Create a new stage for the new UI
            Stage newStage = new Stage();
            newStage.setTitle("Payment");


            // Set the scene with the new UI content
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            paymentWindow.setMainStage(newStage);

            // Show the new stage
            newStage.showAndWait();
            data.clear();
            tb.refresh();
            refList.clear();
            colorList.clear();
            sizeList.clear();
            labelList.clear();

            total = 0;
            totalDiscount = 0;
            totalPrice.setText(String.format("Total: %.3fDT", total));
            payment.setDisable(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Method to handle cell edit commit event





    @FXML
    public void salesWindow(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sales.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            salesWindow sales = loader.getController();
            sales.setUser(user);
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
    public void setUser(String user) {
        this.user = user;
    }




}




