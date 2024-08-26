package com.websale.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.*;

import com.websale.entity.Category;
import com.websale.entity.OrderDetail;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "\"Products\"")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"Id\"")
	private Integer id;

	@Column(name = "\"Name\"")
	private String name;

	@Column(name = "\"Image\"")
	private String image = "product.png";

	@Column(name = "\"UnitPrice\"")
	private Double unitPrice;

	@Column(name = "\"Discount\"")
	private Double discount = 0.0;

	@Column(name = "\"Quantity\"")
	private Integer quantity = 1;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name = "\"ProductDate\"")
	private Date productDate = new Date();

	@Column(name = "\"Available\"")
	private Boolean available = true;

	@Column(name = "\"Description\"")
	private String description;

	@Column(name = "\"ViewCount\"")
	private Integer viewCount = 0;

	@ManyToOne
	@JoinColumn(name = "\"CategoryId\"")
	private Category category;

	@OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
	private List<OrderDetail> orderDetails;

	@Transient
	private Integer availableQuantity;

	@Transient
	private Integer soldQuantity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Date getProductDate() {
		return productDate;
	}

	public void setProductDate(Date productDate) {
		this.productDate = productDate;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public Integer getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public Integer getSoldQuantity() {
		return soldQuantity;
	}

	public void setSoldQuantity(Integer soldQuantity) {
		this.soldQuantity = soldQuantity;
	}

	public Product() {

	}

	public Product(Integer id) {
		super();
		this.id = id;
	}
}
