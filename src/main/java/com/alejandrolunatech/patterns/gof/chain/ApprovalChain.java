package com.alejandrolunatech.patterns.gof.chain;

import java.util.List;
import java.util.Optional;

/** The first approver that decides wins; the order of the list is the policy. */
public record ApprovalChain(List<Approver> approvers) {

    public ApprovalChain {
        approvers = List.copyOf(approvers);
    }

    public Decision decide(Expense expense) {
        return approvers.stream()
                .map(approver -> approver.decide(expense))
                .flatMap(Optional::stream)
                .findFirst()
                .orElseGet(Decision.Escalated::new);
    }
}
