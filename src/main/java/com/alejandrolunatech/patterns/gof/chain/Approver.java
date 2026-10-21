package com.alejandrolunatech.patterns.gof.chain;

import java.util.Optional;
import java.util.Set;

/**
 * One link in the chain. Instead of each handler holding a {@code next} pointer, a handler
 * returns {@link Optional#empty()} to pass, and the chain itself walks the list.
 */
@FunctionalInterface
public interface Approver {

    Optional<Decision> decide(Expense expense);

    static Approver rejectCategories(Set<String> forbidden) {
        return expense -> forbidden.contains(expense.category())
                ? Optional.of(new Decision.Rejected("policy", "category not reimbursed: " + expense.category()))
                : Optional.empty();
    }

    static Approver autoApproveBelow(long cents) {
        return expense -> expense.amountCents() < cents
                ? Optional.of(new Decision.Approved("auto"))
                : Optional.empty();
    }

    static Approver managerUpTo(String manager, long cents) {
        return expense -> expense.amountCents() <= cents
                ? Optional.of(new Decision.Approved(manager))
                : Optional.empty();
    }
}
