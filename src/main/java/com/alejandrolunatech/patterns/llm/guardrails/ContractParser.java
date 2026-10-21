package com.alejandrolunatech.patterns.llm.guardrails;

import com.alejandrolunatech.patterns.llm.core.Classification;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Parses the output contract: exactly the lines {@code label=...} and {@code confidence=...}.
 * Anything else is not an answer. A flat contract keeps this repo free of a JSON library; the
 * Spring Boot repo uses JSON Schema for the same job.
 */
public final class ContractParser {

    private ContractParser() {
    }

    public static Optional<Classification> parse(String output) {
        Map<String, String> fields = new HashMap<>();
        for (String line : output.strip().split("\\R")) {
            int equals = line.indexOf('=');
            if (equals < 1 || fields.put(line.substring(0, equals).strip(), line.substring(equals + 1).strip()) != null) {
                return Optional.empty();
            }
        }
        if (fields.size() != 2 || !fields.containsKey("label") || !fields.containsKey("confidence")) {
            return Optional.empty();
        }
        try {
            double confidence = Double.parseDouble(fields.get("confidence"));
            if (confidence < 0 || confidence > 1) {
                return Optional.empty();
            }
            return Optional.of(new Classification(fields.get("label"), confidence, Classification.Source.MODEL));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
