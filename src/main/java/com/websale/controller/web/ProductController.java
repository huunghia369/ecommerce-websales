package com.websale.controller.web;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.websale.dao.IProductDAO;
import com.websale.entity.Category;
import com.websale.entity.Product;
import com.websale.service.ICategoryService;
import com.websale.service.ICookieService;
import com.websale.service.IHttpService;
import com.websale.service.IMailService;
import com.websale.service.IProductService;

@Controller
public class ProductController {

	@Autowired
	private ICategoryService service;
	@Autowired
	private IProductDAO dao;

	@Autowired
	private IProductService serviceProduct;

	@Autowired
	private ICookieService cookieService;

	@Autowired
	private IMailService mailService;

	@Autowired
	private IHttpService httpService;

	@Autowired
	private ResourceLoader resourceLoader;

	@RequestMapping("/product/list-by-category/{cId}")
	public String listByCategory(@PathVariable("cId") Integer id, Model model) {
		Category category = service.findById(id);
		List<Product> listProduct = category.getProducts();
		model.addAttribute("list",listProduct);
		return "product/list";
	}

	@RequestMapping("/product/list-by-keywords")
	public String listByKeyWords(@RequestParam("keywords") String keywords, Model model) {
		model.addAttribute("list", dao.findByKeywords(keywords));
		return "product/list";
	}

	@RequestMapping("/product/detail/{id}")
	public String detail(@PathVariable("id") Integer id, Model model) {    
		Product p = serviceProduct.findById(id);
		p.setViewCount(p.getViewCount() + 1); 
		dao.update(p);
		model.addAttribute("prod", p);
		List<Product> listDaXem = serviceProduct.getViewProduct("daXem", id.toString());
		model.addAttribute("daXem", listDaXem);
		String ids = cookieService.getCookieValue("like", "");
		if (!ids.isEmpty()) {
			List<Product> list = dao.findByIdsInCookie(ids);
			model.addAttribute("like", list);
		}
		return "product/detail";
	}

	@ResponseBody
	@RequestMapping("/product/favorite/{id}")
	public String[] favorite(@PathVariable("id") Integer id) {
		String ids = cookieService.getCookieValue("like", id.toString());
		if (!ids.contains(id.toString())) {
			ids += "," + id.toString();
		}
		cookieService.createCookie("like", ids, 15);
		return ids.split(",");
	}

	@RequestMapping("/product/list-by-hot/{key}")
	public String listByHot(@PathVariable("key") String key, Model model) 
	{
		List<Product> listP = serviceProduct.findByHot(key);
		model.addAttribute("list", listP);
		return "product/list";
	}

	@GetMapping("/product/search-by-price-range")
	public String searchByPriceRange(@RequestParam("minPrice") Double minPrice,
									 @RequestParam("maxPrice") Double maxPrice,
									 Model model) {
		List<Product> products = serviceProduct.findByPriceRange(minPrice, maxPrice);
		model.addAttribute("list", products);
		return "product/list";
	}

	@GetMapping("/product/search-by-predefined-price-range")
	public String searchByPredefinedPriceRange(@RequestParam("priceRange") String priceRange,
											   Model model) {
		String[] range = priceRange.split("-");
		Double minPrice = Double.parseDouble(range[0]);
		Double maxPrice = range.length > 1 && !range[1].isEmpty() ? Double.parseDouble(range[1]) : Double.MAX_VALUE;
		List<Product> products = serviceProduct.findByPriceRange(minPrice, maxPrice);
		model.addAttribute("list", products);
		return "product/list";
	}

	@ResponseBody
	@RequestMapping("/product/send-friend")
	public String sendFriend(@RequestParam("id") Integer id,
			@RequestParam("to") String to, @RequestParam("subject") String subject, @RequestParam("body") String body) {
		String url = httpService.getCurrentUrl().replace("send-friend", "detail/" + id);
		mailService.send(to, subject, body + "<hr/><a href='" + url + "'>Link sản phẩm. Bấm để xem chi tiết</a>");
		return "Đã gửi thông tin thành công";
	}
}
