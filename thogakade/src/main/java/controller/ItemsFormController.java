package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import dto.CustomerDto;
import dto.ItemDto;
import dto.tm.ItemTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.ItemModel;
import model.impl.ItemModelImpl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class ItemsFormController {
    @FXML
    private JFXTreeTableView<ItemTm> tblItem;
    @FXML
    private TreeTableColumn colOption;
    @FXML
    private TreeTableColumn colQtyOnHand;
    @FXML
    private TreeTableColumn colUnitPrice;
    @FXML
    private TreeTableColumn colDescription;
    @FXML
    private TreeTableColumn colItem;

    @FXML
    private JFXTextField qtyTextField;
    @FXML
    private JFXTextField unitPriceTextField;
    @FXML
    private JFXTextField descriptionTextField;
    @FXML
    private JFXTextField itemCodeTextField;
    @FXML
    private GridPane pane;

    private ItemModel itemModel = new ItemModelImpl();

    public void backBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../veiw/DashBoardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateBtnOnAction(ActionEvent actionEvent) {
        try {
            boolean isUpdated = itemModel.updateItem(new ItemDto(itemCodeTextField.getText(),
                    descriptionTextField.getText(),
                    Double.parseDouble(unitPriceTextField.getText()),
                    Integer.parseInt(qtyTextField.getText())
            ));
            if (isUpdated){
                new Alert(Alert.AlertType.INFORMATION,"Item Updated!").show();
                loadItemTable();
                clearFields();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveBtnOnAction(ActionEvent actionEvent) {
        try {
            boolean isSaved = itemModel.saveItem(new ItemDto(itemCodeTextField.getText(),
                    descriptionTextField.getText(),
                    Double.parseDouble(unitPriceTextField.getText()),
                    Integer.parseInt(qtyTextField.getText())
            ));
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Item Saved!").show();
                loadItemTable();
                clearFields();
            }

        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void reloadBtnOnAction(ActionEvent actionEvent) {
        loadItemTable();
        tblItem.refresh();
        clearFields();
    }
    private void clearFields() {
        tblItem.refresh();
        itemCodeTextField.clear();
        descriptionTextField.clear();
        unitPriceTextField.clear();
        qtyTextField.clear();
        itemCodeTextField.setEditable(true);
    }

    public void initialize(){
        colItem.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new TreeItemPropertyValueFactory<>("qtyOnHand"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadItemTable();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                setData(newValue.getValue());
            }catch (Exception e) {

            }
        });
    }
    private void setData(ItemTm newValue) {
        if (newValue != null) {
            itemCodeTextField.setEditable(false);
            itemCodeTextField.setText(newValue.getCode());
            descriptionTextField.setText(newValue.getDescription());
            unitPriceTextField.setText(String.valueOf(newValue.getUnitPrice()));
            qtyTextField.setText(String.valueOf(newValue.getQtyOnHand()));
        }
    }

    private void loadItemTable() {
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM item";

        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet result = stm.executeQuery(sql);

            while (result.next()){
                JFXButton btn = new JFXButton("Delete");

                ItemTm tm = new ItemTm(
                        result.getString(1),
                        result.getString(2),
                        result.getDouble(3),
                        result.getInt(4),
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteItem(tm.getCode());
                });

                tmList.add(tm);
            }

            TreeItem<ItemTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblItem.setRoot(treeItem);
            tblItem.setShowRoot(false);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(String code) {
        try {
            boolean isDeleted = itemModel.deleteItem(code);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                loadItemTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
