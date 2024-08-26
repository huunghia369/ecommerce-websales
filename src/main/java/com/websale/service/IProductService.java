package com.websale.service;

import java.util.List;

import com.websale.entity.Product;

public interface IProductService extends IGeneralService<Product, Integer> {
	List<Product> findByKeywords(String keyWords);
	List<Product> findAllProductByCategory(int id);
	List<Product> findByHot(String key);
	List<Product> findByPriceRange(Double minPrice, Double maxPrice);
	List<Product>  getViewProduct(String name,String id); //  get những sản phẩm đa xem thông qua mảng id sp trong Cookie
	List<Product>  getFaVoProduct(String name,String id); // get những sản phẩm yêu thích thông qua mảng id sp trong Cookie
	List<Product>  findByIdsInCookie(String ids);
	List<Product> findPaginated(int pageNo, int pageSize);
	long count();
//	Product findById(Integer id);
}
