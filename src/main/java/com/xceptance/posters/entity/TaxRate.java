package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tax_rates")
@IdClass(TaxRateId.class)
public class TaxRate {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "tax_table_id", nullable = false)
    private TaxTable taxTable;

    @Id
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal rate;

    public TaxTable getTaxTable() {
        return taxTable;
    }

    public void setTaxTable(TaxTable taxTable) {
        this.taxTable = taxTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
