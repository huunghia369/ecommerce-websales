package com.websale.controller.admin;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import com.websale.dao.IOrderDetailDAO;
import com.websale.entity.OrderDetail;
import com.websale.service.IAccountService;
import com.websale.service.IOrderSevice;
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

import com.websale.entity.Product;
import com.websale.service.IHttpService;
import com.websale.service.IProductService;

@Controller
@RequestMapping("/admin/product/")
public class ProductsAdminController {
	@Autowired
	private IProductService productService;

	@Autowired
	private IAccountService customerService;

	@Autowired
	private IOrderSevice orderService;

	@Autowired
	private IOrderDetailDAO orderDetailDAO;

	@Autowired
	private IHttpService http;

	@RequestMapping("index")
	public String index(Model model) {
		model.addAttribute("product", new Product());
		List<Product> productList = productService.findAll();
		for (Product product : productList) {
			int quantityInTransit = orderService.getQuantityInTransit(product.getId());
			product.setAvailableQuantity(product.getQuantity() - quantityInTransit);
			int quantitySold = orderService.getQuantitySold(product.getId());
			product.setSoldQuantity(quantitySold);
		}
		productList.sort(Comparator.comparing(Product::getId).reversed());
		model.addAttribute("list", productList);
		return "admin/product/index";
	}

	@RequestMapping("edit/{id}")
	public String edit(Model model, @PathVariable("id") Integer id) {
		model.addAttribute("product", productService.findById(id));
		List<Product> productList = productService.findAll();
		for (Product product : productList) {
			int quantityInTransit = orderService.getQuantityInTransit(product.getId());
			product.setAvailableQuantity(product.getQuantity() - quantityInTransit);
			int quantitySold = orderService.getQuantitySold(product.getId());
			product.setSoldQuantity(quantitySold);
		}
		productList.sort(Comparator.comparing(Product::getId).reversed());
		model.addAttribute("list", productList);
		return "admin/product/index";
	}

	@RequestMapping("create")
	public String create(Model model, RedirectAttributes params, @Validated Product form, BindingResult errors,
			@RequestParam("image_file") MultipartFile file) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Vui lòng không bỏ trống các trường bên trên");
			return "admin/product/index";
		}
		File image = http.saveProductPhoto(file);
		if (image != null) {
			form.setImage(image.getName());
		}
		// thêm xuống db
		productService.add(form);
		params.addAttribute("message", "Thêm mới thành công");
		return "redirect:/admin/product/index"; // redirect lại để mất dữ liệu trên form
	}

	@RequestMapping("update")
	public String update(Model model, RedirectAttributes params, @Validated Product form, BindingResult errors,
			@RequestParam("image_file") MultipartFile file) {
		if (errors.hasErrors()) {
			model.addAttribute("message", "Vui lòng không bỏ trống bên dưới");
			 model.addAttribute("users", customerService.findByRoles(false));
			 model.addAttribute("admins", customerService.findByRoles(true));
			return "admin/product/index";
		}

		File photo = http.saveProductPhoto(file);
		if (photo != null) {
			form.setImage(photo.getName());
		}
		productService.update(form);
		params.addAttribute("message", "Cập nhật thành công");
		return "redirect:/admin/product/edit/" + form.getId(); // redirect giữ lại dữ liệu
	}

	@RequestMapping("delete/{id}")
	public String delete(Model model, RedirectAttributes params, @PathVariable("id") Integer id) {
		Product product = productService.findById(id);
		List<OrderDetail> list = orderDetailDAO.findByPro(product);
		if (!list.isEmpty()) {
			params.addAttribute("message", "Sản phẩm này có đơn hàng liên quan hoặc đang được bán");
			return "redirect:/admin/product/index";
		}
		productService.delete(id);
		params.addAttribute("message", "Xóa thành công");
		return "redirect:/admin/product/index";
	}
}
