package com.xceptance.posters.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomerRepository;
import com.xceptance.posters.service.SessionService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CheckoutControllerUiTest 
{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CatalogCustomerRepository customerRepository;

    @Autowired
    private SessionService sessionService;

    @Test
    public void testShippingAddressUiRendersWithoutCrashing() throws Exception 
    {
        // Setup simple customer
        final CatalogCustomer customer = new CatalogCustomer();
        customer.setEmail("tdd-shipping@example.com");
        customer.setFirstName("TddFirst");
        customer.setLastName("TddLast");
        customer.hashPassword("testpass");
        customerRepository.save(customer);

        // Setup session
        final MockHttpSession session = new MockHttpSession();
        sessionService.setCustomerId(session, customer.getId());

        // We expect the shipping address page to render successfully (200 OK)
        // If the template throws a SpEL exception it will either yield 500 or redirect (3xx)
        mockMvc.perform(get("/en-US/checkout/shippingAddress").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("checkout/shippingAddress"))
               .andExpect(model().attributeExists("customer"));
    }
    @Test
    public void testBillingAddressUiRendersWithoutCrashing() throws Exception 
    {
        final CatalogCustomer customer = new CatalogCustomer();
        customer.setEmail("tdd-billing@example.com");
        customer.setFirstName("TddFirst");
        customer.setLastName("TddLast");
        customer.hashPassword("testpass");
        customerRepository.save(customer);

        final MockHttpSession session = new MockHttpSession();
        sessionService.setCustomerId(session, customer.getId());

        mockMvc.perform(get("/en-US/checkout/billingAddress").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("checkout/billingAddress"));
    }

    @Test
    public void testPaymentUiRendersWithoutCrashing() throws Exception 
    {
        final CatalogCustomer customer = new CatalogCustomer();
        customer.setEmail("tdd-payment@example.com");
        customer.setFirstName("TddFirst");
        customer.setLastName("TddLast");
        customer.hashPassword("testpass");
        customerRepository.save(customer);

        final MockHttpSession session = new MockHttpSession();
        sessionService.setCustomerId(session, customer.getId());

        mockMvc.perform(get("/en-US/checkout/payment").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("checkout/payment"));
    }

    @Test
    public void testOrderConfirmationUiRendersCorrectOrderNumber() throws Exception 
    {
        final CatalogCustomer customer = new CatalogCustomer();
        customer.setEmail("tdd-confirmation@example.com");
        customer.setFirstName("TddFirst");
        customer.setLastName("TddLast");
        customer.hashPassword("testpass");
        customerRepository.save(customer);

        final com.xceptance.posters.entity.CatalogOrder order = new com.xceptance.posters.entity.CatalogOrder();
        order.setOrderNumber("ORD-TEST1234");
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setCurrency("USD");
        order.setOrderState("created");
        order.setPaymentState("authorized");
        order.setTotal(new java.math.BigDecimal("100.00"));

        final com.xceptance.posters.entity.OrderLineItem item = new com.xceptance.posters.entity.OrderLineItem();
        item.setSku("MYPOSTER-1");
        item.setProductName("Test Poster Name");
        item.setQuantity(2);
        item.setUnitPrice(new java.math.BigDecimal("50.00"));
        item.setTotalPrice(new java.math.BigDecimal("100.00"));
        order.addLineItem(item);

        org.springframework.beans.factory.BeanFactory beanFactory = org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(mockMvc.getDispatcherServlet().getServletContext());
        com.xceptance.posters.entity.CatalogOrderRepository orderRepo = beanFactory.getBean(com.xceptance.posters.entity.CatalogOrderRepository.class);
        orderRepo.save(order);

        final MockHttpSession session = new MockHttpSession();
        sessionService.setCustomerId(session, customer.getId());
        sessionService.setOrderId(session, order.getId());

        mockMvc.perform(get("/en-US/checkout/orderConfirmation").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("checkout/orderConfirmation"))
               .andExpect(content().string(containsString("ORD-TEST1234")))    // Should render the explicit order number (currently it renders UUID)
               .andExpect(content().string(containsString("Test Poster Name"))); // Should render the product name (currently it renders SKU only)
    }
}
