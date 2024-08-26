package com.websale.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.*;
import javax.transaction.Transactional;

import com.websale.service.IOrderSevice;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.websale.dao.IReportDAO;

@Transactional
@Repository
public class ReportDAO implements IReportDAO {
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private IOrderSevice orderService;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Object[]> inventory() {
		String hql = "SELECT " + " p.category.nameVN AS category," + " sum(p.quantity) AS totalQuantity, "
				+ " sum(p.quantity*p.unitPrice) AS totalAmount, " + " min(p.unitPrice) AS minPrice, "
				+ " max(p.unitPrice) AS maxPrice, " + " avg(p.unitPrice) AS avgPrice" + " FROM Product p "
				+ " GROUP BY p.category.nameVN";
		return this.getReportData(hql);
	}

	@Override
	public List<Object[]> inventory(Date startDate, Date endDate) {
		String hql = "SELECT " +
				" p.category.nameVN AS category," +
				" sum(p.quantity) AS totalQuantity, " +
				" sum(p.quantity*p.unitPrice) AS totalAmount, " +
				" min(p.unitPrice) AS minPrice, " +
				" max(p.unitPrice) AS maxPrice, " +
				" avg(p.unitPrice) AS avgPrice " +
				" FROM Product p " +
				" WHERE p.productDate BETWEEN :startDate AND :endDate " +
				" GROUP BY p.category.nameVN";
		return this.getReportData(hql, "startDate", startDate, "endDate", endDate);
	}

	@Override
	public List<Object[]> revenueByCategory() {
		String hql = "SELECT " +
				" d.product.category.nameVN AS category," +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity*d.unitPrice*(1-d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice " +
				" FROM OrderDetail d " +
				" WHERE d.order.status = 0 " +
				" GROUP BY d.product.category.nameVN " +
				" ORDER BY revenue DESC";
		return this.getReportData(hql);
	}

	@Override
	public List<Object[]> revenueByCategory(Date startDate, Date endDate) {
		String hql = "SELECT " +
				" d.product.category.nameVN AS category," +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity*d.unitPrice*(1-d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice " +
				" FROM OrderDetail d " +
				" WHERE d.order.status = 0 AND d.order.orderDate BETWEEN :startDate AND :endDate " +
				" GROUP BY d.product.category.nameVN " +
				" ORDER BY revenue DESC";
		return this.getReportData(hql, "startDate", startDate, "endDate", endDate);
	}

	@Override
	public List<Object[]> revenueByCustomer() {
		String hql = "SELECT " +
				" d.order.customer.id AS customer," +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity*d.unitPrice*(1-d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice " +
				" FROM OrderDetail d " +
				" WHERE d.order.status = 0 " +
				" GROUP BY d.order.customer.id";
		return this.getReportData(hql);
	}

	@Override
	public List<Object[]> revenueByCustomer(Date startDate, Date endDate) {
		String hql = "SELECT " +
				" d.order.customer.id AS customer," +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity*d.unitPrice*(1-d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice " +
				" FROM OrderDetail d " +
				" WHERE d.order.status = 0 AND d.order.orderDate BETWEEN :startDate AND :endDate " +
				" GROUP BY d.order.customer.id";
		return this.getReportData(hql, "startDate", startDate, "endDate", endDate);
	}

	@Override
	public List<Object[]> revenueByYear() {
		String hql = "SELECT " +
				" year(d.order.orderDate) AS year," +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity*d.unitPrice*(1-d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice " +
				" FROM OrderDetail d " +
				" WHERE d.order.status = 0 " +
				" GROUP BY year(d.order.orderDate) " +
				" ORDER BY revenue DESC";
		return this.getReportData(hql);
	}

	@Override
	public List<Object[]> revenueByMonth() {
		String hql = "SELECT " +
				" month(d.order.orderDate) AS month," +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity*d.unitPrice*(1-d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice " +
				" FROM OrderDetail d " +
				" WHERE d.order.status = 0 " +
				" GROUP BY month(d.order.orderDate) " +
				" ORDER BY revenue DESC";
		return this.getReportData(hql);
	}

	@Override
	public List<Object[]> revenueByProduct(Integer categoryId) {
		String hql = "SELECT " +
				" d.product.name AS product," +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity*d.unitPrice*(1-d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice," +
				" d.product.category.nameVN AS category " +
				" FROM OrderDetail d " +
				" WHERE d.product.category.id = :categoryId " +
				" AND d.order.status = 0 " +
				" GROUP BY d.product.category.nameVN, d.product.name " +
				" ORDER BY category ASC, revenue DESC";
		return this.getReportData(hql, "categoryId", categoryId);
	}

	@Override
	public List<Object[]> revenueByProduct(Integer categoryId, Date startDate, Date endDate) {
		String hql = "SELECT " +
				" d.product.name AS product," +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity*d.unitPrice*(1-d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice," +
				" d.product.category.nameVN AS category " +
				" FROM OrderDetail d " +
				" WHERE d.product.category.id = :categoryId " +
				" AND d.order.status = 0 " +
				" AND d.order.orderDate BETWEEN :startDate AND :endDate " +
				" GROUP BY d.product.category.nameVN, d.product.name " +
				" ORDER BY category ASC, revenue DESC";
		return this.getReportData(hql, "categoryId", categoryId, "startDate", startDate, "endDate", endDate);
	}

	@Override
	public List<Object[]> revenueByDateRange(Date startDate, Date endDate) {
		String hql = "SELECT " +
				" d.product.category.nameVN AS category, " +
				" d.order.orderDate AS date, " +
				" sum(d.quantity) AS totalQuantity, " +
				" sum(d.quantity * d.unitPrice * (1 - d.discount)) AS revenue, " +
				" min(d.unitPrice) AS minPrice, " +
				" max(d.unitPrice) AS maxPrice, " +
				" avg(d.unitPrice) AS avgPrice " +
				" FROM OrderDetail d " +
				" WHERE d.order.orderDate BETWEEN :startDate AND :endDate " +
				" AND d.order.status = 0 " +
				" GROUP BY d.product.category.nameVN, d.order.orderDate " +
				" ORDER BY d.product.category.nameVN, d.order.orderDate";
		return this.getReportData(hql, "startDate", startDate, "endDate", endDate);
	}

	private List<Object[]> getReportData(String hql, Object... args) {
		Session session = sessionFactory.getCurrentSession();
		TypedQuery<Object[]> query = session.createQuery(hql, Object[].class);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i += 2) {
				String paramName = (String) args[i];
				Object paramValue = args[i + 1];
				query.setParameter(paramName, paramValue);
			}
		}
		return query.getResultList();
	}

	@Override
	public Double getDailyProfit() {
		double profit = 0.0;

		// Lấy danh sách các timestamps của đơn hàng đã hoàn tất
		Map<Integer, Timestamp> completedTimestamps = orderService.getCompletedTimestamps();

		for (Map.Entry<Integer, Timestamp> entry : completedTimestamps.entrySet()) {
			Integer orderId = entry.getKey();
			Timestamp completedTimestamp = entry.getValue();

			if (completedTimestamp.toLocalDateTime().toLocalDate().equals(LocalDate.now())) {
				String jpql = "SELECT COALESCE(SUM(od.unitPrice * od.quantity * (1 - od.discount)), 0) " +
						"FROM Order o " +
						"JOIN o.orderDetails od " +
						"WHERE o.id = :orderId AND o.status = 0";

				profit += (Double) entityManager.createQuery(jpql)
						.setParameter("orderId", orderId)
						.getSingleResult();
			}
		}

		return profit;
	}

	@Override
	public Double getDailyLoss() {
		double loss = 0.0;

		// Lấy danh sách các timestamps của đơn hàng đã hoàn tất
		Map<Integer, Timestamp> completedTimestamps = orderService.getCompletedTimestamps();

		for (Map.Entry<Integer, Timestamp> entry : completedTimestamps.entrySet()) {
			Integer orderId = entry.getKey();
			Timestamp completedTimestamp = entry.getValue();

			if (completedTimestamp.toLocalDateTime().toLocalDate().equals(LocalDate.now())) {
				String jpql = "SELECT COALESCE(SUM(od.unitPrice * od.quantity * (1 - od.discount)), 0) " +
						"FROM Order o " +
						"JOIN o.orderDetails od " +
						"WHERE o.id = :orderId " +
						"AND o.status = 0 " +
						"AND (od.unitPrice * od.quantity * (1 - od.discount)) < 0"; // Thua lỗ

				Double orderLoss = (Double) entityManager.createQuery(jpql)
						.setParameter("orderId", orderId)
						.getSingleResult();

				loss += orderLoss;
			}
		}

		return loss;
	}

	@Override
	public Map<LocalDate, Double> getMonthlyProfit(int year, int month) {
		String jpql = "SELECT DATE_TRUNC('day', o.orderDate) AS day, COALESCE(SUM(od.unitPrice * od.quantity * (1 - od.discount)), 0) AS profit " +
				"FROM Order o " +
				"JOIN o.orderDetails od " +
				"WHERE EXTRACT(YEAR FROM o.orderDate) = :year " +
				"AND EXTRACT(MONTH FROM o.orderDate) = :month " +
				"AND o.status = 0 " +
				"GROUP BY DATE_TRUNC('day', o.orderDate) " +
				"ORDER BY DATE_TRUNC('day', o.orderDate)";
		TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
		query.setParameter("year", year);
		query.setParameter("month", month);

		List<Object[]> results = query.getResultList();
		Map<LocalDate, Double> profits = new TreeMap<>();
		for (Object[] result : results) {
			LocalDate day = ((Timestamp) result[0]).toLocalDateTime().toLocalDate();
			Double profit = (Double) result[1];
			profits.put(day, profit);
		}
		return profits;
	}

	@Override
	public Map<LocalDate, Double> getMonthlyLoss(int year, int month) {
		String jpql = "SELECT DATE_TRUNC('day', o.orderDate) AS day, COALESCE(SUM(od.unitPrice * od.quantity * (1 - od.discount)), 0) AS loss " +
				"FROM Order o " +
				"JOIN o.orderDetails od " +
				"WHERE EXTRACT(YEAR FROM o.orderDate) = :year " +
				"AND EXTRACT(MONTH FROM o.orderDate) = :month " +
				"AND o.status = 0 " +
				"AND od.unitPrice * od.quantity * (1 - od.discount) < 0 " +
				"GROUP BY DATE_TRUNC('day', o.orderDate) " +
				"ORDER BY DATE_TRUNC('day', o.orderDate)";
		TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
		query.setParameter("year", year);
		query.setParameter("month", month);

		List<Object[]> results = query.getResultList();
		Map<LocalDate, Double> losses = new TreeMap<>();
		for (Object[] result : results) {
			LocalDate day = ((Timestamp) result[0]).toLocalDateTime().toLocalDate();
			Double loss = (Double) result[1];
			losses.put(day, loss);
		}
		return losses;
	}

	@Override
	public Map<LocalDate, Double> getWeeklyProfit(int year, int week) {
		String jpql = "SELECT DATE_TRUNC('day', o.orderDate) AS day, COALESCE(SUM(od.unitPrice * od.quantity * (1 - od.discount)), 0) AS profit " +
				"FROM Order o " +
				"JOIN o.orderDetails od " +
				"WHERE EXTRACT(YEAR FROM o.orderDate) = :year " +
				"AND TO_CHAR(o.orderDate, 'IW') = :week " +
				"AND o.status = 0 " +
				"GROUP BY DATE_TRUNC('day', o.orderDate) " +
				"ORDER BY DATE_TRUNC('day', o.orderDate)";
		TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
		query.setParameter("year", year);
		query.setParameter("week", String.format("%02d", week));

		List<Object[]> results = query.getResultList();
		Map<LocalDate, Double> profits = new TreeMap<>();
		for (Object[] result : results) {
			LocalDate day = ((Timestamp) result[0]).toLocalDateTime().toLocalDate();
			Double profit = (Double) result[1];
			profits.put(day, profit);
		}
		return profits;
	}

	@Override
	public Map<LocalDate, Double> getWeeklyLoss(int year, int week) {
		String jpql = "SELECT DATE_TRUNC('day', o.orderDate) AS day, COALESCE(SUM(od.unitPrice * od.quantity * (1 - od.discount)), 0) AS loss " +
				"FROM Order o " +
				"JOIN o.orderDetails od " +
				"WHERE EXTRACT(YEAR FROM o.orderDate) = :year " +
				"AND TO_CHAR(o.orderDate, 'IW') = :week " +
				"AND o.status = 0 " +
				"AND od.unitPrice * od.quantity * (1 - od.discount) < 0 " +
				"GROUP BY DATE_TRUNC('day', o.orderDate) " +
				"ORDER BY DATE_TRUNC('day', o.orderDate)";
		TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
		query.setParameter("year", year);
		query.setParameter("week", String.format("%02d", week));

		List<Object[]> results = query.getResultList();
		Map<LocalDate, Double> losses = new TreeMap<>();
		for (Object[] result : results) {
			LocalDate day = ((Timestamp) result[0]).toLocalDateTime().toLocalDate();
			Double loss = (Double) result[1];
			losses.put(day, loss);
		}
		return losses;
	}

}
