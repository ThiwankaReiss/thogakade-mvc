package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableView;

import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;

import dto.tm.ItemTm;
import dto.tm.OrderDetailsTm;

import dto.tm.OrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import java.util.List;

public class OrderDetailFormController {
    public JFXComboBox cmbOrderID;
    public Label custIdlbl;
    public Label custIdFillLabel;
    public Label custNameLbl;
    public Label custNameFillLabel;
    public Label orderDatelbl;
    public Label orderDateFillLabel;
    public Label lblTotal;
    public JFXTreeTableView tblOrder;
    public TreeTableColumn colCode;
    public TreeTableColumn colDescription;
    public TreeTableColumn colQty;
    public TreeTableColumn colAmount;
    public AnchorPane pane;
    private OrderModel orderModel=new OrderModelImpl();
    private List<OrderDto> orders;
    private CustomerModel customerModel=new CustomerModelImpl();
    private CustomerDto customerDto;
    private OrderDto orderDto;
    private ItemModel itemModel=new ItemModelImpl();


    private List<ItemDto> items;

    private void loadOrderIds() throws SQLException, ClassNotFoundException {
        items=itemModel.allItems();

        try {
            orders = orderModel.allOrders();
            ObservableList list= FXCollections.observableArrayList();
            for (OrderDto dto: orders) {
                list.add(dto.getOrderId());
            }
            cmbOrderID.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void initialize() throws SQLException, ClassNotFoundException {
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));

        loadOrderIds();

        cmbOrderID.getSelectionModel().selectedItemProperty().addListener((ObservableValue,oldValue,orderId)->{
            for (OrderDto dto: orders) {
                if(dto.getOrderId().equals(orderId)){
                    orderDto=dto;
                    orderDateFillLabel.setText(orderDto.getDate());
                    custIdFillLabel.setText(dto.getCustId());
                    loadTable(orderDto.getList());
                    try {
                        setCustomerName(dto.getCustId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    private void loadTable(List<OrderDetailsDto> list) {
        ObservableList<OrderDetailsTm> tmList = FXCollections.observableArrayList();
        int total=0;
        for (OrderDetailsDto dto : list) {
            ItemDto item = selectItem(dto.getCode());
            OrderDetailsTm tm = new OrderDetailsTm(
                    dto.getCode(),
                    item.getDescription(),
                    dto.getQty(),
                    dto.getQty() * item.getUnitPrice()

            );
            total+=dto.getQty()*item.getUnitPrice();
            tmList.add(tm);
        }
        lblTotal.setText(String.valueOf(total));
        TreeItem<OrderDetailsTm> treeItem = new RecursiveTreeItem<OrderDetailsTm>(tmList, RecursiveTreeObject::getChildren);
        tblOrder.setRoot(treeItem);
        tblOrder.setShowRoot(false);

    }
    public ItemDto selectItem(String code){
        for (ItemDto itm:items) {
            if(itm.getCode().equals(code)){
                return itm;
            }
        }
        return null;
    }

    public void setCustomerName(String custId) throws SQLException, ClassNotFoundException {
        customerDto=customerModel.getCustomer(custId);
        custNameFillLabel.setText(customerDto.getName());
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
}
