package com.websale.dao.impl;

import com.websale.entity.Order;
import com.websale.entity.Product;
import org.springframework.stereotype.Service;

import com.websale.dao.IOrderDetailDAO;
import com.websale.entity.OrderDetail;

import java.util.List;

@Service
public class OrderDetailDAO extends GeneraDAO<OrderDetail, Integer> implements IOrderDetailDAO{
    @Override
    public List<OrderDetail> findByPro(Product pro){
        String sql = "From OrderDetail od WHERE od.product.id=?0 ORDER BY od.id ASC";
        return this.getResultList(sql, pro.getId());
    }

    @Override
    public List<OrderDetail> findByProId(Integer proId){
        String sql = "From OrderDetail od WHERE od.product.id=?0 ORDER BY od.id ASC";
        return this.getResultList(sql, proId);
    }

    @Override
    public List<OrderDetail> findByOrder(Order order){
        String sql = "From OrderDetail od WHERE od.order.id=?0 ORDER BY od.id ASC";
        return this.getResultList(sql, order.getId());
    }
}
