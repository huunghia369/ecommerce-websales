package com.websale.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.websale.dao.IOrderDetailDAO;
import com.websale.dao.IProductDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websale.dao.ICategoryDAO;
import com.websale.entity.Category;
import com.websale.entity.Product;
import com.websale.service.ICategoryService;

@Service
public class CategoryService extends GeneralService<Category, Integer> implements ICategoryService {

	@Autowired
	private ICategoryDAO dao;

	@Autowired
	private IProductDAO productDao;

	@Autowired
	private IOrderDetailDAO orderDetailDao;

	@Override
	public List<Category> getRamDomByCategory() {
		String hql = "FROM Category c WHERE size(c.products) >=4 ";

		List<Category> list = dao.getResultList(hql);
		Collections.shuffle(list);
		List<Category> result = new ArrayList<Category>();
		for (Category cate : list) {
			List<Product> prods = cate.getProducts();
			if (prods.size() >= 4) {
				Collections.shuffle(prods);
				cate.setProducts(prods.subList(0, 4));
				result.add(cate);
			}
		}
		return list;
	}

	@Override
	public boolean hasProductsWithOrders(Integer categoryId) {
		List<Product> products = productDao.findByCategoryId(categoryId);
		for (Product product : products) {
			if (!orderDetailDao.findByProId(product.getId()).isEmpty()) {
				return true;
			}
		}
		return false;
	}
}
