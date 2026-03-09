package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sites")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "main_locale_id", nullable = false)
    private Locale mainLocale;

    @ManyToOne
    @JoinColumn(name = "fallback_locale_id")
    private Locale fallbackLocale;

    @Column(nullable = false)
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_table_id")
    private PriceTable priceTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_table_id")
    private TaxTable taxTable;

    @Column(name = "prices_are_net", nullable = false)
    private Boolean pricesAreNet = true;

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Locale getMainLocale() {
        return mainLocale;
    }

    public void setMainLocale(Locale mainLocale) {
        this.mainLocale = mainLocale;
    }

    public Locale getFallbackLocale() {
        return fallbackLocale;
    }

    public void setFallbackLocale(Locale fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PriceTable getPriceTable() {
        return priceTable;
    }

    public void setPriceTable(PriceTable priceTable) {
        this.priceTable = priceTable;
    }

    public TaxTable getTaxTable() {
        return taxTable;
    }

    public void setTaxTable(TaxTable taxTable) {
        this.taxTable = taxTable;
    }

    public Boolean getPricesAreNet() {
        return pricesAreNet;
    }

    public void setPricesAreNet(Boolean pricesAreNet) {
        this.pricesAreNet = pricesAreNet;
    }
}
