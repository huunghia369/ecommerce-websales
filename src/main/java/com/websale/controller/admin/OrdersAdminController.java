package com.websale.controller.admin;

import java.io.IOException;

import com.websale.dao.IOrderDetailDAO;
import com.websale.entity.OrderDetail;
import com.websale.entity.Product;
import com.websale.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websale.entity.Order;

import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/admin/order/")
public class OrdersAdminController {
	@Autowired
	private IOrderSevice orderService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IOrderDetailDAO orderDetailDAO;

	@Autowired
	private IMailService mailService;

	@Autowired
	private IHttpService http;

	@RequestMapping("index")
	public String index(Model model) {
		model.addAttribute("order", new Order());
		List<Order> orderList = orderService.findAll();
		orderList.sort(Comparator.comparing(Order::getId).reversed());
		model.addAttribute("list", orderList);
		return "admin/order/index";
	}

	@RequestMapping("edit/{id}")
	public String edit(Model model, @PathVariable("id") Integer id) {
		model.addAttribute("order", orderService.findById(id));
		List<Order> orderList = orderService.findAll();
		orderList.sort(Comparator.comparing(Order::getId).reversed());
		model.addAttribute("list", orderList);
		return "admin/order/index";
	}

	@RequestMapping("create")
	public String create(Model model, RedirectAttributes params, @Validated Order form,
			BindingResult errors) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Vui lòng không bỏ trống các trường bên trên");
			List<Order> orderList = orderService.findAll();
			orderList.sort(Comparator.comparing(Order::getId).reversed());
			model.addAttribute("list", orderList);
			return "admin/order/index";
		}
//		orderService.add(form);
		params.addAttribute("message", "Cần mua hàng để thêm đơn hàng!");
		return "redirect:/admin/order/index"; // redirect lại để mất dữ liệu trên form
	}

	@RequestMapping("update")
	public String update(Model model, RedirectAttributes params, @Validated  Order form,
			BindingResult errors) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Vui lòng không bỏ trống bên dưới");
			List<Order> orderList = orderService.findAll();
			orderList.sort(Comparator.comparing(Order::getId).reversed());
			model.addAttribute("list", orderList);
			return "admin/order/index";
		}
		Order order = orderService.findById(form.getId());

		// Sử dụng biến tạm thời để lưu trữ thời gian hoàn tất
		Timestamp completedTimestamp = null;

		if (form.getStatus() == 0 && order.getStatus() == 3) { //Cập nhật thời gian hoàn tất đơn hàng khi chuyển từ trạng thái "Đã giao" sang "Hoàn tất"
			completedTimestamp = new Timestamp(System.currentTimeMillis());
			orderService.setCompletedTimestamp(form.getId(), completedTimestamp);
		}

		// Nếu không có trạng thái mới từ form, giữ nguyên trạng thái cũ
		if (form.getStatus() == null) {
			form.setStatus(order.getStatus());
		}

		orderService.updateOrder(form);

		if (form.getStatus() == 0) { // Kiểm tra nếu trạng thái đơn hàng là "Hoàn tất" (status = 0)
			// Giá trị gửi đến khi chuyển đến form in hóa đơn
			params.addFlashAttribute("orderId", form.getId());
			params.addFlashAttribute("printInvoice", true);

			// Gửi email thông báo cho khách hàng
			String email = order.getCustomer().getEmail();
			String subject = "Thông báo về đơn hàng #" + form.getId();
			String url = http.getCurrentUrl().replace("/update", "/invoice/" + order.getId());
			String body = "Kính gửi quý khách,<br/><br/>Hóa đơn cho đơn hàng #" + order.getId() +
					" đã hoàn tất.<br/><br/>Bạn có thể tải về hóa đơn từ liên kết sau: <br/><a href='" +
					url + "'>Tải hóa đơn</a><br/><br/>Trân trọng,<br/>Đội ngũ hỗ trợ.";
			mailService.send(email, subject, body);

		} else if (form.getStatus() == 4) { // Kiểm tra nếu trạng thái đơn hàng là "Đã hủy" (status = 4)
			// Gửi email thông báo cho khách hàng về việc đơn hàng bị hủy
			String email = order.getCustomer().getEmail();
			String subject = "Thông báo về đơn hàng #" + form.getId();
			String body = "Kính gửi quý khách,<br/><br/>Đơn hàng #" + order.getId() +
					" đã bị hủy.<br/><br/>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.<br/><br/>Trân trọng,<br/>Đội ngũ hỗ trợ.";
			mailService.send(email, subject, body);
		} else if (form.getStatus() == 3) { // Kiểm tra nếu trạng thái đơn hàng là "Đã giao" (status = 3)
			boolean hasError = false;
			StringBuilder errorMessage = new StringBuilder();

			// Trừ số lượng sản phẩm trong kho
			for (OrderDetail detail : order.getOrderDetails()) {
				Product product = detail.getProduct();
				int quantityOrdered = detail.getQuantity();
				int quantityInStock = product.getQuantity();

				if (quantityOrdered > quantityInStock) {
					// Thông báo lỗi nếu số lượng đặt hàng vượt quá số lượng có sẵn
					errorMessage.append("Số lượng đặt hàng vượt quá số lượng có sẵn cho sản phẩm: ").append(product.getName()).append(". ");
					hasError = true;
					// Đặt số lượng sản phẩm về số lượng tối đa có sẵn
					detail.setQuantity(quantityInStock);
				} else {
					product.setQuantity(quantityInStock - quantityOrdered);
					if (product.getQuantity() <= 0) {
						product.setAvailable(false);
					}
					productService.update(product);
				}
			}

			if (hasError) {
				params.addFlashAttribute("message", errorMessage.toString());
				return "redirect:/admin/order/edit/" + form.getId(); // Redirect giữ lại dữ liệu
			}
		}

		params.addAttribute("message", "Cập nhật thành công");
		return "redirect:/admin/order/edit/" + form.getId(); // redirect giữ lại dữ liệu
	}

	@PostMapping("/update-status/{id}")
	public String updateOrderStatus(@PathVariable("id") Integer orderId, RedirectAttributes params) {
		// Tìm đơn hàng theo ID
		Order order = orderService.findById(orderId);

		if (order != null) {
			// Kiểm tra trạng thái hiện tại
			if (order.getStatus() == 1) { // Chờ xác nhận
				order.setStatus(2); // Đã xác nhận
				orderService.updateOrder(order);

				// Gửi email thông báo cho khách hàng
				String email = order.getCustomer().getEmail();
				String subject = "Thông báo về đơn hàng #" + orderId;
				String body = "Kính gửi quý khách,<br/><br/>Đơn hàng #" + orderId +
						" đã được xác nhận.<br/><br/>Trân trọng,<br/>Đội ngũ hỗ trợ.";
				mailService.send(email, subject, body);

				params.addFlashAttribute("message", "Duyệt đơn hàng thành công");
			} else {
				params.addFlashAttribute("message", "Trạng thái đơn hàng không hợp lệ để duyệt");
			}
		} else {
			params.addFlashAttribute("message", "Không tìm thấy đơn hàng");
		}

		return "redirect:/admin/order/edit/" + orderId; // redirect giữ lại dữ liệu;
	}

	@RequestMapping("delete/{id}")
	public String delete(Model model, RedirectAttributes params, @PathVariable("id") Integer id) {
		Order order = orderService.findById(id);
		if (order.getStatus() != 4){ //Xem đơn hàng đã hủy chưa. Nếu hủy rồi có thể xóa
			order.setStatus(4);
			orderService.updateOrder(order);
			params.addAttribute("message", "Đơn hàng đã hủy. Bạn có thể tiếp tục xóa");
			return "redirect:/admin/order/edit/" + id; // redirect giữ lại dữ liệu
		} else {
			List<OrderDetail> list = orderDetailDAO.findByOrder(order);
			if (!list.isEmpty()) {
				params.addAttribute("message", "Đơn hàng này đang được xử lý bên người mua");
				return "redirect:/admin/order/index";
			} else {
				params.addAttribute("message", "Đơn hàng này không thực hiện đúng cách. Hãy xem xét trong CSDL!");
				return "redirect:/admin/order/index";
			}
		}
	}

	@RequestMapping("invoice/{id}")
	public ResponseEntity<ByteArrayResource> generateInvoicePdf(@PathVariable("id") Integer orderId) {
		if (orderId == null) {
			// Xử lý khi không có orderId hợp lệ
			return ResponseEntity.badRequest().build();
		}
		try {
			Order order = orderService.findById(orderId);
			byte[] pdfData = orderService.createInvoicePdf(order);

			// Thiết lập ResponseEntity để trả về PDF
			ByteArrayResource resource = new ByteArrayResource(pdfData);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + orderId + ".pdf");
			headers.setContentType(MediaType.APPLICATION_PDF);
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
