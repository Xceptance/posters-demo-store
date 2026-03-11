package com.xceptance.posters.entity;

import java.util.List;

/**
 * Credit card vendor detection from BIN (Bank Identification Number) prefixes.
 * Supports 8 vendors with their prefix ranges, valid card lengths, CVV lengths,
 * and number formatting groups.
 */
public enum CreditCardVendor
{
    VISA("Visa",
         List.of(new PrefixRange("4")),
         List.of(16, 13, 19),
         3,
         new int[]{4, 4, 4, 4}),

    MASTERCARD("Mastercard",
               List.of(new PrefixRange("51", "55"),
                       new PrefixRange("2221", "2720")),
               List.of(16),
               3,
               new int[]{4, 4, 4, 4}),

    AMEX("American Express",
         List.of(new PrefixRange("34"),
                 new PrefixRange("37")),
         List.of(15),
         4,
         new int[]{4, 6, 5}),

    UNIONPAY("UnionPay",
             List.of(new PrefixRange("62")),
             List.of(16, 17, 18, 19),
             3,
             new int[]{4, 4, 4, 4}),

    JCB("JCB",
        List.of(new PrefixRange("3528", "3589")),
        List.of(16, 17, 18, 19),
        3,
        new int[]{4, 4, 4, 4}),

    DISCOVER("Discover",
             List.of(new PrefixRange("6011"),
                     new PrefixRange("644", "649"),
                     new PrefixRange("65")),
             List.of(16, 17, 18, 19),
             3,
             new int[]{4, 4, 4, 4}),

    DINERS_CLUB("Diners Club",
                List.of(new PrefixRange("300", "305"),
                        new PrefixRange("36"),
                        new PrefixRange("38")),
                List.of(14, 16),
                3,
                new int[]{4, 6, 4}),

    MAESTRO("Maestro",
            List.of(new PrefixRange("5018"),
                    new PrefixRange("5020"),
                    new PrefixRange("5038"),
                    new PrefixRange("5893"),
                    new PrefixRange("6304"),
                    new PrefixRange("6759"),
                    new PrefixRange("6761"),
                    new PrefixRange("6762"),
                    new PrefixRange("6763")),
            List.of(12, 13, 14, 15, 16, 17, 18, 19),
            3,
            new int[]{4, 4, 4, 4});

    private final String displayName;
    private final List<PrefixRange> prefixRanges;
    private final List<Integer> validLengths;
    private final int cvvLength;
    private final int[] formatGroups;

    CreditCardVendor(String displayName,
                     List<PrefixRange> prefixRanges,
                     List<Integer> validLengths,
                     int cvvLength,
                     int[] formatGroups)
    {
        this.displayName = displayName;
        this.prefixRanges = prefixRanges;
        this.validLengths = validLengths;
        this.cvvLength = cvvLength;
        this.formatGroups = formatGroups;
    }

    public String getDisplayName() { return displayName; }
    public List<Integer> getValidLengths() { return validLengths; }
    public int getCvvLength() { return cvvLength; }
    public int[] getFormatGroups() { return formatGroups; }

    /**
     * Detects the card vendor from a card number's BIN prefix.
     * Checks vendors in enum order (most specific prefixes should be listed
     * before less specific ones where overlap is possible).
     *
     * @param cardNumber the card number (digits only, may be partial)
     * @return the detected vendor, or null if no match
     */
    public static CreditCardVendor detect(String cardNumber)
    {
        if (cardNumber == null || cardNumber.isEmpty())
        {
            return null;
        }

        // Strip non-digits
        String digits = cardNumber.replaceAll("\\D", "");
        if (digits.isEmpty())
        {
            return null;
        }

        // Check each vendor — order matters for overlapping prefixes
        for (CreditCardVendor vendor : values())
        {
            for (PrefixRange range : vendor.prefixRanges)
            {
                if (range.matches(digits))
                {
                    return vendor;
                }
            }
        }
        return null;
    }

    /**
     * Checks if the given card number length is valid for this vendor.
     */
    public boolean isValidLength(int length)
    {
        return validLengths.contains(length);
    }

    /**
     * A BIN prefix range. Can be a single prefix or a numeric range (lo–hi inclusive).
     */
    public static class PrefixRange
    {
        private final String lo;
        private final String hi;

        /** Single prefix (exact match). */
        public PrefixRange(String prefix)
        {
            this.lo = prefix;
            this.hi = prefix;
        }

        /** Numeric range [lo, hi] inclusive. lo and hi must have the same length. */
        public PrefixRange(String lo, String hi)
        {
            this.lo = lo;
            this.hi = hi;
        }

        /**
         * Checks if the given digits (possibly partial) match this prefix range.
         * For a range like ("51","55"), a card starting with "51" through "55" matches.
         */
        public boolean matches(String digits)
        {
            int prefixLen = lo.length();

            if (digits.length() < prefixLen)
            {
                // Partial input: check if it could still match
                // e.g., digits="5" can potentially match range "51"-"55"
                String partialLo = lo.substring(0, digits.length());
                String partialHi = hi.substring(0, digits.length());
                return digits.compareTo(partialLo) >= 0 && digits.compareTo(partialHi) <= 0;
            }

            // Full prefix comparison
            String prefix = digits.substring(0, prefixLen);
            return prefix.compareTo(lo) >= 0 && prefix.compareTo(hi) <= 0;
        }
    }
}
