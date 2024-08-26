package com.websale.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websale.dao.ICustomerDAO;
import com.websale.entity.Customer;
import com.websale.service.IAccountService;
import com.websale.service.IHttpService;
import com.websale.service.IMailService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService extends GeneralService<Customer, String> implements IAccountService {

	@Autowired
	private ICustomerDAO dao;

	@Autowired
	private IHttpService http;

	@Autowired
	private IMailService mailer;

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Customer findById(String id) {
		return dao.findById(id);
	}

	@Override
	public void updateUser(Customer user) {
		dao.update(user);
	}

	@Override
	public boolean sendActivedUser(Customer user) {
		String to = user.getEmail();
		String subject = "Welcome to WEB SALES ";
		String url = http.getCurrentUrl().replace("register", "activate/" + http.encode(user.getId()));
		String body = "<a href='" + url + "'>Click to activate your account!</a>";
		return mailer.send(to, subject, body);
	}

	@Override
	public List<Customer> findByRoles(boolean roles) {
		return dao.findByRoles(roles);
	}

	@Transactional
	public Customer findByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		Query<Customer> query = session.createQuery("FROM Customer WHERE email = :email", Customer.class);
		query.setParameter("email", email);
		return query.uniqueResult();
	}

	@Transactional
	public Customer findByPhone(String phone) {
		Session session = sessionFactory.getCurrentSession();
		Query<Customer> query = session.createQuery("FROM Customer WHERE phone = :phone", Customer.class);
		query.setParameter("phone", phone);
		return query.uniqueResult();
	}

	// ConcurrentHashMap để theo dõi số lượng truy cập
	private final ConcurrentHashMap<String, AtomicInteger> accessCounts = new ConcurrentHashMap<>();

	// ConcurrentMap để theo dõi khách hàng đang hoạt động
	private final ConcurrentHashMap<String, LocalDateTime> activeCustomers = new ConcurrentHashMap<>();
	private static final int INACTIVITY_THRESHOLD_MINUTES = 30;

	public void logAccess(String customerId) {
		accessCounts.computeIfAbsent(customerId, k -> new AtomicInteger(0)).incrementAndGet();
	}

	public int countCustomerAccesses(String customerId) {
		AtomicInteger count = accessCounts.get(customerId);
		return count != null ? count.get() : 0;
	}

	public int countTotalAccesses() {
		return accessCounts.values().stream().mapToInt(AtomicInteger::get).sum();
	}

	public void userLoggedIn(String customerId) {
		activeCustomers.put(customerId, LocalDateTime.now());
	}

	public void userLoggedOut(String customerId) {
		activeCustomers.remove(customerId);
	}

	public int countActiveCustomers() {
		LocalDateTime now = LocalDateTime.now();
		return (int) activeCustomers.values().stream()
				.filter(lastActivity -> lastActivity.isAfter(now.minusMinutes(INACTIVITY_THRESHOLD_MINUTES)))
				.count();
	}
}
