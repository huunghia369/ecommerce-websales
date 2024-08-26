package com.websale.controller.admin;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.websale.dao.ICustomerDAO;
import com.websale.dao.IOrderDAO;
import com.websale.dao.IProductDAO;
import com.websale.entity.Customer;
import com.websale.entity.Product;
import com.websale.service.IAccountService;
import com.websale.service.IMailService;
import com.websale.service.IOrderSevice;
import com.websale.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import com.websale.dao.IReportDAO;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class AdminHomeController {

	@Autowired
	private IReportDAO reportDAO;

	@Autowired
	private IProductDAO productDAO;

	@Autowired
	private ICustomerDAO customerDAO;

	@Autowired
	private IOrderDAO orderDAO;

	@Autowired
	private IOrderSevice orderService;

	@Autowired
	private IAccountService accountService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IMailService mailService;

	@RequestMapping("/admin/home/index")
	public String index(Model model, HttpSession session) {
		List<Product> allLowStockProducts = productDAO.getLowStockProducts(10);
		List<Product> lowStockProducts = allLowStockProducts.stream()
				.limit(9)
				.collect(Collectors.toList());
		for (Product product : lowStockProducts) {
			int quantitySold = orderService.getQuantitySold(product.getId());
			int quantityInTransit = orderService.getQuantityInTransit(product.getId());
			int availableQuantity = product.getQuantity() - quantityInTransit;
			product.setSoldQuantity(quantitySold);
			product.setAvailableQuantity(availableQuantity);
		}
		model.addAttribute("lowStockProducts", lowStockProducts);

		List<Product> topSellingProducts = productDAO.getTopSellingProducts(7);
		for (Product product : topSellingProducts) {
			int quantitySold = orderService.getQuantitySold(product.getId());
			int quantityInTransit = orderService.getQuantityInTransit(product.getId());
			int availableQuantity = product.getQuantity() - quantityInTransit;
			product.setSoldQuantity(quantitySold);
			product.setAvailableQuantity(availableQuantity);
		}
		model.addAttribute("topSellingProducts", topSellingProducts);

		List<Object[]> topCustomersData = customerDAO.getTopCustomers(5);
		List<Customer> topCustomers = new ArrayList<>();
		for (Object[] row : topCustomersData) {
			Customer customer = new Customer((String) row[0]);
			customer.setFullname((String) row[1]);
			customer.setEmail((String) row[2]);
			customer.setPhone((String) row[3]);
			customer.setTotalPurchases((Double) row[4]);
			topCustomers.add(customer);
		}
		model.addAttribute("topCustomers", topCustomers);

		String customerId = (String) session.getAttribute("currentUserId");
		if (customerId != null) {
			// Lấy thông tin về số lần truy cập của khách hàng
			int accessCount = accountService.countTotalAccesses();
			model.addAttribute("totalAccesses", accessCount);

			// Lấy thông tin về số lượng khách hàng đang hoạt động
			int activeCustomerCount = accountService.countActiveCustomers();
			model.addAttribute("activeCustomers", activeCustomerCount);
		}

		// Lấy lợi nhuận - thua lỗ hàng ngày
		Double profitToday = reportDAO.getDailyProfit();
		Double lossToday = reportDAO.getDailyLoss();
		model.addAttribute("profitToday", profitToday);
		model.addAttribute("lossToday", lossToday);

		Date today = new Date();
		int currentMonth = LocalDate.now().getMonthValue();
		int year = LocalDate.now().getYear();
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		int currentWeek = LocalDate.now().get(weekFields.weekOfWeekBasedYear());

		// Lấy số lượng đã bán - đang giao
		int quantitySoldToday = orderDAO.getQuantitySoldInDate(today);
		int quantityInTransitToday = orderDAO.getQuantityInTransitInDate();

		model.addAttribute("quantitySoldToday", quantitySoldToday);
		model.addAttribute("quantityInTransitToday", quantityInTransitToday);

		// Báo cáo lợi nhuận - thua lỗ hàng tháng
		Map<LocalDate, Double> currentMonthProfit = reportDAO.getMonthlyProfit(year, currentMonth);
		Map<LocalDate, Double> previousMonthProfit = reportDAO.getMonthlyProfit(year, currentMonth == 1 ? 12 : currentMonth - 1);

		double totalCurrentMonthProfit = currentMonthProfit.values().stream().mapToDouble(Double::doubleValue).sum();
		double totalPreviousMonthProfit = previousMonthProfit.values().stream().mapToDouble(Double::doubleValue).sum();

		Map<LocalDate, Double> currentMonthLoss = reportDAO.getMonthlyLoss(year, currentMonth);
		Map<LocalDate, Double> previousMonthLoss = reportDAO.getMonthlyLoss(year, currentMonth == 1 ? 12 : currentMonth - 1);

		double totalCurrentMonthLoss = currentMonthLoss.values().stream().mapToDouble(Double::doubleValue).sum();
		double totalPreviousMonthLoss = previousMonthLoss.values().stream().mapToDouble(Double::doubleValue).sum();

		model.addAttribute("totalCurrentMonthProfit", totalCurrentMonthProfit);
		model.addAttribute("totalPreviousMonthProfit", totalPreviousMonthProfit);
		model.addAttribute("profitDifference", totalCurrentMonthProfit - totalPreviousMonthProfit);
		model.addAttribute("totalCurrentMonthLoss", totalCurrentMonthLoss);
		model.addAttribute("totalPreviousMonthLoss", totalPreviousMonthLoss);
		model.addAttribute("lossDifference", totalCurrentMonthLoss - totalPreviousMonthLoss);

		// Báo cáo lợi nhuận - thua lỗ hàng tuần
		Map<LocalDate, Double> currentWeekProfit = reportDAO.getWeeklyProfit(year, currentWeek);
		Map<LocalDate, Double> previousWeekProfit = reportDAO.getWeeklyProfit(year, currentWeek - 1);

		Double totalCurrentWeekProfit = currentWeekProfit.values().stream().mapToDouble(Double::doubleValue).sum();
		Double totalPreviousWeekProfit = previousWeekProfit.values().stream().mapToDouble(Double::doubleValue).sum();
		Double weekProfitDifference = totalCurrentWeekProfit - totalPreviousWeekProfit;

		Map<LocalDate, Double> currentWeekLoss = reportDAO.getWeeklyLoss(year, currentWeek);
		Map<LocalDate, Double> previousWeekLoss = reportDAO.getWeeklyLoss(year, currentWeek - 1);

		Double totalCurrentWeekLoss = currentWeekLoss.values().stream().mapToDouble(Double::doubleValue).sum();
		Double totalPreviousWeekLoss = previousWeekLoss.values().stream().mapToDouble(Double::doubleValue).sum();
		Double weekLossDifference = totalCurrentWeekLoss - totalPreviousWeekLoss;

		model.addAttribute("totalCurrentWeekProfit", totalCurrentWeekProfit);
		model.addAttribute("totalPreviousWeekProfit", totalPreviousWeekProfit);
		model.addAttribute("weekProfitDifference", weekProfitDifference);
		model.addAttribute("totalCurrentWeekLoss", totalCurrentWeekLoss);
		model.addAttribute("totalPreviousWeekLoss", totalPreviousWeekLoss);
		model.addAttribute("weekLossDifference", weekLossDifference);

		return "admin/home/index";
	}

	@PostMapping("/update-stock")
	public String updateStock(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
		StringBuilder emailContent = new StringBuilder();
		emailContent.append("<h2>Thông báo Cập nhật Số lượng Hàng tồn kho</h2>")
				.append("<p>Xin chào,</p>")
				.append("<p>Dưới đây là chi tiết về các sản phẩm đã được cập nhật số lượng trong kho:</p>")
				.append("<table border='1' cellpadding='10' cellspacing='0'>")
				.append("<thead>")
				.append("<tr>")
				.append("<th style='background-color:#f2f2f2;'>Id</th>")
				.append("<th style='background-color:#f2f2f2;'>Tên Sản phẩm</th>")
				.append("<th style='background-color:#f2f2f2;'>Số lượng Ban đầu</th>")
				.append("<th style='background-color:#f2f2f2;'>Số lượng Cập nhật</th>")
				.append("</tr>")
				.append("</thead>")
				.append("<tbody>");
		boolean stockChanged = false;

		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			String[] parts = key.split("_");
			if (parts.length == 2) {
				String productIdStr = parts[1];
				Integer productId = Integer.parseInt(productIdStr);
				Integer newQuantity = Integer.parseInt(value);

				Product product = productService.findById(productId);
				if (product != null) {
					Integer originalQuantity = product.getQuantity();

					if (!originalQuantity.equals(newQuantity)) {
						product.setQuantity(newQuantity);
						productService.update(product);

						emailContent.append("<tr>")
								.append("<td>").append(product.getId()).append("</td>")
								.append("<td>").append(product.getName()).append("</td>")
								.append("<td style='text-align:center;'>").append(originalQuantity).append("</td>")
								.append("<td style='text-align:center;'>").append(newQuantity - originalQuantity).append("</td>")
								.append("</tr>");

						stockChanged = true;
					}
				}
			}
		}

		emailContent.append("</tbody>")
				.append("</table>")
				.append("<p>Trân trọng,</p>")
				.append("<p>Đội ngũ Quản lý Kho</p>");

		if (stockChanged) {
			String email = "clonenghia03@gmail.com";
			String subject = "Thông báo cập nhật số lượng hàng tồn kho";
			String body = emailContent.toString();

			mailService.send(email, subject, body);
		}

		redirectAttributes.addFlashAttribute("message", "Cập nhật số lượng thành công");
		return "redirect:/admin/home/index";
	}

	@PostMapping("/contact-customer")
	public String contactCustomer(@RequestParam("email") String email,
							@RequestParam("subject") String subject,
							@RequestParam("body") String body,
							RedirectAttributes redirectAttributes) {
		mailService.send(email, subject, body);
		redirectAttributes.addFlashAttribute("message", "Email đã được gửi thành công!");
		return "redirect:/admin/home/index"; // Redirect to a relevant page
	}

	@GetMapping("/admin/home/report/all-pdf")
	public ResponseEntity<InputStreamResource> generateAllPdfReports() throws IOException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try (PdfWriter writer = new PdfWriter(outputStream);
			 PdfDocument pdf = new PdfDocument(writer);
			 Document document = new Document(pdf)) {

			// Tạo font hỗ trợ Unicode
			PdfFont font = PdfFontFactory.createFont("src/main/webapp/font/dejavu-fonts-ttf-2.37/ttf/DejaVuSans.ttf", PdfEncodings.IDENTITY_H);

			Paragraph titleParagraph = new Paragraph("BÁO CÁO")
					.setTextAlignment(TextAlignment.CENTER)
					.setBold()
					.setFontSize(20)
					.setFont(font);

			document.add(titleParagraph);

			// Danh sách các loại báo cáo
			String[] reportTypes = {
					"revenueByCategory",
					"revenueByCustomer",
					"revenueByYear",
					"revenueByMonth",
					"revenueByProduct",
					"inventory"
			};

			// Danh sách các categoryId cho revenueByProduct
			int[] productCategoryIds = {1, 2, 3};

			boolean isLastReport = false;

			for (int typeIndex = 0; typeIndex < reportTypes.length; typeIndex++) {
				String type = reportTypes[typeIndex];
				List<Object[]> data;
				String title;
				String[] headers;

				switch (type) {
					case "revenueByCategory":
						title = "Doanh thu theo danh mục";
						data = reportDAO.revenueByCategory();
						headers = new String[]{"Loại sản phẩm", "Số lượng", "Doanh số", "Giá bán thấp nhất", "Giá bán cao nhất", "Trung bình"};
						break;
					case "revenueByCustomer":
						title = "Doanh thu theo khách hàng";
						data = reportDAO.revenueByCustomer();
						headers = new String[]{"Khách hàng", "Số lượng", "Doanh số", "Giá bán thấp nhất", "Giá bán cao nhất", "Trung bình"};
						break;
					case "revenueByYear":
						title = "Doanh thu theo năm";
						data = reportDAO.revenueByYear();
						headers = new String[]{"Năm", "Số lượng", "Doanh số", "Giá thấp nhất", "Giá cao nhất", "Giá trung bình"};
						break;
					case "revenueByMonth":
						title = "Doanh thu theo tháng";
						data = reportDAO.revenueByMonth();
						headers = new String[]{"Tháng", "Số lượng", "Doanh số", "Giá thấp", "Giá cao", "Trung Bình"};
						break;
					case "revenueByProduct":
						for (int categoryIndex = 0; categoryIndex < productCategoryIds.length; categoryIndex++) {
							int categoryId = productCategoryIds[categoryIndex];
							title = "Doanh thu theo sản phẩm - Danh mục " + categoryId;
							data = reportDAO.revenueByProduct(categoryId);
							headers = new String[]{"Sản phẩm", "Số lượng", "Doanh số", "Giá thấp nhất", "Giá cao nhất", "Trung bình", "Loại"};

							// Thêm tiêu đề báo cáo với font hỗ trợ Unicode
							Paragraph sectionTitle = new Paragraph(title)
									.setFont(font)
									.setBold()
									.setFontSize(16)
									.setTextAlignment(TextAlignment.CENTER)
									.setMarginBottom(10); // Khoảng cách dưới tiêu đề

							document.add(sectionTitle);

							// Thêm dữ liệu vào bảng
							if (!data.isEmpty()) {
								Table table = new Table(UnitValue.createPercentArray(headers.length)).useAllAvailableWidth();
								table.setKeepTogether(true); // Giữ toàn bộ bảng trên cùng một trang nếu có thể

								// Thêm tiêu đề cột
								for (String header : headers) {
									Cell headerCell = new Cell().add(new Paragraph(header).setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));
									table.addHeaderCell(headerCell);
								}

								// Thêm dữ liệu
								for (Object[] row : data) {
									for (Object cell : row) {
										Paragraph cellContent = new Paragraph(cell != null ? cell.toString() : "")
												.setFont(font)
												.setTextAlignment(TextAlignment.LEFT);

										// Tạo ô với nội dung và thiết lập chiều rộng
										Cell dataCell = new Cell().add(cellContent);
										dataCell.setPadding(5);
										table.addCell(dataCell);
									}
								}

								document.add(table.setMarginBottom(20)); // Khoảng cách dưới bảng
							} else {
								document.add(new Paragraph("Không có dữ liệu để hiển thị").setFont(font).setMarginBottom(20));
							}

							// Kiểm tra nếu đây không phải là báo cáo cuối cùng
							isLastReport = (typeIndex == reportTypes.length - 1 && categoryIndex == productCategoryIds.length - 1);

							// Thêm ngắt trang sau mỗi báo cáo, trừ báo cáo cuối cùng
							if (!isLastReport) {
								document.add(new AreaBreak());
							}
						}
						continue;

					default:
						title = "Tổng hợp tồn kho";
						data = reportDAO.inventory();
						headers = new String[]{"Loại sản phẩm", "Tổng số lượng", "Giá trị", "Giá thấp nhất", "Giá cao nhất", "Trung bình"};
						break;
				}

				// Thêm tiêu đề báo cáo với font hỗ trợ Unicode
				Paragraph sectionTitle = new Paragraph(title)
						.setFont(font)
						.setBold()
						.setFontSize(16)
						.setTextAlignment(TextAlignment.CENTER);

				document.add(sectionTitle);

				// Thêm dữ liệu vào bảng
				if (!data.isEmpty()) {
					Table table = new Table(UnitValue.createPercentArray(headers.length)).useAllAvailableWidth();

					// Thêm tiêu đề cột
					for (String header : headers) {
						table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(font).setBold().setTextAlignment(TextAlignment.CENTER)));
					}

					// Thêm dữ liệu
					for (Object[] row : data) {
						for (Object cell : row) {
							table.addCell(new Cell().add(new Paragraph(cell.toString()).setFont(font)));
						}
					}

					document.add(table);
				} else {
					document.add(new Paragraph("Không có dữ liệu để hiển thị").setFont(font));
				}

				// Kiểm tra nếu đây là báo cáo cuối cùng
				isLastReport = typeIndex == reportTypes.length - 1;

				// Thêm ngắt trang sau mỗi báo cáo, trừ báo cáo cuối cùng
				if (!isLastReport) {
					document.add(new AreaBreak());
				}
			}
		}

		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=all_reports.pdf");

		return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
	}
}
