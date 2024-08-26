package com.websale.controller.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.websale.entity.Category;
import com.websale.service.ICategoryService;
import com.websale.service.IReportService;

@Controller
public class InventoryAndReportControlller {

	@Autowired
	private IReportService reportService;
	
	@Autowired ICategoryService categoryService;

	@RequestMapping("/admin/inventory/category")
	public String inventoryByCategory(Model model) {
		model.addAttribute("data", reportService.inventory());
		return "admin/report/inventory-by-category";
	}

	@GetMapping("/admin/inventory-by-category-date")
	public String inventoryByCategoryAndDateRange(
			@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			Model model) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? sdf.parse(startDateStr) : null;
			Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? sdf.parse(endDateStr) : null;

			if (startDate != null && endDate != null) {
				List<Object[]> data = reportService.inventory(startDate, endDate);
				model.addAttribute("data", data);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "admin/report/inventory-by-category";
	}

	@RequestMapping("/admin/revenue/category")
	public String reportByCategory(Model model) {
		model.addAttribute("rpcates", reportService.revenueByCategory());
		return "admin/report/revenue-by-category";
	}

	@GetMapping("/admin/revenue-by-category-date")
	public String revenueByCategoryAndDateRange(
			@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			Model model) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? sdf.parse(startDateStr) : null;
			Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? sdf.parse(endDateStr) : null;

			if (startDate != null && endDate != null) {
				List<Object[]> data = reportService.revenueByCategory(startDate, endDate);
				model.addAttribute("rpcates", data);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "admin/report/revenue-by-category";
	}

	@RequestMapping("/admin/revenue/customer")
	public String reportByCustomer(Model model) {
		model.addAttribute("rpcustomer", reportService.revenueByCustomer());
		return "admin/report/revenue-by-customer";
	}

	@GetMapping("/admin/revenue-by-customer-date")
	public String revenueByCustomerAndDateRange(
			@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			Model model) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? sdf.parse(startDateStr) : null;
			Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? sdf.parse(endDateStr) : null;

			if (startDate != null && endDate != null) {
				List<Object[]> data = reportService.revenueByCustomer(startDate, endDate);
				model.addAttribute("rpcustomer", data);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "admin/report/revenue-by-customer";
	}

	@RequestMapping("/admin/revenue/product")
	public String reportByProduct(Model model,
			@RequestParam(name = "category_id",required = false) Integer category_id) {
		List<Category> list = categoryService.findAll();
		if (category_id == null) {
			category_id = list.get(0).getId();
		}
		model.addAttribute("categories", list);
		model.addAttribute("rpProduct", reportService.revenueByProduct(category_id));
		return "admin/report/revenue-by-product";
	}

	@GetMapping("/admin/revenue-by-product-date")
	public String revenueByProductAndDateRange(
			@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			@RequestParam(name = "category_id", required = false) Integer category_id,
			Model model) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? sdf.parse(startDateStr) : null;
			Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? sdf.parse(endDateStr) : null;

			if (startDate != null && endDate != null) {
				if (category_id == null) {
					List<Category> list = categoryService.findAll();
					category_id = list.get(0).getId();
				}
				List<Object[]> data = reportService.revenueByProduct(category_id, startDate, endDate);
				model.addAttribute("rpProduct", data);
				model.addAttribute("categories", categoryService.findAll());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "admin/report/revenue-by-product";
	}
	
	@RequestMapping("/admin/revenue/month")
	public String revenueByMonth(Model model) {
		model.addAttribute("rpMonth", reportService.revenueByMonth());
		return "admin/report/revenue-by-month";
	}

	@RequestMapping("/admin/revenue/year")
	public String revenueByYear(Model model) {
		model.addAttribute("rpYear", reportService.revenueByYear());
		return "admin/report/revenue-by-year";
	}

	@GetMapping("/admin/revenue-by-date")
	public String getRevenueByDate(
			@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			Model model) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? sdf.parse(startDateStr) : null;
			Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? sdf.parse(endDateStr) : null;

			if (startDate != null && endDate != null) {
				List<Object[]> data = reportService.revenueByDateRange(startDate, endDate);
				model.addAttribute("revenueData", data);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "admin/report/revenue-by-product";
	}


}
