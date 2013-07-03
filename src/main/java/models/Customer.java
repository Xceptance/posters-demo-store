package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity
@Table(name="customer")
public class Customer {

	@Id
	private int id;
	
	private String email;
	
	private String password;
	
	private String name;
	
	private String firstName;
	
	
	@OneToMany (cascade = CascadeType.ALL)
	private List<DeliveryAddress> deliveryAddress;
	
	@OneToMany (cascade = CascadeType.ALL)
	private List<CreditCard> creditCard;
	
	@OneToMany (cascade = CascadeType.ALL)
	private List<Order> order;
	
	
	public Customer () {
		this.deliveryAddress = new ArrayList<DeliveryAddress>();
		this.creditCard = new ArrayList<CreditCard>();
		this.order = new ArrayList<Order>();
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public List<DeliveryAddress> getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(List<DeliveryAddress> deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int customerId) {
		this.id = customerId;
	}

	public List<CreditCard> getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(List<CreditCard> creditCard) {
		this.creditCard = creditCard;
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}

	public void addCreditCard(CreditCard creditCard) {
		this.creditCard.add(creditCard);
	}
	
	public void deleteCreditCard(CreditCard creditCard) {
		this.creditCard.remove(creditCard);
//		int index = this.creditCard.indexOf(creditCard);
//		this.creditCard.remove(index);
	}
}
