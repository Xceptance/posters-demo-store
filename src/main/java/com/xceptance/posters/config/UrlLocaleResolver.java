package com.xceptance.posters.config;

import java.util.Locale;
import java.util.Map;

import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Resolves the locale from the URL path segment: /{locale}/...
 * For example, "/de-DE/productDetail/..." resolves to Locale("de", "DE").
 *
 * <p>This integrates with Spring's i18n infrastructure so that Thymeleaf's
 * {@code #{key}} message expressions and {@code #numbers.formatCurrency()}
 * resolve to the correct locale automatically.</p>
 *
 * <p>Handles non-standard locale codes (e.g. "en-GB" maps to "en-GB")
 * for proper Java currency/number formatting.</p>
 */
public class UrlLocaleResolver extends AbstractLocaleResolver
{
    /**
     * Maps custom URL locale strings to proper Java Locale instances.
     * "en-GB" is mapped to "en-GB" because ISO 3166-1 uses "GB" for
     * the United Kingdom, and Java's NumberFormat/Currency need this.
     */
    private static final Map<String, Locale> LOCALE_MAP = Map.of(
        "en-US", Locale.forLanguageTag("en-US"),
        "en-GB", Locale.forLanguageTag("en-GB"),
        "de-DE", Locale.forLanguageTag("de-DE"),
        "sv-SE", Locale.forLanguageTag("sv-SE")
    );

    public UrlLocaleResolver()
    {
        setDefaultLocale(Locale.forLanguageTag("en-US"));
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request)
    {
        String path = request.getRequestURI();
        String[] segments = path.split("/");
        if (segments.length > 1)
        {
            String localeStr = segments[1];
            // Check our custom mapping first (handles en-GB → en-GB)
            Locale mapped = LOCALE_MAP.get(localeStr);
            if (mapped != null)
            {
                return mapped;
            }
            // Fallback: try standard conversion
            Locale locale = Locale.forLanguageTag(localeStr);
            if (!locale.getLanguage().isEmpty())
            {
                return locale;
            }
        }
        return getDefaultLocale();
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale)
    {
        // Locale is determined by URL, not settable via this method
    }
}
