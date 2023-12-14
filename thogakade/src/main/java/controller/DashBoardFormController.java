package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashBoardFormController {
    @FXML
    private AnchorPane pane;

    public void veiwCustomerButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../veiw/CustomerForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Customer Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void veiwItemsButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../veiw/ItemsForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Item Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void placeOrderButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../veiw/PlaceOrderForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Item Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void orderDetailButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../veiw/OrderDetailForm.fxml"))));
            stage.setResizable(true);
            stage.setTitle("Order Detail Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
