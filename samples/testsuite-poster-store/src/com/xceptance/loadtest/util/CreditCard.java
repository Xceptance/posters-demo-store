package com.xceptance.loadtest.util;

/**
 * Provides a credit card.
 * 
 * @author sebastianloob
 */
public class CreditCard
{

    /**
     * Credit card owner.
     */
    private String owner;

    /**
     * Credit card number.
     */
    private String number;

    /**
     * Month of credit card expiration.
     */
    private String expirationMonth;

    /**
     * Year of credit card expiration.
     */
    private String expirationYear;

    public CreditCard()
    {
        this.number = "4111111111111111";
        this.owner = "John Doe";
    }

    public CreditCard(Account account)
    {
        this.number = "4111111111111111";
        this.owner = account.getFirstName() + " " + account.getLastName();
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getExpirationMonth()
    {
        return expirationMonth;
    }

    public void setExpirationMonth(String expirationMonth)
    {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationYear()
    {
        return expirationYear;
    }

    public void setExpirationYear(String expirationYear)
    {
        this.expirationYear = expirationYear;
    }
}
