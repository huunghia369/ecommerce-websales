package com.websale.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "\"Orders\"")
@DynamicUpdate
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"Id\"")
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss")
	@Column(name = "\"OrderDate\"")
	private Date orderDate = new Date();

	@NotEmpty(message = "Vui lòng nhập địa chỉ")
	@Column(name = "\"Address\"")
	private String address;

	@Column(name = "\"Amount\"")
	private Double amount;

	@Column(name = "\"Description\"")
	private String description;

	@Column(name = "\"Status\"")
	private Integer status;

	@ManyToOne
	@JoinColumn(name = "\"CustomerId\"")
	private Customer customer;

	@OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	private List<OrderDetail> orderDetails;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public Order() {

	}
}

