package com.alejandrolunatech.patterns.gof.visitor.classic;

/**
 * The textbook Visitor, kept for comparison with {@code Operations}. Every node needs
 * {@code accept}, and every visitor needs one method per node type.
 */
public interface ClassicExpr {

    <R> R accept(Visitor<R> visitor);

    interface Visitor<R> {

        R visitNum(Num num);

        R visitAdd(Add add);
    }

    record Num(double value) implements ClassicExpr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitNum(this);
        }
    }

    record Add(ClassicExpr left, ClassicExpr right) implements ClassicExpr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAdd(this);
        }
    }

    final class Evaluator implements Visitor<Double> {

        @Override
        public Double visitNum(Num num) {
            return num.value();
        }

        @Override
        public Double visitAdd(Add add) {
            return add.left().accept(this) + add.right().accept(this);
        }
    }
}
