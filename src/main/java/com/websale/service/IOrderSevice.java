package com.websale.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.websale.entity.Order;
import com.websale.entity.Product;
import com.websale.service.impl.CartService;

public interface IOrderSevice extends IGeneralService<Order, Integer> {
	Order createOrder();
	void addOrderAndOrderDetail(Order o, CartService cart); // thêm mới đơn hàng vào db
	List<Order> getAllOrderByUser();
	Order findById(Integer id);
	Map<Integer, Product> getPurchasedItems();
	void updateOrder(Order form);
	byte[] createInvoicePdf(Order order)  throws IOException;
	void setCompletedTimestamp(Integer orderId, Timestamp timestamp);
	Timestamp getCompletedTimestamp(Integer orderId);
	Map<Integer, Timestamp> getCompletedTimestamps();
	int getQuantityInTransit(Integer productId);
	int getQuantitySold(Integer productId);
}
