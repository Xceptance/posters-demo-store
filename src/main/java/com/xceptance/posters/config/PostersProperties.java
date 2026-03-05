/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xceptance.posters.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application-specific configuration properties, bound from the
 * {@code posters.*} namespace in {@code application.yml}.
 * <p>
 * Replaces the former Ninja-specific {@code PosterConstants} class.
 */
@Component
@ConfigurationProperties(prefix = "posters")
public class PostersProperties
{
    private String currency;
    private BigDecimal shippingCosts = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private String unitOfLength;
    private int pageSize;
    private String languages;
    private String version;
    private boolean importCustomer;
    private String luceneIndexDir;
    private Regex regex = new Regex();

    // -- Getters and Setters --

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public BigDecimal getShippingCosts()
    {
        return shippingCosts;
    }

    public void setShippingCosts(BigDecimal shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    public BigDecimal getTax()
    {
        return tax;
    }

    public void setTax(BigDecimal tax)
    {
        this.tax = tax;
    }

    public String getUnitOfLength()
    {
        return unitOfLength;
    }

    public void setUnitOfLength(String unitOfLength)
    {
        this.unitOfLength = unitOfLength;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getLanguages()
    {
        return languages;
    }

    public void setLanguages(String languages)
    {
        this.languages = languages;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public boolean isImportCustomer()
    {
        return importCustomer;
    }

    public void setImportCustomer(boolean importCustomer)
    {
        this.importCustomer = importCustomer;
    }

    public String getLuceneIndexDir()
    {
        return luceneIndexDir;
    }

    public void setLuceneIndexDir(String luceneIndexDir)
    {
        this.luceneIndexDir = luceneIndexDir;
    }

    public Regex getRegex()
    {
        return regex;
    }

    public void setRegex(Regex regex)
    {
        this.regex = regex;
    }

    /**
     * Returns the supported languages as an array of locale codes.
     */
    public String[] getLanguageArray()
    {
        return languages != null ? languages.split(",") : new String[0];
    }

    /**
     * Nested class for regex validation patterns.
     */
    public static class Regex
    {
        private String zip;
        private String name;
        private String creditCard;
        private String email;
        private String productCount;

        public String getZip()
        {
            return zip;
        }

        public void setZip(String zip)
        {
            this.zip = zip;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getCreditCard()
        {
            return creditCard;
        }

        public void setCreditCard(String creditCard)
        {
            this.creditCard = creditCard;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public String getProductCount()
        {
            return productCount;
        }

        public void setProductCount(String productCount)
        {
            this.productCount = productCount;
        }
    }
}
