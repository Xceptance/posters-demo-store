import static org.junit.Assert.*;

import models.Customer;
import models.Order;
import models.Product;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;


public class OrderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void orderByCustomer() {
		for(Customer customer : Ebean.find(Customer.class).findList()) {

			for(Order order : customer.getOrder()) {
				System.out.println("hier");
				for(Product product : order.getBasket().getProductIds()) {
					System.out.println(customer.getEmail() + ": " + product.getName());
				}
			}
		}
	}
	
	public void orderProducts() {
		
	}

}
