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
@Table(name = "catalog_order_addresses")
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CatalogOrder order;

    @Column(nullable = false)
    private String type; // SHIPPING or BILLING

    @Column(name = "recipient_first_name", nullable = false)
    private String recipientFirstName;

    @Column(name = "recipient_last_name", nullable = false)
    private String recipientLastName;

    @Column
    private String company;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column
    private String phone;

    // Getters and Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public CatalogOrder getOrder() { return order; }
    public void setOrder(CatalogOrder order) { this.order = order; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRecipientFirstName() { return recipientFirstName; }
    public void setRecipientFirstName(String v) { this.recipientFirstName = v; }

    public String getRecipientLastName() { return recipientLastName; }
    public void setRecipientLastName(String v) { this.recipientLastName = v; }

    public String getCompany() { return company; }
    public void setCompany(String v) { this.company = v; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String v) { this.addressLine1 = v; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String v) { this.addressLine2 = v; }

    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }

    public String getState() { return state; }
    public void setState(String v) { this.state = v; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String v) { this.postalCode = v; }

    public String getCountry() { return country; }
    public void setCountry(String v) { this.country = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
}
