package omar.retailstoremanagement;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.net.URL;
import java.util.ResourceBundle;
public class chartsWindow implements Initializable {
    private String user;

    @FXML
    private PieChart pieChart;
    public void setUser(String user) {
        this.user = user;
    }
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
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://omars.mysql.database.azure.com:3306", "omar", "Amrou2009@")) {
            try (Statement stmt = con.createStatement()) {
                stmt.execute("USE stockdb");
                ResultSet resultSet = stmt.executeQuery("SELECT sum(total) as total, ref FROM payments, payment_references\n" +
                        "WHERE payments.payment_id = payment_references.payment_id\n" +
                        "AND payment_date >= DATE_SUB(CURDATE(), INTERVAL '1' MONTH)\n" +
                        "GROUP BY ref");
                while (resultSet.next()) {
                    String formattedPrice = String.format("%.3f", Double.parseDouble(resultSet.getString("total")));
                    String ref = resultSet.getString("ref");
                    System.out.println(ref + " " + formattedPrice);
                    pieChartData.add(new PieChart.Data(ref, Double.parseDouble(formattedPrice)));
                }
                pieChartData.forEach(data ->
                        data.nameProperty().bind(
                                Bindings.concat(
                                        data.getName(), " ", data.pieValueProperty(), "DT"
                                )
                        )
                );
                pieChart.getData().addAll(pieChartData);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
