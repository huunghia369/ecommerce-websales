package com.websale.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websale.dao.IReportDAO;
import com.websale.service.IReportService;
@Service
public class ReportService implements IReportService {
	
	@Autowired
	private IReportDAO dao;

	@Override
	public List<Object[]> inventory() {
		return dao.inventory();
	}

	@Override
	public List<Object[]> inventory(Date startDate, Date endDate) {
		return dao.inventory(startDate, endDate);
	}

	@Override
	public List<Object[]> revenueByCategory() {
		return dao.revenueByCategory();
	}

	@Override
	public List<Object[]> revenueByCategory(Date startDate, Date endDate) {
		return dao.revenueByCategory(startDate, endDate);
	}

	@Override
	public List<Object[]> revenueByCustomer() {
		return dao.revenueByCustomer();
	}

	@Override
	public List<Object[]> revenueByCustomer(Date startDate, Date endDate) {
		return dao.revenueByCustomer(startDate, endDate);
	}

	@Override
	public List<Object[]> revenueByYear() {
		return dao.revenueByYear();
	}

	@Override
	public List<Object[]> revenueByMonth() {	
		return dao.revenueByMonth();
	}

	@Override
	public List<Object[]> revenueByProduct(Integer categoryId) {
	return dao.revenueByProduct(categoryId);
	}

	@Override
	public List<Object[]> revenueByProduct(Integer categoryId, Date startDate, Date endDate) {
		return dao.revenueByProduct(categoryId, startDate, endDate);
	}

	@Override
	public List<Object[]> revenueByDateRange(Date startDate, Date endDate){ return dao.revenueByDateRange(startDate, endDate);}
}
