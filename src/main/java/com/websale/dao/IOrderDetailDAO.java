package com.websale.dao;

import com.websale.entity.Order;
import com.websale.entity.OrderDetail;
import com.websale.entity.Product;

import java.util.List;

public interface IOrderDetailDAO extends IGeneralDAO<OrderDetail, Integer> {
    List<OrderDetail> findByPro(Product pro);
    List<OrderDetail> findByOrder(Order order);
    List<OrderDetail> findByProId(Integer proId);
}
