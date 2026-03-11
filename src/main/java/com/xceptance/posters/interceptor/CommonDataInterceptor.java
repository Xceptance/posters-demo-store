package com.xceptance.posters.interceptor;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.CatalogService;
import com.xceptance.posters.entity.Category;
import com.xceptance.posters.entity.LocalizedTextService;
import com.xceptance.posters.entity.CatalogCart;
import com.xceptance.posters.entity.CatalogCustomerRepository;
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
    private final CatalogService catalogService;
    private final LocalizedTextService textService;
    private final CatalogCustomerRepository customerRepository;
    private final SessionService sessionService;
    private final PostersProperties props;

    public CommonDataInterceptor(CatalogService catalogService,
                                  LocalizedTextService textService,
                                  CatalogCustomerRepository customerRepository,
                                  SessionService sessionService,
                                  PostersProperties props)
    {
        this.catalogService = catalogService;
        this.textService = textService;
        this.customerRepository = customerRepository;
        this.sessionService = sessionService;
        this.props = props;
    }

    /**
     * Simple DTO to carry resolved category data to the template.
     */
    public record CategoryDto(int id, String name, List<SubCategoryDto> subCategories) {}
    public record SubCategoryDto(int id, String name) {}

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView mav) throws Exception
    {
        if (mav == null || mav.getViewName() == null || mav.getViewName().startsWith("redirect:"))
        {
            return;
        }

        // Extract locale early (needed for category names)
        String requestPath = request.getRequestURI();
        String[] segments = requestPath.split("/");
        String locale = segments.length > 1 ? segments[1] : "en-US";

        // Categories (new entity model)
        List<CategoryDto> navCategories = buildNavCategories(locale);
        mav.addObject("topCategory", navCategories);

        // Cart info
        CatalogCart cart = sessionService.getCart(request.getSession());
        int cartItemCount = cart.getLineItems().stream().mapToInt(li -> li.getQuantity()).sum();
        mav.addObject("cartProductCount", cartItemCount);
        mav.addObject("cartId", cart.getId());
        java.math.BigDecimal subTotal = cart.getSubTotal() != null ? cart.getSubTotal() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalTax = cart.getTotalTax() != null ? cart.getTotalTax() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal total = cart.getTotal() != null ? cart.getTotal() : java.math.BigDecimal.ZERO;
        mav.addObject("subTotalPrice", subTotal.toPlainString());
        mav.addObject("subOrderTotalTax", totalTax.toPlainString());
        mav.addObject("totalPrice", total.toPlainString());

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
        mav.addObject("currPath", requestPath);
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

        // Build localized country list for checkout address forms
        mav.addObject("countries", buildLocalizedCountries(currentLocale));
        mav.addObject("defaultCountryCode", "United States");
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

    /**
     * Build navigation category DTOs with resolved, locale-specific names.
     */
    private List<CategoryDto> buildNavCategories(String localeCode) {
        List<CategoryDto> result = new ArrayList<>();
        for (Category topCat : catalogService.getTopCategories()) {
            String topName = textService.getText(topCat.getNameTextId(), localeCode);
            List<SubCategoryDto> subs = new ArrayList<>();
            for (Category subCat : catalogService.getSubCategories(topCat.getId())) {
                String subName = textService.getText(subCat.getNameTextId(), localeCode);
                subs.add(new SubCategoryDto(subCat.getId(), subName));
            }
            result.add(new CategoryDto(topCat.getId(), topName, subs));
        }
        return result;
    }

    /**
     * ISO country codes for the countries we want in the checkout dropdown.
     */
    private static final String[] COUNTRY_CODES = {
        "AR", "AU", "AT", "BE", "BR", "CA", "CL", "CN", "CO", "CZ",
        "DK", "FI", "FR", "DE", "GR", "HU", "IN", "ID", "IE", "IL",
        "IT", "JP", "LU", "MY", "MX", "NL", "NZ", "NO", "PE", "PH",
        "PL", "PT", "RO", "SG", "ZA", "KR", "ES", "SE", "CH", "TW",
        "TH", "TR", "AE", "GB", "US", "VN"
    };

    /**
     * Build a list of countries with English value (for form submission) and
     * localized display name (for the dropdown label), sorted alphabetically
     * in the given locale.
     */
    private List<Map<String, String>> buildLocalizedCountries(Locale displayLocale)
    {
        // Use a TreeMap with locale-aware collation to sort by display name
        Collator collator = Collator.getInstance(displayLocale);
        Map<String, String> sorted = new TreeMap<>(collator);

        for (String code : COUNTRY_CODES)
        {
            Locale countryLocale = Locale.of("", code);
            String englishName = countryLocale.getDisplayCountry(Locale.ENGLISH);
            String localizedName = countryLocale.getDisplayCountry(displayLocale);
            sorted.put(localizedName, englishName);
        }

        List<Map<String, String>> result = new ArrayList<>();
        for (var entry : sorted.entrySet())
        {
            result.add(Map.of("code", entry.getValue(), "name", entry.getKey()));
        }
        return result;
    }
}
