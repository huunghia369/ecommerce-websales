package com.websale.entity;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "\"Customers\"")
public class Customer {
	@NotEmpty(message = "Vui lòng nhập tài khoản")
	@Id
	@Column(name = "\"Id\"")
	private String id;

	@NotEmpty(message = "Vui lòng nhập mật khẩu")
	@Column(name = "\"Password\"")
	private String password;

	@NotEmpty(message = "Vui lòng nhập tên khách hàng")
	@Column(name = "\"Fullname\"")
	private String fullname;

	@NotEmpty(message = "Vui lòng nhập email")
	@Email
	@Column(name = "\"Email\"")
	private String email;

	@Column(name = "\"Photo\"")
	private String photo = "user.png";

	@Column(name = "\"Activated\"")
	private boolean activated = false;

	@Column(name = "\"Admin\"")
	private boolean admin = false;

	@Column(name = "\"Phone\"")
	private String phone;

	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<Order> orders;

	@Transient
	private Double totalPurchases;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public Double getTotalPurchases() {
		return totalPurchases;
	}

	public void setTotalPurchases(Double totalPurchases) {
		this.totalPurchases = totalPurchases;
	}

	public Customer(@NotBlank String id) {
		super();
		this.id = id;
	}

	public Customer() {

	}

}
