package com.websale.service.impl;

import java.util.List;

import com.websale.service.IOrderSevice;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websale.dao.IProductDAO;
import com.websale.entity.Product;
import com.websale.service.ICookieService;
import com.websale.service.IProductService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService extends GeneralService<Product, Integer>implements IProductService {

	@Autowired
	private IProductDAO dao;

	@Autowired
	private IOrderSevice orderSevice;

	@Autowired
	private ICookieService cookieService;

	@Autowired
	private SessionFactory factory;

	@Override
	public List<Product> findByKeywords(String keyWords) {
		return dao.findByKeywords(keyWords);
	}


	@Override
	public List<Product> findAllProductByCategory(int id) {
		return dao.findByCategoryId(id);
	}

	@Override
	public List<Product> findByHot(String key) {
		return dao.findItemByHot(key);
	}

	@Override
	public List<Product> findByPriceRange(Double minPrice, Double maxPrice) {return dao.findByPriceRange(minPrice, maxPrice);}

	@Override
	public List<Product> getViewProduct(String name, String id) {
		String ids = cookieService.getCookieValue(name, id.toString());
		if (!ids.contains(id.toString())) { 
			ids += "," + id.toString();
		}
		cookieService.createCookie(name, ids, 15);
		return dao.findByIdsInCookie(ids); 
	}

	@Override
	public List<Product> getFaVoProduct(String name, String id) {
		String favos = cookieService.getCookieValue(name, "");
		if (favos.length() > 0) 
		{
			return dao.findByIdsInCookie(favos); 
		} else
			return null;
	}


	@Override
	public List<Product> findByIdsInCookie(String ids) {
		return dao.findByIdsInCookie(ids);
	}

	@Override
	@Transactional
	public List<Product> findPaginated(int pageNo, int pageSize) {
		Session session = factory.getCurrentSession();
		String hql = "FROM Product";
		Query<Product> query = session.createQuery(hql, Product.class);
		query.setFirstResult((pageNo - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	@Transactional
	public long count() {
		Session session = factory.getCurrentSession();
		String hql = "SELECT COUNT(p) FROM Product p";
		Query<Long> query = session.createQuery(hql, Long.class);
		return query.uniqueResult();
	}

	@Override
	public Product findById(Integer id) {
		Product product = dao.findById(id);
		if (product != null) {
			// Lấy số lượng sản phẩm đang giao
			int quantityInTransit = orderSevice.getQuantityInTransit(id);
			// Tính toán số lượng còn lại
			int availableQuantity = product.getQuantity() - quantityInTransit;
			// Đặt giá trị cho thuộc tính tính toán
			product.setAvailableQuantity(availableQuantity);
			int quantitySold = orderSevice.getQuantitySold(product.getId());
			product.setSoldQuantity(quantitySold);
		}
		return product;
	}
}
