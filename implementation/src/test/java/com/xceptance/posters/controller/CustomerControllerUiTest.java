package com.xceptance.posters.controller;
import com.xceptance.posters.repository.CatalogOrderRepository;
import com.xceptance.posters.repository.CustomerRepository;
import com.xceptance.posters.entity.OrderCustomer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.repository.CustomerRepository;
import com.xceptance.posters.entity.CatalogOrder;
import com.xceptance.posters.repository.CatalogOrderRepository;
import com.xceptance.posters.entity.OrderLineItem;
import com.xceptance.posters.service.SessionService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerUiTest 
{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CatalogOrderRepository orderRepository;

    @Autowired
    private SessionService sessionService;

    @Test
    public void testAccountOverviewUiRendersWithoutCrashing() throws Exception 
    {
        // Setup simple customer
        final Customer customer = new Customer();
        customer.setEmail("tdd-account@example.com");
        customer.setFirstName("AccFirst");
        customer.setLastName("AccLast");
        customer.hashPassword("testpass");
        customerRepository.save(customer);

        final MockHttpSession session = new MockHttpSession();
        sessionService.setCustomerId(session, customer.getId());

        mockMvc.perform(get("/en-US/accountOverview").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("customer/accountOverview"))
               .andExpect(model().attributeExists("customer"));
    }

    @Test
    public void testOrderOverviewUiRendersWithOrdersWithoutCrashing() throws Exception 
    {
        // Setup simple customer
        final Customer customer = new Customer();
        customer.setEmail("tdd-order-history@example.com");
        customer.setFirstName("HistFirst");
        customer.setLastName("HistLast");
        customer.hashPassword("testpass");
        customerRepository.save(customer);

        // Create a mock order linked to the customer to verify it populates the history view
        final CatalogOrder order = new CatalogOrder();
        order.setOrderNumber("ORD-TEST1234");
        order.setOrderDate(LocalDateTime.now());
        order.setCurrency("USD");
        order.setOrderState("created");
        order.setPaymentState("authorized");
        order.setTotal(new BigDecimal("100.00"));
        
        final OrderLineItem item = new OrderLineItem();
        item.setSku("MYPOSTER-1");
        item.setProductName("Test Poster");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        item.setTotalPrice(new BigDecimal("100.00"));
        item.setImageUrl("https://legacy-cache/history-img.webp");
        item.setVariantDescription("32x24, Wood");
        order.addLineItem(item);

        final com.xceptance.posters.entity.OrderCustomer orderCustomer = new com.xceptance.posters.entity.OrderCustomer();
        orderCustomer.setEmail(customer.getEmail());
        orderCustomer.setFirstName(customer.getFirstName());
        orderCustomer.setLastName(customer.getLastName());
        order.setCustomer(orderCustomer);
        
        orderRepository.save(order);

        final MockHttpSession session = new MockHttpSession();
        sessionService.setCustomerId(session, customer.getId());

        // Verify that the retrieved orders are properly populated into the model using the OrderDto architecture and rendered safely
        mockMvc.perform(get("/en-US/orderOverview").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("customer/orderOverview"))
               .andExpect(model().attribute("orderDtos", hasSize(1)))
               .andExpect(content().string(containsString("https://legacy-cache/history-img.webp")))
               .andExpect(content().string(containsString("32x24, Wood")));
    }
}
