package com.websale.controller.admin;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websale.dao.IOrderDAO;
import com.websale.entity.Order;
import com.websale.entity.Customer;
import com.websale.service.IAccountService;
import com.websale.service.IHttpService;

@Controller
@RequestMapping("/admin/customer/")
public class CustomersAdminController {
	@Autowired
	private IAccountService customerService;

	@Autowired
	private IOrderDAO orderDAO;

	@Autowired
	private IHttpService http;
	
	@Autowired
	ServletContext context;

	@RequestMapping("index")
	public String index(Model model) {
		model.addAttribute("customer", new Customer());
		model.addAttribute("users", customerService.findByRoles(false));
		model.addAttribute("admins", customerService.findByRoles(true));
		return "admin/customer/index";
	}

	@RequestMapping("edit/{id}")
	public String edit(Model model, @PathVariable("id") String id) {
		model.addAttribute("customer", customerService.findById(id));
		model.addAttribute("users", customerService.findByRoles(false));
		model.addAttribute("admins", customerService.findByRoles(true));
		return "admin/customer/index";
	}

	@RequestMapping("create")
	public String create(Model model, RedirectAttributes params, @Validated Customer form, BindingResult errors,
			@RequestParam("photo_file") MultipartFile file) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Vui lòng không bỏ trống các trường bên trên");
			model.addAttribute("users", customerService.findByRoles(false));
			model.addAttribute("admins", customerService.findByRoles(true));
			return "admin/customer/index";
		}
		File photo = http.saveCustomerPhoto(file);
		if (photo != null) {
			form.setPhoto(photo.getName());
		}
		customerService.add(form);
		params.addAttribute("message", "Thêm mới thành công");
		return "redirect:/admin/customer/index"; // redirect lại để mất dữ liệu trên form
	}

	@RequestMapping("update")
	public String update(Model model, RedirectAttributes params, @Validated Customer form, BindingResult errors,@RequestParam("photo_file") MultipartFile file) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Vui lòng không bỏ trống bên dưới");
			model.addAttribute("users", customerService.findByRoles(false));
			model.addAttribute("admins", customerService.findByRoles(true));
			return "admin/customer/index";
		}
		// Kiểm tra email trùng lặp
		Customer existingCustomer = customerService.findByEmail(form.getEmail());
		if (existingCustomer != null && !existingCustomer.getId().equals(form.getId())) {
			model.addAttribute("message", "Email đã tồn tại. Vui lòng chọn email khác.");
			model.addAttribute("users", customerService.findByRoles(false));
			model.addAttribute("admins", customerService.findByRoles(true));
			return "admin/customer/index";
		}
		// Kiểm tra sđt trùng lặp
		Customer existingPhone = customerService.findByPhone(form.getPhone());
		if (existingPhone != null && !existingPhone.getPhone().equals(form.getPhone())) {
			model.addAttribute("message", "SĐT đã tồn tại. Vui lòng chọn sđt khác.");
			model.addAttribute("users", customerService.findByRoles(false));
			model.addAttribute("admins", customerService.findByRoles(true));
			return "admin/customer/index";
		}
		File photo = http.saveCustomerPhoto(file);
		if(photo != null) {
			form.setPhoto(photo.getName());
		}
		customerService.update(form);
		params.addAttribute("message", "Cập nhật thành công");
		return "redirect:/admin/customer/edit/" + form.getId(); // redirect giữ lại dữ liệu
	}

	@RequestMapping("delete/{id}")
	public String delete(Model model, RedirectAttributes params, @PathVariable("id") String id, HttpSession session) {
		String currentUserId = (String) session.getAttribute("currentUserId"); // Lấy tên đăng nhập của người dùng hiện tại

		// Kiểm tra nếu người dùng đang cố gắng xóa tài khoản của chính mình
		if (id.equals(currentUserId)) {
			params.addAttribute("message", "Bạn không thể xóa tài khoản của chính mình");
			return "redirect:/admin/customer/index";
		}

		// Tìm kiếm người dùng theo ID
		Customer customer = customerService.findById(id);
		if (customer == null) {
			params.addAttribute("message", "Tài khoản không tồn tại");
			return "redirect:/admin/customer/index";
		}

		// Kiểm tra nếu người dùng có đơn hàng liên quan
		List<Order> list = orderDAO.findByUser(customer);
		if (!list.isEmpty()) {
			params.addAttribute("message", "Tài khoản này có đơn hàng liên quan và không thể xóa");
			return "redirect:/admin/customer/index";
		}
		customerService.delete(id);
		params.addAttribute("message", "Xóa thành công");
		return "redirect:/admin/customer/index";
	}
}
