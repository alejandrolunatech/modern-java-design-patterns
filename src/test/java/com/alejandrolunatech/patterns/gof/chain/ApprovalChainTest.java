package com.alejandrolunatech.patterns.gof.chain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ApprovalChainTest {

    private final ApprovalChain chain = new ApprovalChain(List.of(
            Approver.rejectCategories(Set.of("alcohol")),
            Approver.autoApproveBelow(5_000),
            Approver.managerUpTo("team lead", 50_000)));

    @Test
    void smallExpensesAreApprovedAutomatically() {
        assertThat(chain.decide(new Expense("ana", "travel", 1_200))).isEqualTo(new Decision.Approved("auto"));
    }

    @Test
    void largerExpensesGoToTheManager() {
        assertThat(chain.decide(new Expense("ana", "training", 40_000))).isEqualTo(new Decision.Approved("team lead"));
    }

    @Test
    void policyRunsFirstSoEvenSmallForbiddenExpensesAreRejected() {
        assertThat(chain.decide(new Expense("ana", "alcohol", 900))).isInstanceOf(Decision.Rejected.class);
    }

    @Test
    void whatNobodyCanDecideIsEscalated() {
        assertThat(chain.decide(new Expense("ana", "hardware", 90_000))).isInstanceOf(Decision.Escalated.class);
    }
}
