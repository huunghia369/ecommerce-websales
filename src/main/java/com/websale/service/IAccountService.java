package com.websale.service;

import java.util.List;

import com.websale.entity.Customer;

public interface IAccountService extends IGeneralService<Customer, String> {
	boolean sendActivedUser(Customer user);
	void updateUser(Customer user);
	List<Customer> findByRoles(boolean admin);
	Customer findByEmail(String email);
	Customer findByPhone(String phone);
	void logAccess(String customerId);
	int countCustomerAccesses(String customerId);
	int countTotalAccesses();
	void userLoggedIn(String customerId);
	void userLoggedOut(String customerId);
	int countActiveCustomers();
}
