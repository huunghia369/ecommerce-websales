package com.websale.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.websale.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.websale.dao.IProductDAO;
import com.websale.entity.Product;
import com.websale.service.ICartService;

@SessionScope 
@Service("cart")
public class CartService implements ICartService {

	@Autowired
	private IProductService productService;

	Map<Integer, Product> map = new HashMap<Integer, Product>();

	@Override
	public void addCart(Integer id) {
		Product product = productService.findById(id);
		if (product == null || !product.getAvailable() || product.getAvailableQuantity() <= 0) {
			// Xử lý khi sản phẩm không còn sẵn hoặc hết hàng
			throw new IllegalArgumentException("Sản phẩm không có sẵn hoặc hết hàng.");
		}
		Product p = map.get(id);
		if (p != null) {
			// Cập nhật số lượng và kiểm tra số lượng có sẵn
			int newQuantity = p.getQuantity() + 1;
			if (newQuantity > product.getAvailableQuantity()) {
				p.setQuantity(product.getAvailableQuantity()); // Đặt số lượng bằng số lượng có sẵn
				throw new IllegalArgumentException("Số lượng sản phẩm yêu cầu vượt quá số lượng có sẵn. Đã đặt số lượng tối đa có sẵn.");
			} else {
				p.setQuantity(newQuantity);
			}
		} else {
			p = productService.findById(id);
			if (p.getQuantity() <= 0) {
				p.setAvailable(false);
				throw new IllegalArgumentException("Sản phẩm không có sẵn.");
			}
			p.setQuantity(1);
			map.put(id, p);
		}
	}


	@Override
	public void removeCart(Integer id) {
		map.remove(id);
	}


	@Override
	public void updateCart(Integer id, Integer qty) {
		Product product = productService.findById(id);
		Product p = map.get(id);
		if (p != null) {
			if (qty > product.getAvailableQuantity()) {
				p.setQuantity(product.getAvailableQuantity()); // Đặt số lượng bằng số lượng có sẵn
				throw new IllegalArgumentException("Số lượng sản phẩm yêu cầu vượt quá số lượng có sẵn. Đã đặt số lượng tối đa có sẵn.");
			} else {
				p.setQuantity(qty);
			}
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public int getCountCart() {
		Collection<Product> product = this.getItemsCart();
		int count = 0;
		for (Product p : product) {
			count += p.getQuantity();
		}
		return count;
	}

	@Override
	public double getAmountCart() {
		Collection<Product> ps = this.getItemsCart();
		double amount = 0;
		for (Product product : ps) {
			amount += (product.getQuantity() * (product.getUnitPrice() - (product.getUnitPrice() * product.getDiscount())));
		}
		return amount;
	}


	@Override
	public Collection<Product> getItemsCart() {
		return map.values();
	}

}
