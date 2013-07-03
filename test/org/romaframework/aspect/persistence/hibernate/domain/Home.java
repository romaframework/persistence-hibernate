package org.romaframework.aspect.persistence.hibernate.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Home {

	@Id
	private long		id;
	private String	address;
	private Double	price;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
