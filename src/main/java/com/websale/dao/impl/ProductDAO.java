package com.websale.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.websale.dao.IProductDAO;
import com.websale.entity.Product;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class ProductDAO extends GeneraDAO<Product, Integer> implements IProductDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Product> findByKeywords(String keywords) {
		String hql = "FROM Product p WHERE LOWER(p.name) LIKE LOWER(?0) " +
				"OR LOWER(p.category.name) LIKE LOWER(?1) " +
				"OR LOWER(p.category.nameVN) LIKE LOWER(?2) " +
				"OR LOWER(p.description) LIKE LOWER(?3)";
		String keyWords = "%" + keywords.toLowerCase() + "%";
		return getResultListParam(hql, keyWords, keyWords, keyWords, keyWords);
	}

	@Override
	public List<Product> findByCategoryId(Integer id) {
		String hql = "FROM Product p WHERE p.category.id = ?0 ORDER BY p.id ASC";
		return getResultList(hql, id);
	}

	@Override
	public List<Product> findItemByHot(String key) {
		String hql = "FROM Product";
		switch (key) {
		case "hangmoi":
			hql = "From Product p where year(current_date()) - year(p.productDate) < 10 ";
			break;
		// sắp xếp chi tiết đơn hàng theo số lượng bán giảm dần
		case "banchay":
			hql = "From Product p order by size (p.orderDetails) DESC";
			break;
		case "xemnhieu":
			hql = "FROM Product p ORDER BY p.viewCount DESC";
			break;
		case "giamgia":
			hql = "From Product p Where p.discount > 0 ORDER BY p.discount DESC";
			break;

		default:
			break;
		}
		return getResultPageOrPagram(hql, 0, 12);
	}

	@Override
	public List<Product> findByPriceRange(Double minPrice, Double maxPrice) {
		String hql = "FROM Product p WHERE p.unitPrice BETWEEN :minPrice AND :maxPrice ORDER BY p.unitPrice ASC";
		return entityManager.createQuery(hql, Product.class)
				.setParameter("minPrice", minPrice)
				.setParameter("maxPrice", maxPrice)
				.getResultList();
	}

	@Override
	public List<Product> findByIdsInCookie(String id) {
		String hql = "From Product p Where p.id IN ("+ id +")"; 		
		return getResultList(hql);
	}

	@Override
	public List<Product> getLowStockProducts(int threshold) {
		String hql = "FROM Product p WHERE p.quantity < :threshold ORDER BY p.quantity ASC";
		return entityManager.createQuery(hql, Product.class)
				.setParameter("threshold", threshold)
				.getResultList();
	}

	@Override
	public List<Product> getTopSellingProducts(int limit) {
		String hql = "SELECT p FROM Product p " +
				"JOIN p.orderDetails od " +
				"WHERE od.order.status = 0 " +
				"GROUP BY p.id " +
				"ORDER BY SUM(od.quantity) DESC";
		return entityManager.createQuery(hql, Product.class)
				.setMaxResults(limit)
				.getResultList();
	}
}
