package com.websale.controller.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.websale.service.IHttpService;
import com.websale.service.IMailService;
import com.websale.service.impl.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websale.entity.Order;
import com.websale.service.IOrderSevice;
import com.websale.service.impl.CartService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class OrderController {

	@Autowired
	private CartService cartService;

	@Autowired
	private IOrderSevice orderService;

	@Autowired
	private VNPayService vnPayService;

	@Autowired
	private IMailService mailService;

	@Autowired
	private IHttpService http;

	@GetMapping("/order/checkout")
	public String checkOut(Model model,RedirectAttributes attributes) {
		if (cartService.getCountCart() <= 0) {
			attributes.addFlashAttribute("message","Chưa có sản phẩm trong giỏ hàng");
			return "redirect:/cart/view";
		}
		model.addAttribute("cart", cartService);
		Order order = orderService.createOrder();
		model.addAttribute("order", order);
		return "order/checkout";
	}

	@PostMapping("/order/checkout")
	public String checkOut(RedirectAttributes redirectAttributes, @Validated @ModelAttribute("order") Order order,
						   @RequestParam("paymentMethod") String paymentMethod) {
		try {
			// Thêm đơn hàng và chi tiết đơn hàng vào cơ sở dữ liệu
			orderService.addOrderAndOrderDetail(order, cartService);
			// Xóa sản phẩm trong giỏ hàng
			cartService.clear();

			// Kiểm tra phương thức thanh toán
			if ("vnpay".equals(paymentMethod)) {
				// Chuyển hướng người dùng đến VNPAY để thực hiện thanh toán
				String paymentUrl = vnPayService.createPaymentUrl(
						String.valueOf(order.getId()),
						order.getAmount(),
						"Thanh toán đơn hàng #" + order.getId(),
						"http://localhost:8081/order/vnpay/return"
				);
				return "redirect:" + paymentUrl;
			} else if ("cod".equals(paymentMethod)) {
				// Xử lý thanh toán COD
				order.setStatus(1); // Trạng thái chờ xử lý
				orderService.updateOrder(order);
				redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công. Đơn hàng đang chờ xử lý");
				return "redirect:/home/index";
			} else {
				redirectAttributes.addFlashAttribute("error", "Phương thức thanh toán không hợp lệ!");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi trong quá trình đặt hàng. Vui lòng thử lại sau.");
		}
		return "redirect:/home/index";
	}

	@RequestMapping("/order/list")
	public String listOrder(Model model) {
		List<Order> list = orderService.getAllOrderByUser();
		model.addAttribute("orders", list);
		model.addAttribute("ordersWaiting",(List<Order>) list.stream().filter(item -> item.getStatus() == 1).collect(Collectors.toList()));
		model.addAttribute("ordersDelivery",
				(List<Order>) list.stream().filter(item -> item.getStatus() == 2).collect(Collectors.toList()));
		model.addAttribute("ordersDeliverted",
				(List<Order>) list.stream().filter(item -> item.getStatus() == 3).collect(Collectors.toList()));
		model.addAttribute("ordersCancel",
				(List<Order>) list.stream().filter(item -> item.getStatus() == 4).collect(Collectors.toList()));
		return "order/list";
	}

	@RequestMapping("/order/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id) {
		Order order = orderService.findById(id);
		model.addAttribute("order", order);
		return "order/detail";
	}

	@RequestMapping("/order/items")
	public String getPurchasedItems(Model model) {
		model.addAttribute("list", orderService.getPurchasedItems().values());
		return "product/list";
	}

	@GetMapping("/order/vnpay/return")
	public String vnPayReturn(RedirectAttributes redirectAttributes, HttpServletRequest request) {
		Map<String, String> params = request.getParameterMap().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

		try {
			boolean isSuccess = vnPayService.validateReturn(params);
			if (isSuccess) {
				int orderId = Integer.parseInt(params.get("vnp_TxnRef"));
				Order order = orderService.findById(orderId);
				order.setStatus(2); // Cập nhật trạng thái đơn hàng (Đã xác nhận - Đang giao)
				orderService.updateOrder(order); // Lưu đơn hàng sau khi cập nhật trạng thái
				// Gửi email thông báo cho khách hàng
				String email = order.getCustomer().getEmail();
				String subject = "Thông báo về đơn hàng #" + order.getId();
				String body = "Kính gửi quý khách,<br/><br/>" +
						"Chúng tôi xin thông báo rằng đơn hàng của bạn đã được thanh toán thành công.<br/><br/>" +
						"Đơn hàng sẽ được giao trong thời gian tới.<br/><br/>" +
						"Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi.<br/><br/>" +
						"Trân trọng,<br/>Đội ngũ hỗ trợ.";
				mailService.send(email, subject, body);
				redirectAttributes.addFlashAttribute("message", "Thanh toán thành công!");
			} else {
				redirectAttributes.addFlashAttribute("error", "Thanh toán không thành công!");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi xử lý thanh toán.");
		}

		return "redirect:/home/index";
	}
}
