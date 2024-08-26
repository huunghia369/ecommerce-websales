package com.websale.dao;

import java.util.List;

import com.websale.entity.Customer;

public interface ICustomerDAO extends IGeneralDAO<Customer, String> {
	List<Customer> findByRoles(boolean roles);
	List<Object[]> getTopCustomers(int limit);
}
