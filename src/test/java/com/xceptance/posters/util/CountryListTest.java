package com.xceptance.posters.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CountryListTest
{
    @Test
    void listIsNonEmpty()
    {
        List<CountryList.CountryEntry> countries = CountryList.getCountries();
        assertThat(countries).isNotEmpty();
        assertThat(countries.size()).isGreaterThan(200);
    }

    @Test
    void listIsSortedByName()
    {
        List<CountryList.CountryEntry> countries = CountryList.getCountries();
        for (int i = 1; i < countries.size(); i++)
        {
            assertThat(countries.get(i).name())
                .as("Expected '%s' to come after '%s'",
                    countries.get(i).name(), countries.get(i - 1).name())
                .isGreaterThanOrEqualTo(countries.get(i - 1).name());
        }
    }

    @Test
    void containsKnownCountries()
    {
        List<CountryList.CountryEntry> countries = CountryList.getCountries();

        assertThat(countries)
            .anyMatch(c -> "US".equals(c.code()) && "United States".equals(c.name()));
        assertThat(countries)
            .anyMatch(c -> "DE".equals(c.code()) && "Germany".equals(c.name()));
        assertThat(countries)
            .anyMatch(c -> "SE".equals(c.code()) && "Sweden".equals(c.name()));
    }

    @Test
    void codesAreIso3166Alpha2()
    {
        for (CountryList.CountryEntry entry : CountryList.getCountries())
        {
            assertThat(entry.code())
                .as("Country code for '%s'", entry.name())
                .matches("[A-Z]{2}");
        }
    }

    @Test
    void listIsUnmodifiable()
    {
        List<CountryList.CountryEntry> countries = CountryList.getCountries();
        org.junit.jupiter.api.Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> countries.add(new CountryList.CountryEntry("XX", "Test"))
        );
    }

    @Test
    void localizedCountryNamesInGerman()
    {
        List<CountryList.CountryEntry> countries = CountryList.getCountries(java.util.Locale.GERMAN);
        assertThat(countries)
            .anyMatch(c -> "DE".equals(c.code()) && "Deutschland".equals(c.name()));
        assertThat(countries)
            .anyMatch(c -> "US".equals(c.code()) && "Vereinigte Staaten".equals(c.name()));
    }

    @Test
    void localizedCountryNamesInSwedish()
    {
        java.util.Locale swedish = java.util.Locale.forLanguageTag("sv");
        List<CountryList.CountryEntry> countries = CountryList.getCountries(swedish);
        assertThat(countries)
            .anyMatch(c -> "SE".equals(c.code()) && "Sverige".equals(c.name()));
    }

    @Test
    void localizedListIsSortedByLocalizedName()
    {
        List<CountryList.CountryEntry> countries = CountryList.getCountries(java.util.Locale.GERMAN);
        for (int i = 1; i < countries.size(); i++)
        {
            assertThat(countries.get(i).name())
                .isGreaterThanOrEqualTo(countries.get(i - 1).name());
        }
    }
}
