package com.websale.service;

import java.util.Collection;

import com.websale.entity.Product;

public interface ICartService {
	void addCart(Integer id);
	void removeCart(Integer id);
	void updateCart(Integer id, Integer qty);
	void clear();
	int getCountCart();
	double getAmountCart();
	Collection<Product> getItemsCart();
}
