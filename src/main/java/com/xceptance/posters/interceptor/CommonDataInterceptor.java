package com.xceptance.posters.interceptor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.model.Cart;
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

        // Locale / path info
        String requestPath = request.getRequestURI();
        mav.addObject("currPath", requestPath);
        // Extract locale from path (first segment after /)
        String[] segments = requestPath.split("/");
        String locale = segments.length > 1 ? segments[1] : "en-US";
        mav.addObject("urlLocale", locale);
        String staticPath = requestPath.replaceFirst("/" + locale, "");
        mav.addObject("staticPath", staticPath);
        var langArray = props.getLanguageArray();
        mav.addObject("supportedLanguages", Arrays.asList(langArray));

        // Build maps for the language switcher
        Map<String, String> languageNames = new LinkedHashMap<>();
        Map<String, String> languageFlags = new LinkedHashMap<>();
        for (String lang : langArray)
        {
            Locale loc = Locale.forLanguageTag(lang);
            languageNames.put(lang, loc.getDisplayLanguage(loc)
                + " (" + loc.getDisplayCountry(loc) + ")");
            languageFlags.put(lang, toFlagEmoji(loc.getCountry()));
        }
        mav.addObject("languageNames", languageNames);
        mav.addObject("languageFlags", languageFlags);
        Locale currentLocale = Locale.forLanguageTag(locale);
        mav.addObject("currentLanguageName",
            currentLocale.getDisplayLanguage(currentLocale));
        mav.addObject("currentLanguageFlag",
            toFlagEmoji(currentLocale.getCountry()));

        // Locale-dependent config values
        String currency;
        String unitLength;
        if (locale.startsWith("de"))
        {
            currency = "€";
            unitLength = "cm";
        }
        else if (locale.equals("en-GB"))
        {
            currency = "£";
            unitLength = "cm";
        }
        else if (locale.equals("sv-SE"))
        {
            currency = "kr";
            unitLength = "cm";
        }
        else
        {
            currency = props.getCurrency();
            unitLength = props.getUnitOfLength();
        }
        mav.addObject("currency", currency);
        mav.addObject("currentVersion", props.getVersion());
        mav.addObject("unitLength", unitLength);
        mav.addObject("regexEmail", props.getRegex().getEmail());
        mav.addObject("regexName", props.getRegex().getName());
        mav.addObject("regexCreditCard", props.getRegex().getCreditCard());
        mav.addObject("regexZip", props.getRegex().getZip());
        mav.addObject("regexProductCount", props.getRegex().getProductCount());
    }

    /**
     * Convert a 2-letter ISO country code (e.g. "US") into a flag emoji (e.g. 🇺🇸)
     * using Unicode Regional Indicator Symbols.
     */
    private String toFlagEmoji(String countryCode)
    {
        if (countryCode == null || countryCode.length() != 2)
        {
            return "";
        }
        int firstChar = Character.codePointAt(countryCode.toUpperCase(), 0) - 0x41 + 0x1F1E6;
        int secondChar = Character.codePointAt(countryCode.toUpperCase(), 1) - 0x41 + 0x1F1E6;
        return new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
    }
}
