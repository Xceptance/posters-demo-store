package com.xceptance.posters.entity;

import java.util.regex.Pattern;

/**
 * Validates SKUs against the format: [A-Z0-9]{6,10}-[0-9]{4}
 *
 * <ul>
 *   <li>Simple product: XXXXXX-0000 (purchasable)</li>
 *   <li>Master product: prefix only (not purchasable, not in price table)</li>
 *   <li>Variant: XXXXXX-NNNN where N ≥ 0001 (purchasable)</li>
 * </ul>
 */
public final class SkuValidator {

    private static final Pattern FULL_SKU_PATTERN =
        Pattern.compile("[A-Z0-9]{6,10}-[0-9]{4}");

    private static final Pattern MASTER_PREFIX_PATTERN =
        Pattern.compile("[A-Z0-9]{6,10}");

    private SkuValidator() {
        // utility class
    }

    /**
     * Validates a full purchasable SKU (prefix-suffix format).
     */
    public static boolean isValid(String sku) {
        if (sku == null || sku.isEmpty()) {
            return false;
        }
        return FULL_SKU_PATTERN.matcher(sku).matches();
    }

    /**
     * Validates a master product SKU prefix (no suffix).
     */
    public static boolean isValidMasterPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return false;
        }
        return MASTER_PREFIX_PATTERN.matcher(prefix).matches();
    }

    /**
     * Extracts the variant number (the 4-digit suffix) from a full purchasable SKU.
     *
     * @throws IllegalArgumentException if the SKU is not valid
     */
    public static int extractVariantNumber(String sku) {
        if (!isValid(sku)) {
            throw new IllegalArgumentException("Invalid SKU: " + sku);
        }
        String suffix = sku.substring(sku.lastIndexOf('-') + 1);
        return Integer.parseInt(suffix);
    }
}
