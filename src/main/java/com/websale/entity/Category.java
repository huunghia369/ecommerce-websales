package com.websale.entity;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "\"Categories\"")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"Id\"")
	Integer id;
	@Column(name = "\"Name\"")
	String name;
	@NotBlank(message = "Không được bỏ trống tên loại sản phẩm Tiếng Việt")
	@Column(name = "\"NameVN\"")
	String nameVN;


	@OneToMany(mappedBy = "category", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<Product> products;

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

	public String getNameVN() {
		return nameVN;
	}

	public void setNameVN(String nameVN) {
		this.nameVN = nameVN;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

}
