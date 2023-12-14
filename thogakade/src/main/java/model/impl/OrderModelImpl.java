package model.impl;

import db.DBConnection;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.OrderDetailsModel;
import model.OrderModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderModelImpl implements OrderModel {
    OrderDetailsModel orderDetailsModel = new OrderDetailsModelImpl();
    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO  orders Values (?,?,?)";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,dto.getOrderId());
        pstm.setString(2,dto.getDate());
        pstm.setString(3,dto.getCustId());

        if (pstm.executeUpdate() > 0) {
            boolean isDetailSaved = orderDetailsModel.saveOrderDetails(dto.getList());
            if (isDetailSaved) {
                connection.commit();
                return true;
            }
        }
        return  false;
    }

    @Override
    public OrderDto lastOrder() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 1";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()){
            return new OrderDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    null
            );
        }

        return null;
    }

    @Override
    public List<OrderDto> allOrders() throws SQLException, ClassNotFoundException {
        List<OrderDto> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        OrderDetailsModel orderDetailsModel=new OrderDetailsModelImpl();

        while (resultSet.next()){
            list.add(new OrderDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    orderDetailsModel.getOrderDetails(resultSet.getString(1))
            ));
        }
        return list;
    }
}
