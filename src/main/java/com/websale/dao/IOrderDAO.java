package com.websale.dao;

import java.util.Date;
import java.util.List;

import com.websale.entity.Customer;
import com.websale.entity.Order;
import com.websale.service.impl.CartService;

public interface IOrderDAO extends IGeneralDAO<Order, Integer> {

	void create(Order o, CartService cart);
	List<Order> findByUser(Customer user);
	int getQuantityInTransit(Integer productId);
	int getQuantitySold(Integer productId);
	int getQuantityInTransitInDate();
	int getQuantitySoldInDate(Date date);
}
