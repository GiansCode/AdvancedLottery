package com.gmail.ianlim224.advancedlottery.utils;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;

import java.text.DecimalFormat;

public final class SpigotCommons {
    private static final long MILLION  = 1_000_000L;
    private static final long BILLION  = 1_000_000_000L;
    private static final long TRILLION = 1_000_000_000_000L;

    private static DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###,##0.00");

    private SpigotCommons() {}

    public static void setMoneyFormat(String pattern, AdvancedLottery plugin) {
        try {
            MONEY_FORMAT = new DecimalFormat(pattern);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid money_display_format — using default.");
        }
    }

    public static boolean isInteger(String s) {
        if (s == null || s.isEmpty()) return false;
        int start = s.charAt(0) == '-' ? 1 : 0;
        if (start == 1 && s.length() == 1) return false;
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') return false;
        }
        return true;
    }

    public static String formatMoney(double amount) {
        if (amount < MILLION)  return MONEY_FORMAT.format(amount);
        if (amount < BILLION)  return MONEY_FORMAT.format(amount / MILLION)  + " million";
        if (amount < TRILLION) return MONEY_FORMAT.format(amount / BILLION)  + " billion";
        return MONEY_FORMAT.format(amount / TRILLION) + " trillion";
    }
}
