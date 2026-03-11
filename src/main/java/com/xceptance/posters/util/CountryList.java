package com.xceptance.posters.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides a sorted list of all ISO 3166-1 countries with their
 * two-letter codes and localized display names.
 * <p>
 * This utility is designed to be reusable across any page that
 * needs a country selector (checkout, account settings, etc.).
 */
public final class CountryList
{
    private static final String[] ISO_CODES = Locale.getISOCountries();
    private static final Map<Locale, List<CountryEntry>> CACHE = new ConcurrentHashMap<>();

    private CountryList()
    {
        // utility class
    }

    /**
     * Returns an unmodifiable, alphabetically sorted list of all countries
     * with display names in English.
     */
    public static List<CountryEntry> getCountries()
    {
        return getCountries(Locale.ENGLISH);
    }

    /**
     * Returns an unmodifiable, alphabetically sorted list of all countries
     * with display names localized to the given locale.
     *
     * @param displayLocale the locale used for country name translation
     */
    public static List<CountryEntry> getCountries(Locale displayLocale)
    {
        // Normalize to language-only key for caching (e.g. de-DE → de)
        Locale cacheKey = Locale.forLanguageTag(displayLocale.getLanguage());
        return CACHE.computeIfAbsent(cacheKey, key ->
        {
            List<CountryEntry> list = new ArrayList<>(ISO_CODES.length);
            for (String code : ISO_CODES)
            {
                Locale countryLocale = new Locale("", code);
                String displayName = countryLocale.getDisplayCountry(displayLocale);
                list.add(new CountryEntry(code, displayName));
            }
            list.sort(Comparator.comparing(CountryEntry::name));
            return Collections.unmodifiableList(list);
        });
    }

    /**
     * A country entry with its ISO 3166-1 alpha-2 code and display name.
     */
    public record CountryEntry(String code, String name) {}
}

