package com.websale.controller.web;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.websale.dao.ICustomerDAO;
import com.websale.entity.Customer;
import com.websale.service.IAccountService;
import com.websale.service.ICookieService;
import com.websale.service.IHttpService;
import com.websale.service.IMailService;

import javax.servlet.http.HttpSession;

@Controller
public class AccountController {

	@Autowired
	private IAccountService accountService;

	@Autowired
	private ICustomerDAO customerDAO;

	@Autowired
	private IHttpService http;
	@Autowired
	private ICookieService cookieService;

	@Autowired
	private IMailService mailerService;

	@GetMapping("/account/login")
	public String login(Model model) {
		String[] userInfo = cookieService.getCookieValue("user", " , ").split(",");
		model.addAttribute("username", userInfo[0].trim());
		model.addAttribute("password", userInfo[1].trim());
		return "account/login";
	}

	@PostMapping("/account/login") // lúc ấn submit
	public String login(Model model, @RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam(name = "remember", defaultValue = "false") boolean remember, HttpSession session) {
		if ("".equals(username.trim())) {
			model.addAttribute("message", "Tên đăng nhập không được để trống");
			return "account/login";
		} else if ("".equals(password.trim())) {
			model.addAttribute("message", "Mật khẩu không được để trống");
			return "account/login";
		}
		Customer user = accountService.findById(username);
		if (user == null || !password.equals(user.getPassword())) {
			model.addAttribute("message", "Sai tên đăng nhập hoặc mật khẩu");
		} else if (!user.isActivated()) {
			model.addAttribute("message", "Vui lòng vào mail kích hoạt tài khoản");
		} else {
			http.setSession("user", user);
			session.setAttribute("currentUserId", user.getId());
			model.addAttribute("message", "Đăng nhập thành công");
			// Ghi nhận số lượng truy cập của khách hàng
			accountService.logAccess(user.getId());
			// Đánh dấu khách hàng đã đăng nhập
			accountService.userLoggedIn(user.getId());
			cookieService.createCookie("user", username + "," + password, remember ? 15 : 0);
			String securityUri = http.getSession("security-uri");
			if (securityUri != null)
			{
				return "redirect:" + securityUri;
			} else
				return "redirect:/home/index";
		}
		return "account/login";
	}

	@RequestMapping("/account/logoff")
	public String logoff(Model model, HttpSession session) {
		String customerId = (String) session.getAttribute("currentUserId");
		if (customerId != null) {
			// Đánh dấu khách hàng đã đăng xuất
			accountService.userLoggedOut(customerId);
		}
		http.removeSession("user");
		return "redirect:/home/index";
	}

	@GetMapping("/account/forgot")
	public String forgot() {
		return "account/forgot";

	}

	@PostMapping("/account/forgot")
	public String forgot(Model model, @RequestParam("username") String username, @RequestParam("email") String email,
			RedirectAttributes redirectAttributes) {
		Customer user = accountService.findById(username);
		if (user == null) {
			model.addAttribute("message", "Sai tên đăng nhập");
		} else if (!user.getEmail().equals(email)) {
			model.addAttribute("message", "Sai tên email đã đăng kí");
		} else {
			mailerService.send(email, "Forgot Password", user.getPassword());
			redirectAttributes.addFlashAttribute("message", "Mật khẩu đã được gửi qua email");
			return "redirect:/account/login";
		}
		return "account/forgot";

	}

	@GetMapping("/account/register")
	public String register(Model model) {
		Customer user = new Customer();
		model.addAttribute("user", user);
		return "account/register";

	}

	@PostMapping("/account/register")
	public String register(Model model, @RequestParam("photo_file") MultipartFile file,
			@RequestParam("confirm") String confirm, @Validated @ModelAttribute("user") Customer form,
			BindingResult errors, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Vui lòng không bỏ trống !");
		} else if (!confirm.equals(form.getPassword())) {
			model.addAttribute("message", "Xác nhận password không trùng khớp!");
		} else {
			Customer user = accountService.findById(form.getId());
			if (user != null) {
				model.addAttribute("message", "Tài khoản user đã được dùng!");
			} else if (!accountService.sendActivedUser(form)) {
				model.addAttribute("message", "Không thể gửi email kích hoạt!");
			} else {
				File photo = http.saveCustomerPhoto(file);
				if (photo != null) {
					form.setPhoto(photo.getName());
				} else {
					form.setPhoto("user.png");
				}
				accountService.add(form);
				redirectAttributes.addFlashAttribute("message", "Kiểm tra email và kích hoạt tài khoản!");
				return "redirect:/account/login";

			}
		}
		return "account/register";
	}

	@GetMapping("/account/activate/{id}")
	public String activate(Model model, @PathVariable("id") String id, RedirectAttributes redirectAttributes) {
		Customer user = accountService.findById(http.decode(id));
		user.setActivated(true);
		customerDAO.update(user);
		redirectAttributes.addFlashAttribute("message", "Tài khoản đã được kích hoạt");
		return "redirect:/account/login";
	}

	@GetMapping("/account/change")
	public String changForm() {
		return "account/change";
	}

	@PostMapping("/account/change")
	public String change(Model model, @RequestParam("username") String username,
			@RequestParam("password") String password, @RequestParam("newPassword") String newPassword,
			@RequestParam("confirm") String confirm, RedirectAttributes redirectAttributes) {

		if (!newPassword.equals(confirm)) {
			model.addAttribute("message", "Xác nhận mật khẩu không chính xác");
		} else {
			Customer user = accountService.findById(username);
			if (user == null) {
				model.addAttribute("message", "Sai tên đăng nhập");
			} else if (!password.equals(user.getPassword())) {
				model.addAttribute("message", "Sai mật khẩu");
			} else {
				user.setPassword(newPassword);
				accountService.updateUser(user);
				redirectAttributes.addFlashAttribute("message", "Thay đổi mật khẩu thành công");
				return "redirect:/account/login";
			}
		}
		return "account/change";
	}

	@GetMapping("/account/edit")
	public String editForm(Model model) {
		Customer user = http.getSession("user");
		model.addAttribute("user", user);
		return "account/edit";
	}

	@PostMapping("/account/edit")
	public String edit(Model model, @RequestParam("photo_file") MultipartFile file,
			@Validated @ModelAttribute("user") Customer form, BindingResult errors) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Vui lòng không bỏ trống bên dưới!");
		} else {
			// Kiểm tra email trùng lặp
			Customer existingCustomer = accountService.findByEmail(form.getEmail());
			if (existingCustomer != null && !existingCustomer.getId().equals(form.getId())) {
				model.addAttribute("message", "Email đã tồn tại. Vui lòng chọn email khác.");
				model.addAttribute("users", accountService.findByRoles(false));
				model.addAttribute("admins", accountService.findByRoles(true));
				return "admin/customer/index";
			}
			// Kiểm tra sđt trùng lặp
			Customer existingPhone = accountService.findByPhone(form.getPhone());
			if (existingPhone != null && !existingPhone.getPhone().equals(form.getPhone())) {
				model.addAttribute("message", "SĐT đã tồn tại. Vui lòng chọn sđt khác.");
				model.addAttribute("users", accountService.findByRoles(false));
				model.addAttribute("admins", accountService.findByRoles(true));
				return "admin/customer/index";
			}
			File photo = http.saveCustomerPhoto(file);
			if (photo != null) {
				form.setPhoto(photo.getName());
			}
			accountService.updateUser(form);
			http.setSession("user", form);
			model.addAttribute("message", "Đã cập nhật thông tin cá nhân!");
		}
		return "account/edit";
	}

}
