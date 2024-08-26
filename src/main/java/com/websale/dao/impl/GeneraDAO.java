package com.websale.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.websale.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.websale.dao.IGeneralDAO;

@Transactional
public class GeneraDAO<L, K> implements IGeneralDAO<L, K> {

	@Autowired
	protected SessionFactory factory;

	@Override
	public L create(L entity) {
		Session session = factory.getCurrentSession();
		session.save(entity);
		return entity;
	}

	@Override
	public void update(L entity) {
		Session session = factory.getCurrentSession();
		session.update(entity);
	}

	@Override
	public void delete(@SuppressWarnings("unchecked") K... ids) {
		Session session = factory.getCurrentSession();
		for (K id : ids) {
			L entity = this.findById(id);
			if (entity != null) {
				// Xử lý các liên kết trước khi xóa
				handleAssociatedEntities(entity, session);
				session.delete(entity);
			}
		}
	}

	private void handleAssociatedEntities(L entity, Session session) {
		if (entity instanceof Product) {
			Product product = (Product) entity;
			List<OrderDetail> orderDetails = product.getOrderDetails();
			for (OrderDetail orderDetail : orderDetails) {
				// Xóa các liên kết trước khi xóa sản phẩm
				orderDetail.setProduct(null);
				session.delete(orderDetail);
			}
		} else if (entity instanceof Category) {
			Category category = (Category) entity;
			List<Product> products = category.getProducts();
			for (Product product : products) {
				// Xóa các liên kết trước khi xóa sản phẩm
				product.setCategory(null);
				session.delete(product);
			}
		} else if (entity instanceof Customer) {
			Customer customer = (Customer) entity;
			List<Order> orders = customer.getOrders();
			for (Order order : orders) {
				// Xóa các liên kết trước khi xóa đơn hàng
				List<OrderDetail> orderDetails = order.getOrderDetails();
				for (OrderDetail orderDetail : orderDetails) {
					orderDetail.setOrder(null);
					session.delete(orderDetail);
				}
				session.delete(order);
			}
		} else if (entity instanceof Order) {
			Order order = (Order) entity;
			List<OrderDetail> orderDetails = order.getOrderDetails();
			for (OrderDetail orderDetail : orderDetails) {
				orderDetail.setOrder(null);
				session.delete(orderDetail);
			}
		}
		// Thêm xử lý cho các loại thực thể khác nếu cần
	}

	@Override
	public L findById(K id) {
		Session session = factory.getCurrentSession();
		Class<L> entityClass = this.getEntityClass();
		L entity = session.find(entityClass, id);
		return entity;
	}


	@SuppressWarnings({ "unchecked" })
	private Class<L> getEntityClass() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		return  (Class<L>) type.getActualTypeArguments()[0];
	}

	@Override
	public List<L> findAll() {
		Session session = factory.getCurrentSession();
		String hql = "FROM " + this.getEntityClass().getSimpleName();
		TypedQuery<L> query = session.createQuery(hql, this.getEntityClass());
		List<L> entity = query.getResultList();
		return entity;
	}

	@Override
	public <E> List<E> getResultList(String hql, Object...mangParam) {			
		return this.getResultPageOrPagram(hql,0,0,mangParam);
	}
	
	@Override
	public <E> List<E> getResultListParam(String hql, Object...mangParam) {	
		return this.getResultPageOrPagram(hql, 0, 0, mangParam);
	}

	@Override
	public <E> List<E> getResultPageOrPagram(String hql, int page, int size, Object...mangParam) {
		Session session = factory.getCurrentSession();
		@SuppressWarnings("unchecked")
		TypedQuery<E> query =  session.createQuery(hql);
		// size > 0 mới phân trang
		if(size >0)
		{
			query.setFirstResult(page*size);
			query.setMaxResults(size);
		}
		for(int i=0;i<mangParam.length;i++)
		{
			query.setParameter(i, mangParam[i]);
		}
		List<E> list = query.getResultList();
		return list;
	}

}
