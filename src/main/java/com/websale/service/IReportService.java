package com.websale.service;

import java.util.Date;
import java.util.List;

public interface IReportService {
	List<Object[]> inventory();
	List<Object[]> inventory(Date startDate, Date endDate);
	List<Object[]> revenueByCategory();
	List<Object[]> revenueByCategory(Date startDate, Date endDate);
	List<Object[]> revenueByProduct(Integer categoryId);
	List<Object[]> revenueByProduct(Integer categoryId, Date startDate, Date endDate);
	List<Object[]> revenueByCustomer();
	List<Object[]> revenueByCustomer(Date startDate, Date endDate);
	List<Object[]> revenueByYear();
	List<Object[]> revenueByMonth();
	List<Object[]> revenueByDateRange(Date startDate, Date endDate);
}
