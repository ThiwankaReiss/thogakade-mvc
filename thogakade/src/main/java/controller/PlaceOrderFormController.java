package controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.OrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerModel;
import model.ItemModel;
import model.OrderModel;
import model.impl.CustomerModelImpl;
import model.impl.ItemModelImpl;
import model.impl.OrderModelImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderFormController {


    @FXML
    private Label orderIdLabel;
    @FXML
    private JFXButton backBtn;
    @FXML
    private Label lblTotal;
    @FXML
    private JFXButton addToCartBtn;
    @FXML
    private JFXButton placeOrderBtn;
    @FXML
    private TreeTableColumn colOption;
    @FXML
    private TreeTableColumn colAmount;
    @FXML
    private TreeTableColumn colQty;
    @FXML
    private TreeTableColumn colDescription;
    @FXML
    private TreeTableColumn colCode;
    @FXML
    private JFXTreeTableView tblOrder;
    @FXML
    private JFXTextField qtyTextField;
    @FXML
    private JFXTextField unitPriceTextField;
    @FXML
    private JFXComboBox cmbItemCode;
    @FXML
    private JFXComboBox cmbCustId;
    @FXML
    private JFXTextField descriptoinTextField;
    @FXML
    private JFXTextField custNameTextField;
    @FXML
    private AnchorPane pane;
    private  CustomerModel customerModel=new CustomerModelImpl();
    private  List<CustomerDto> customers;
    private ItemModel itemModel=new ItemModelImpl();
    private  List<ItemDto> items;
    private double total=0;
    private ObservableList<OrderTm> tmList=FXCollections.observableArrayList();
    private OrderModel orderModel = new OrderModelImpl();

    public void initialize(){

        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        custNameTextField.setEditable(false);
        descriptoinTextField.setEditable(false);
        unitPriceTextField.setEditable(false);


        loadCustomerIds();
        loadItemCodes();

        generateId();

        cmbCustId.getSelectionModel().selectedItemProperty().addListener((ObservableValue,oldValue,id)->{
            for (CustomerDto dto: customers) {
                if(dto.getId().equals(id)){
                    custNameTextField.setText(dto.getName());
                }
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((ObservableValue,oldValue,code)->{
            for (ItemDto dto: items) {
                if(dto.getCode().equals(code)){
                    descriptoinTextField.setText(dto.getDescription());
                    unitPriceTextField.setText(String.valueOf(dto.getUnitPrice()));
                }
            }
        });
    }

    private void generateId() {
        try {
            OrderDto dto = orderModel.lastOrder();
            if (dto!=null){
                String id = dto.getOrderId();
                int num = Integer.parseInt(id.split("[D]")[1]);
                num++;
                orderIdLabel.setText(String.format("D%03d",num));
            }else{
                orderIdLabel.setText("D001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerIds() {

        try {
            customers = customerModel.allCustomers();
            ObservableList list= FXCollections.observableArrayList();
            for (CustomerDto dto: customers) {
                list.add(dto.getId());
            }
            cmbCustId.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadItemCodes() {
        try {
            items = itemModel.allItems();
            ObservableList list= FXCollections.observableArrayList();
            for (ItemDto dto: items) {
                list.add(dto.getCode());
            }
            cmbItemCode.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void backBtnOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../veiw/DashBoardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToCartBtnOnAction(ActionEvent actionEvent) {
        if(qtyTextField.getText().equals(null)||qtyTextField.getText().equals("")){
            new Alert(Alert.AlertType.ERROR, "Enter a Buying Quantity").show();
        }else if (Integer.parseInt(qtyTextField.getText())>0) {
            double amount=Integer.parseInt(qtyTextField.getText())*Double.parseDouble(unitPriceTextField.getText());
            JFXButton btn=new JFXButton("Delete");

            OrderTm tm=new OrderTm(
                    cmbItemCode.getValue().toString(),
                    descriptoinTextField.getText(),
                    Integer.parseInt(qtyTextField.getText()),
                    amount,
                    btn
            );
            btn.setOnAction(actionEvent1 -> {
                total-= tm.getAmount();
                tmList.remove(tm);
                tblOrder.refresh();
                lblTotal.setText(String.valueOf(total));
            });


            boolean isExists =false;
            for (OrderTm order: tmList) {
                if (order.getCode().equals(tm.getCode())) {
                    order.setQty(order.getQty()+ tm.getQty());
                    order.setAmount(order.getAmount()+ tm.getAmount());
                    total+=amount;
                    isExists=true;
                }
            }

            if(!isExists){
                tmList.add(tm);
                total+=amount;
            }
            try {
                for (OrderTm order : tmList) {
                    for (ItemDto item : items) {
                        if (order.getCode().equals(item.getCode()) && order.getQty() > item.getQtyOnHand()) {
                            order.setQty(order.getQty() - tm.getQty());
                            order.setAmount(order.getAmount() - tm.getAmount());
                            total-=amount;
                            new Alert(Alert.AlertType.ERROR, "Out of Stocks").show();
                        }
                        if (order.getAmount() == 0) {
                            tmList.remove(order);
                        }
                    }
                }
            }catch (Exception e){

            }
            lblTotal.setText(String.valueOf(total));
            TreeItem<OrderTm> treeObject = new RecursiveTreeItem<OrderTm>(tmList, RecursiveTreeObject::getChildren);
            tblOrder.setRoot(treeObject);
            tblOrder.setShowRoot(false);

        }else{
            new Alert(Alert.AlertType.ERROR, "Enter Positive Buying Quantity").show();
        }

    }

    public void placeOrderBtnOnAction(ActionEvent actionEvent) {
        List<OrderDetailsDto> list=new ArrayList<>();
        for (OrderTm tm: tmList) {
            list.add(new OrderDetailsDto(
                    orderIdLabel.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    (tm.getAmount()/tm.getQty())
            ));
        }

        boolean isSaved = false;
        try {
            isSaved = orderModel.saveOrder(new OrderDto(
                    orderIdLabel.getText(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                    cmbCustId.getValue().toString(),
                    list
            ));
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Order Saved!").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
