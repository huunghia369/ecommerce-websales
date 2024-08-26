package com.websale.dao;

import java.util.List;

import com.websale.entity.Customer;
import com.websale.entity.Product;

public interface IProductDAO extends IGeneralDAO<Product, Integer> {

	List<Product> findByKeywords(String keywords);
	List<Product> findByCategoryId(Integer id);
	List<Product> findItemByHot(String key);
	List<Product> findByPriceRange(Double minPrice, Double maxPrice);
	List<Product> findByIdsInCookie(String id);
	List<Product> getLowStockProducts(int threshold);
	List<Product> getTopSellingProducts(int limit);
}
