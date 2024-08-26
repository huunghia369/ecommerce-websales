package com.websale.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.websale.dao.ICustomerDAO;
import com.websale.entity.Customer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class CustomerDAO extends GeneraDAO<Customer, String> implements ICustomerDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Customer> findByRoles(boolean roles) {
		String sql = "From Customer c WHERE c.admin =?0";
		return this.getResultList(sql, roles);
	}

	@Override
	public List<Object[]> getTopCustomers(int limit) {
		String jpql = "SELECT c.id, c.fullname, c.email, c.phone, SUM(o.amount) FROM Customer c " +
				"JOIN c.orders o " +
				"GROUP BY c.id, c.fullname, c.email, c.phone " +
				"ORDER BY SUM(o.amount) DESC";
		return entityManager.createQuery(jpql)
				.setMaxResults(limit)
				.getResultList();
	}
}
