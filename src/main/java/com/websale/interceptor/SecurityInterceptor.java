package com.websale.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.websale.entity.Category;
import com.websale.entity.Customer;
import com.websale.service.ICategoryService;
import com.websale.service.IHttpService;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
	@Autowired
	private IHttpService http;

	@Autowired
	private ICategoryService cateService;

	@SuppressWarnings("deprecation")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// lấy ds cates hiển thị tại Danh mục hàng hóa
		List<Category> list = cateService.findAll();
		request.setAttribute("cates", list);
		
		Customer user = http.getSession("user");
		String url = request.getRequestURI(); // lưu lại url user muốn truy cập

		if (user == null) {
			http.setSession("security-uri", url); // đưa vào attribute
			response.sendRedirect("/account/login?message="
					+ java.net.URLEncoder.encode("Bạn cần phải đăng nhập trước khi sử dụng chức năng!", "UTF-8"));
			return false;
		} else {
			if (!user.isAdmin()) {
				if (url.contains("/admin/")) {
					response.sendRedirect("/account/login?message="
							+ java.net.URLEncoder.encode("Bạn không có quyền vào trang admin!", "UTF-8"));
					return false;
				}
			}
			return true;
		}

	}

}
