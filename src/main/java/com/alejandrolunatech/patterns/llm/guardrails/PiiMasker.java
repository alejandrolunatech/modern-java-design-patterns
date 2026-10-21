package com.alejandrolunatech.patterns.llm.guardrails;

import java.util.regex.Pattern;

/**
 * Replaces card numbers, IBANs and email addresses with placeholders, so they never leave our
 * system. Regexes catch the common formats; a production system would add a dedicated detector.
 */
public final class PiiMasker implements InputGuardrail {

    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");
    private static final Pattern IBAN = Pattern.compile("\\b[A-Z]{2}\\d{2}(?: ?[A-Z0-9]{4}){2,7}(?: ?[A-Z0-9]{1,3})?\\b");
    private static final Pattern CARD = Pattern.compile("\\b\\d(?:[ -]?\\d){12,18}\\b");

    @Override
    public String apply(String text) {
        String masked = EMAIL.matcher(text).replaceAll("[EMAIL]");
        masked = IBAN.matcher(masked).replaceAll("[IBAN]");
        return CARD.matcher(masked).replaceAll("[CARD]");
    }
}
