import static org.junit.Assert.*;

import models.Customer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.database.CustomerInformation;


public class TestForTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		for(Customer cust : CustomerInformation.getAllCustomers()) {
			System.out.println("Customer: " + cust.getFirstName());
		}
	}

}
