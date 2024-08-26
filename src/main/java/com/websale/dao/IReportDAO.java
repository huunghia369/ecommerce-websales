package com.websale.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IReportDAO {
	List<Object[]> inventory();
	List<Object[]> inventory(Date startDate, Date endDate);
	List<Object[]> revenueByProduct(Integer categoryId);
	List<Object[]> revenueByProduct(Integer categoryId, Date startDate, Date endDate);
	List<Object[]> revenueByCategory();
	List<Object[]> revenueByCategory(Date startDate, Date endDate);
	List<Object[]> revenueByCustomer();
	List<Object[]> revenueByCustomer(Date startDate, Date endDate);
	List<Object[]> revenueByYear();
	List<Object[]> revenueByMonth();
	List<Object[]> revenueByDateRange(Date startDate, Date endDate);
	Double getDailyProfit();
	Double getDailyLoss();
	Map<LocalDate, Double> getMonthlyProfit(int year, int month);
	Map<LocalDate, Double> getMonthlyLoss(int year, int month);
	Map<LocalDate, Double> getWeeklyProfit(int year, int week);
	Map<LocalDate, Double> getWeeklyLoss(int year, int week);
}
