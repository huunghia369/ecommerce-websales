package com.websale.service.impl;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.websale.dao.IOrderDAO;
import com.websale.dao.IOrderDetailDAO;
import com.websale.entity.Customer;
import com.websale.entity.Order;
import com.websale.entity.OrderDetail;
import com.websale.entity.Product;
import com.websale.service.IOrderSevice;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class OrderService extends GeneralService<Order, Integer> implements IOrderSevice {

	@Autowired
	private CartService cart;

	@Autowired
	private HttpService http;

	@Autowired
	private IOrderDAO orderDAO;

	@Autowired
	private IOrderDetailDAO detailDAO;

	@Autowired
	private SessionFactory sessionFactory;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Order createOrder() {
		Customer customer = http.getSession("user");
		Order order = new Order();
		order.setOrderDate(new Date());
		order.setAmount(cart.getAmountCart());
		order.setCustomer(customer);
		return order;
	}

	@Override
	public void addOrderAndOrderDetail(Order o, CartService cart) {
		o.setStatus(1);
		orderDAO.create(o);
		Collection<Product> items = cart.getItemsCart();
		items.forEach(p -> {
			OrderDetail detail = new OrderDetail();
			detail.setOrder(o);
			detail.setProduct(p);
			detail.setUnitPrice(p.getUnitPrice());
			detail.setDiscount(p.getDiscount());
			detail.setQuantity(p.getQuantity());
			detailDAO.create(detail);
		});
	}

	@Override
	public List<Order> getAllOrderByUser() {
		Customer user = http.getSession("user");
		return orderDAO.findByUser(user);
	}

	@Override
	public Order findById(Integer id) {
		return orderDAO.findById(id);
	}

	@Override
	public Map<Integer, Product> getPurchasedItems() {
		Customer user = http.getSession("user");
		List<Order> list = orderDAO.findByUser(user);
		Map<Integer, Product> prods = new HashMap<Integer, Product>();
		list.forEach(order -> {
			order.getOrderDetails().forEach(details -> {
				Product p = details.getProduct();
				prods.put(p.getId(), p);
			});

		});
		return prods;
	}

	@Transactional
	public void updateOrder(Order form) {
		Session session = sessionFactory.getCurrentSession();

		Order existingOrder = session.get(Order.class, form.getId());
		if (existingOrder == null) {
			throw new IllegalArgumentException("Order not found");
		}

		// Chỉ cập nhật các trường cần thiết, không cập nhật CustomerId
		existingOrder.setAddress(form.getAddress());
		existingOrder.setAmount(form.getAmount());
		existingOrder.setDescription(form.getDescription());
		existingOrder.setOrderDate(form.getOrderDate());
		existingOrder.setStatus(form.getStatus());

		// Cập nhật Order
		session.update(existingOrder);
	}

	@Transactional
	public byte[] createInvoicePdf(Order order) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(outputStream);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);

		// Tạo font hỗ trợ Unicode
		PdfFont font = PdfFontFactory.createFont("src/main/webapp/font/dejavu-fonts-ttf-2.37/ttf/DejaVuSans.ttf", PdfEncodings.IDENTITY_H);

		// Thêm logo
		ImageData logo = ImageDataFactory.create("src/main/webapp/static/images/stamp.png");
		Image logoImage = new Image(logo).scaleToFit(120, 60);
		document.add(logoImage.setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));

		// Tiêu đề hóa đơn
		document.add(new Paragraph("HÓA ĐƠN").setFont(font).setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));

		// Thông tin đơn hàng
		document.add(new Paragraph("Mã đơn hàng: " + order.getId()).setFont(font).setMarginBottom(5));
		document.add(new Paragraph("Ngày đặt hàng: " + order.getOrderDate()).setFont(font).setMarginBottom(5));
		document.add(new Paragraph("Tên khách hàng: " + order.getCustomer().getFullname()).setFont(font).setMarginBottom(5));
		document.add(new Paragraph("Địa chỉ: " + order.getAddress()).setFont(font).setMarginBottom(5));
		document.add(new Paragraph("Ghi chú: " + (order.getDescription() != null ? order.getDescription() : "Không có ghi chú")).setFont(font).setMarginBottom(10));

		// Thêm bảng chi tiết đơn hàng
		Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 3})).useAllAvailableWidth();
		table.addHeaderCell(new Cell().add(new Paragraph("Sản phẩm").setFont(font).setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setFont(font).setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Số lượng").setFont(font).setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Giảm giá").setFont(font).setBold()));
		table.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setFont(font).setBold()));

		for (OrderDetail detail : order.getOrderDetails()) {
			table.addCell(new Cell().add(new Paragraph(detail.getProduct().getName()).setFont(font)));
			table.addCell(new Cell().add(new Paragraph(String.format("%,.2f đ", detail.getUnitPrice())).setFont(font)));
			table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getQuantity())).setFont(font)));
			table.addCell(new Cell().add(new Paragraph(String.format("%,.2f%%", detail.getDiscount() * 100)).setFont(font)));
			table.addCell(new Cell().add(new Paragraph(String.format("%,.2f đ", detail.getUnitPrice() * detail.getQuantity() * (1 - detail.getDiscount()))).setFont(font)));
		}

		document.add(table.setMarginBottom(10));

		// Thêm chữ ký
		ImageData signature = ImageDataFactory.create("src/main/webapp/static/images/signature.png");
		Image signatureImage = new Image(signature).scaleToFit(100, 50);
		document.add(new Paragraph("Chữ ký").setFont(font).setBold().setMarginTop(10));
		document.add(signatureImage.setTextAlignment(TextAlignment.RIGHT).setMarginBottom(10));

		// Thông tin liên hệ và công ty
		document.add(new Paragraph("Công ty: PTIT HCM.").setFont(font).setMarginBottom(5));
		document.add(new Paragraph("Địa chỉ công ty: 123 Đường ABC, Thành phố XYZ").setFont(font).setMarginBottom(5));
		document.add(new Paragraph("Điện thoại: 0987475408").setFont(font).setMarginBottom(5));
		document.add(new Paragraph("Email: clonenghia03@gmail.com").setFont(font).setMarginBottom(5));

		document.close();

		return outputStream.toByteArray();
	}

	private Map<Integer, Timestamp> completedTimestampMap = new ConcurrentHashMap<>();

	public void setCompletedTimestamp(Integer orderId, Timestamp completedTimestamp) {
		completedTimestampMap.put(orderId, completedTimestamp);
	}

	public Timestamp getCompletedTimestamp(Integer orderId) {
		return completedTimestampMap.get(orderId);
	}

	public Map<Integer, Timestamp> getCompletedTimestamps() {
		return new HashMap<>(completedTimestampMap);
	}

	@Override
	public int getQuantityInTransit(Integer productId) {
		return orderDAO.getQuantityInTransit(productId);
	}

	@Override
	public int getQuantitySold(Integer productId){
		return orderDAO.getQuantitySold(productId);
	}
}
