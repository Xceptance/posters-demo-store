package com.xceptance.posters.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Provides a sorted list of all ISO 3166-1 countries with their
 * two-letter codes and English display names.
 * <p>
 * This utility is designed to be reusable across any page that
 * needs a country selector (checkout, account settings, etc.).
 */
public final class CountryList
{
    private static final List<CountryEntry> COUNTRIES;

    static
    {
        String[] isoCodes = Locale.getISOCountries();
        List<CountryEntry> list = new ArrayList<>(isoCodes.length);
        for (String code : isoCodes)
        {
            Locale locale = new Locale("", code);
            String displayName = locale.getDisplayCountry(Locale.ENGLISH);
            list.add(new CountryEntry(code, displayName));
        }
        list.sort(Comparator.comparing(CountryEntry::name));
        COUNTRIES = Collections.unmodifiableList(list);
    }

    private CountryList()
    {
        // utility class
    }

    /**
     * Returns an unmodifiable, alphabetically sorted list of all countries.
     */
    public static List<CountryEntry> getCountries()
    {
        return COUNTRIES;
    }

    /**
     * A country entry with its ISO 3166-1 alpha-2 code and display name.
     */
    public record CountryEntry(String code, String name) {}
}
