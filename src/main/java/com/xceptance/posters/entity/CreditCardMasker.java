package com.xceptance.posters.entity;

/**
 * Masks credit card numbers using standard PAN format:
 * first 6 and last 4 digits visible, middle replaced with asterisks.
 *
 * <p>Example: {@code 512345******3456}
 */
public final class CreditCardMasker
{
    private CreditCardMasker()
    {
        // Utility class
    }

    /**
     * Masks a card number. Strips non-digit characters first.
     * If the card has 10 or fewer digits, returns it unmasked (nothing to hide).
     *
     * @param cardNumber the card number (may contain spaces, dashes, etc.)
     * @return the masked PAN string, or empty string for null/blank input
     */
    public static String mask(String cardNumber)
    {
        if (cardNumber == null)
        {
            return "";
        }

        String digits = cardNumber.replaceAll("\\D", "");
        if (digits.isEmpty())
        {
            return "";
        }

        int len = digits.length();
        int visiblePrefix = 6;
        int visibleSuffix = 4;

        // If too short to mask, return as-is
        if (len <= visiblePrefix + visibleSuffix)
        {
            return digits;
        }

        int maskedCount = len - visiblePrefix - visibleSuffix;
        return digits.substring(0, visiblePrefix)
               + "*".repeat(maskedCount)
               + digits.substring(len - visibleSuffix);
    }
}
