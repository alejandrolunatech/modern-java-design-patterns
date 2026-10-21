package com.alejandrolunatech.patterns.gof.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import com.alejandrolunatech.patterns.gof.visitor.Expr.Add;
import com.alejandrolunatech.patterns.gof.visitor.Expr.Mul;
import com.alejandrolunatech.patterns.gof.visitor.Expr.Neg;
import com.alejandrolunatech.patterns.gof.visitor.Expr.Num;
import com.alejandrolunatech.patterns.gof.visitor.classic.ClassicExpr;
import org.junit.jupiter.api.Test;

class OperationsTest {

    private final Expr expr = new Mul(new Add(new Num(2), new Num(3)), new Neg(new Num(4)));

    @Test
    void evaluates() {
        assertThat(Operations.evaluate(expr)).isEqualTo(-20.0);
    }

    @Test
    void prints() {
        assertThat(Operations.print(expr)).isEqualTo("(2 + 3) * -4");
    }

    @Test
    void simplifiesWithNestedPatterns() {
        Expr messy = new Add(new Num(0), new Mul(new Num(1), new Neg(new Neg(new Num(7)))));

        assertThat(Operations.simplify(messy)).isEqualTo(new Num(7));
        assertThat(Operations.simplify(new Mul(new Num(0), expr))).isEqualTo(new Num(0));
    }

    @Test
    void theClassicVisitorGivesTheSameAnswerWithMoreCeremony() {
        ClassicExpr classic = new ClassicExpr.Add(new ClassicExpr.Num(2), new ClassicExpr.Num(3));

        assertThat(classic.accept(new ClassicExpr.Evaluator())).isEqualTo(Operations.evaluate(new Add(new Num(2), new Num(3))));
    }
}
