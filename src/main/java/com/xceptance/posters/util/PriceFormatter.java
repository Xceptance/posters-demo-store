package com.xceptance.posters.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Utility for formatting prices according to locale conventions.
 * Handles the mapping from URL locale strings (e.g. "en-GB") to proper
 * Java Locale instances for currency formatting.
 *
 * <p>Examples:
 * <ul>
 *   <li>en-US: $17.00</li>
 *   <li>en-GB: £17.00</li>
 *   <li>de-DE: 17,00 €</li>
 *   <li>sv-SE: 185,30 kr</li>
 * </ul>
 */
public final class PriceFormatter
{
    private static final Map<String, Locale> LOCALE_MAP = Map.of(
        "en-US", Locale.forLanguageTag("en-US"),
        "en-GB", Locale.forLanguageTag("en-GB"),
        "de-DE", Locale.forLanguageTag("de-DE"),
        "sv-SE", Locale.forLanguageTag("sv-SE")
    );

    private PriceFormatter() {}

    /**
     * Maps a URL locale string (e.g. "en-GB") to a proper Java Locale.
     */
    public static Locale toJavaLocale(String urlLocale)
    {
        if (urlLocale == null) return Locale.US;
        Locale mapped = LOCALE_MAP.get(urlLocale);
        if (mapped != null) return mapped;
        Locale parsed = Locale.forLanguageTag(urlLocale);
        return parsed.getLanguage().isEmpty() ? Locale.US : parsed;
    }

    /**
     * Formats a price according to the given URL locale's currency conventions.
     *
     * @param price     the price value
     * @param urlLocale the URL locale string (e.g. "de-DE", "en-GB", "sv-SE")
     * @return formatted price string (e.g. "17,00 €", "185,30 kr")
     */
    public static String format(BigDecimal price, String urlLocale)
    {
        Locale javaLocale = toJavaLocale(urlLocale);
        NumberFormat fmt = NumberFormat.getCurrencyInstance(javaLocale);
        return fmt.format(price);
    }
}
