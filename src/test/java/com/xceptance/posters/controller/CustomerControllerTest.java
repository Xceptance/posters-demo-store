package com.xceptance.posters.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;

import com.xceptance.posters.dto.OrderDto;
import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomerRepository;
import com.xceptance.posters.entity.CatalogOrder;
import com.xceptance.posters.entity.CatalogOrderRepository;
import com.xceptance.posters.service.CheckoutService;
import com.xceptance.posters.service.SessionService;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CatalogCustomerRepository customerRepository;

    @Mock
    private CatalogOrderRepository orderRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void testOrderOverviewInjectsOrderDtos() {
        // Arrange
        final MockHttpSession session = new MockHttpSession();
        final ConcurrentModel model = new ConcurrentModel();
        final UUID customerId = UUID.randomUUID();
        final CatalogCustomer customer = new CatalogCustomer();
        customer.setEmail("test@example.com");

        final CatalogOrder order = new CatalogOrder();
        order.setOrderDate(LocalDateTime.now());
        
        final OrderDto mockDto = new OrderDto("ORD-1", LocalDateTime.now(), null, null, null, null, null, null, null, null, List.of());

        when(sessionService.isCustomerLoggedIn(session)).thenReturn(true);
        when(sessionService.getCustomerId(session)).thenReturn(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomer_EmailOrderByOrderDateDesc("test@example.com")).thenReturn(List.of(order));
        when(checkoutService.toOrderDto(any(CatalogOrder.class))).thenReturn(mockDto);

        // Act
        final String viewName = customerController.orderOverview("en_US", session, model);

        // Assert
        assertThat(viewName).isEqualTo("customer/orderOverview");
        assertThat(model.getAttribute("customer")).isEqualTo(customer);
        
        @SuppressWarnings("unchecked")
        final List<OrderDto> mappedDtos = (List<OrderDto>) model.getAttribute("orderDtos");
        
        assertThat(mappedDtos).isNotNull();
        assertThat(mappedDtos).hasSize(1);
        assertThat(mappedDtos.get(0).orderNumber()).isEqualTo("ORD-1");
    }
}
