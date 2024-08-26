package com.websale.service;

import java.util.List;

import com.websale.entity.Category;

public interface ICategoryService extends IGeneralService<Category, Integer> {
	List<Category> getRamDomByCategory();
	boolean hasProductsWithOrders(Integer categoryId);
}
