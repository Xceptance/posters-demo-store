package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "basket")
public class Basket {

	/**
	 * The id of the basket.
	 */
	@Id
	private int id;

	/**
	 * The customer of the basket.
	 */
	private Customer customer;

	/**
	 * The products, which are in the basket.
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Product> productIds;

	/**
	 * product id : count of product in basket
	 */
	// @ManyToMany (cascade = CascadeType.ALL)
	// @JoinColumn(name="product_id")
	// private Map<Product, Double> products;

	/**
	 * The total price of the basket.
	 */
	private double totalPrice;

	/**
	 * The order of the basket.
	 */
	@OneToOne
	private Order order;

	public Basket() {
		// this.products = new HashMap<Product, Double>();
		this.productIds = new ArrayList<Product>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Product> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<Product> products) {
		this.productIds = products;
	}

	public double getTotalPrice() {
		calculateTotalPrice();
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void calculateTotalPrice() {
		this.totalPrice = 0;

		for (Product product : productIds) {
			// add for each product the price to the total price
			this.totalPrice += product.getPrice();
		}
	}

	public void addProduct(Product productId) {
		productIds.add(productId);
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	// public Map<Product, Double> getProducts() {
	// return products;
	// }
	//
	// public void setProducts(Map<Product, Double> products) {
	// this.products = products;
	// }
	//
	// public void addProducts (Product product) {
	// if(this.products.containsKey(product)) {
	// this.products.put(product, this.products.get(product) + 1);
	// }
	// else {
	// this.products.put(product, 1.0);
	// }
	// }
}
