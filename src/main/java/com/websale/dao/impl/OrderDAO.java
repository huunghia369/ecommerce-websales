package com.websale.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.websale.dao.IOrderDAO;
import com.websale.dao.IOrderDetailDAO;
import com.websale.entity.Customer;
import com.websale.entity.Order;
import com.websale.entity.OrderDetail;
import com.websale.entity.Product;
import com.websale.service.impl.CartService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class OrderDAO extends GeneraDAO<Order, Integer> implements IOrderDAO {

	@Autowired
	private IOrderDetailDAO dao;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void create(Order o, CartService cart) {
		this.create(o);
		Collection<Product> items = cart.getItemsCart();
		items.forEach(p -> {
			OrderDetail detail = new OrderDetail();
			detail.setOrder(o);
			detail.setProduct(p);
			detail.setUnitPrice(p.getUnitPrice());
			detail.setDiscount(p.getDiscount());
			detail.setQuantity(p.getQuantity());
			dao.create(detail);
		});

	}

	@Override
	public List<Order> findByUser(Customer user) {
		String sql = "FROM Order o WHERE o.customer.id=?0 ORDER BY o.orderDate DESC";
		return this.getResultList(sql, user.getId());
	}

	@Override
	public int getQuantityInTransit(Integer productId) {
		// Truy vấn để lấy tổng số lượng sản phẩm đang giao
		String jpql = "SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.product.id = :productId AND od.order.status = 2";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("productId", productId);
		Long result = (Long) query.getSingleResult();
		return result != null ? result.intValue() : 0;
	}

	@Override
	public int getQuantitySold(Integer productId) {
		// Truy vấn để lấy tổng số lượng sản phẩm đã bán
		String jpql = "SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.product.id = :productId AND od.order.status = 0";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("productId", productId);
		Long result = (Long) query.getSingleResult();
		return result != null ? result.intValue() : 0;
	}

	@Override
	public int getQuantityInTransitInDate() {
		// Truy vấn để lấy tổng số lượng sản phẩm đang giao
		String jpql = "SELECT SUM(od.quantity) FROM OrderDetail od " +
				"WHERE od.order.status = 2";
		Query query = entityManager.createQuery(jpql);
		Long result = (Long) query.getSingleResult();
		return result != null ? result.intValue() : 0;
	}

	@Override
	public int getQuantitySoldInDate(Date date) {
		// Truy vấn để lấy tổng số lượng sản phẩm đã bán trong ngày
		String jpql = "SELECT SUM(od.quantity) FROM OrderDetail od " +
				"WHERE od.order.status = 0 " +
				"AND DATE(od.order.orderDate) = :date";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("date", date);
		Long result = (Long) query.getSingleResult();
		return result != null ? result.intValue() : 0;
	}
}
