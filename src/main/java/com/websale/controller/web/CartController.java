package com.websale.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.websale.service.ICartService;

@Controller
public class CartController {

	@Autowired
	ICartService cart;

	@RequestMapping("/header/cart")
	public String info(Model model) {
		model.addAttribute("cart", cart);
		return "non-layout/layout/_header-cart";
	}

	@RequestMapping("/cart/view")
	public String cartView(Model model) {
		model.addAttribute("cart", cart);
		return "cart/view";
	}

	@ResponseBody
	@RequestMapping("/product/add-to-cart/{id}")
	public String addCart(@PathVariable("id") Integer id) {
		try {
			cart.addCart(id);
			return getInfo();
		} catch (IllegalArgumentException e) {
			// Xử lý lỗi khi sản phẩm không có sẵn hoặc không đủ số lượng
			return String.format("{\"error\": \"%s\"}", e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping("/cart/remove/{id}")
	public String removeCart(@PathVariable("id") Integer id) {
		cart.removeCart(id);
		return getInfo();
	}
	
	@ResponseBody
	@RequestMapping("/cart/clear")
	public String clearCart() {
		cart.clear();
		return getInfo();
	}
	
	@ResponseBody
	@RequestMapping("/cart/update/{id}/{qty}")
	public String changeQuantity(@PathVariable("id") Integer id , @PathVariable("qty") Integer qty) {
		try {
			cart.updateCart(id, qty);
			return getInfo();
		} catch (IllegalArgumentException e) {
			// Xử lý lỗi khi sản phẩm không có sẵn hoặc không đủ số lượng
			return String.format("{\"error\": \"%s\"}", e.getMessage());
		}
	}

	private String getInfo() {
		return String.format("{\"count\":%d,\"amount\":%.2f}", cart.getCountCart(), cart.getAmountCart());
	}

}
