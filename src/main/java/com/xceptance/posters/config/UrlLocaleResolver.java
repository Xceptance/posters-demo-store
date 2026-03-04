package com.xceptance.posters.config;

import java.util.Locale;

import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Resolves the locale from the URL path segment: /{locale}/...
 * For example, "/de-DE/productDetail/..." resolves to Locale("de", "DE").
 *
 * <p>This integrates with Spring's i18n infrastructure so that Thymeleaf's
 * {@code #{key}} message expressions resolve to the correct locale automatically.</p>
 */
public class UrlLocaleResolver extends AbstractLocaleResolver
{
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
            // Convert "en-US" or "de-DE" to a proper Locale
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
