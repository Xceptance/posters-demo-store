package com.xceptance.posters.interceptor;

import java.util.Arrays;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.model.Cart;
import com.xceptance.posters.model.Customer;
import com.xceptance.posters.repository.CustomerRepository;
import com.xceptance.posters.repository.TopCategoryRepository;
import com.xceptance.posters.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor that populates common model attributes needed by all storefront pages:
 * categories, cart info, login state, config values, locale info.
 * Replaces the old WebShopController.setCommonData() pattern.
 */
@Component
public class CommonDataInterceptor implements HandlerInterceptor
{
    private final TopCategoryRepository topCategoryRepository;
    private final CustomerRepository customerRepository;
    private final SessionService sessionService;
    private final PostersProperties props;

    public CommonDataInterceptor(TopCategoryRepository topCategoryRepository,
                                  CustomerRepository customerRepository,
                                  SessionService sessionService,
                                  PostersProperties props)
    {
        this.topCategoryRepository = topCategoryRepository;
        this.customerRepository = customerRepository;
        this.sessionService = sessionService;
        this.props = props;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView mav) throws Exception
    {
        if (mav == null || mav.getViewName() == null || mav.getViewName().startsWith("redirect:"))
        {
            return;
        }

        // Categories
        mav.addObject("topCategory", topCategoryRepository.findAll());

        // Cart info
        Cart cart = sessionService.getCart(request.getSession());
        cart.calculateTotalTaxPrice();
        cart.calculateTotalPrice();
        mav.addObject("cartProducts", cart.getProducts());
        mav.addObject("cartProductCount", cart.getProductCount());
        mav.addObject("cartId", cart.getId());
        mav.addObject("subTotalPrice", cart.getSubTotalPriceAsString());
        mav.addObject("subOrderTotalTax", cart.getTotalTaxPriceAsString());
        mav.addObject("totalPrice", cart.getTotalPriceAsString());

        // Customer login state
        if (sessionService.isCustomerLoggedIn(request.getSession()))
        {
            var customerId = sessionService.getCustomerId(request.getSession());
            var customer = customerRepository.findById(customerId).orElse(null);
            if (customer != null)
            {
                mav.addObject("isLogged", true);
                mav.addObject("customerFirstName", customer.getFirstName());
            }
            else
            {
                sessionService.removeCustomerId(request.getSession());
                mav.addObject("isLogged", false);
            }
        }
        else
        {
            mav.addObject("isLogged", false);
        }

        // Config values
        mav.addObject("currency", props.getCurrency());
        mav.addObject("currentVersion", props.getVersion());
        mav.addObject("unitLength", props.getUnitOfLength());
        mav.addObject("regexEmail", props.getRegex().getEmail());
        mav.addObject("regexName", props.getRegex().getName());
        mav.addObject("regexCreditCard", props.getRegex().getCreditCard());
        mav.addObject("regexZip", props.getRegex().getZip());
        mav.addObject("regexProductCount", props.getRegex().getProductCount());

        // Locale / path info
        String requestPath = request.getRequestURI();
        mav.addObject("currPath", requestPath);
        // Extract locale from path (first segment after /)
        String[] segments = requestPath.split("/");
        String locale = segments.length > 1 ? segments[1] : "en-US";
        mav.addObject("urlLocale", locale);
        String staticPath = requestPath.replaceFirst("/" + locale, "");
        mav.addObject("staticPath", staticPath);
        mav.addObject("supportedLanguages", Arrays.asList(props.getLanguageArray()));
    }
}
