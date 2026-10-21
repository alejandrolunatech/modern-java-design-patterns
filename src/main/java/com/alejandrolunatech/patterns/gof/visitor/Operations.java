package com.alejandrolunatech.patterns.gof.visitor;

import com.alejandrolunatech.patterns.gof.visitor.Expr.Add;
import com.alejandrolunatech.patterns.gof.visitor.Expr.Mul;
import com.alejandrolunatech.patterns.gof.visitor.Expr.Neg;
import com.alejandrolunatech.patterns.gof.visitor.Expr.Num;

/**
 * Each operation is a method with an exhaustive switch. Adding an operation touches no node type;
 * adding a node type makes every switch here fail to compile until it is handled.
 */
public final class Operations {

    private Operations() {
    }

    public static double evaluate(Expr expr) {
        return switch (expr) {
            case Num(double value) -> value;
            case Add(Expr left, Expr right) -> evaluate(left) + evaluate(right);
            case Mul(Expr left, Expr right) -> evaluate(left) * evaluate(right);
            case Neg(Expr operand) -> -evaluate(operand);
        };
    }

    public static String print(Expr expr) {
        return switch (expr) {
            case Num(double value) -> value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
            case Add(Expr left, Expr right) -> "(" + print(left) + " + " + print(right) + ")";
            case Mul(Expr left, Expr right) -> print(left) + " * " + print(right);
            case Neg(Expr operand) -> "-" + print(operand);
        };
    }

    /** Nested record patterns and guards: rules a classic Visitor would need many lines for. */
    public static Expr simplify(Expr expr) {
        return switch (expr) {
            case Add(Num(double zero), Expr other) when zero == 0 -> simplify(other);
            case Add(Expr other, Num(double zero)) when zero == 0 -> simplify(other);
            case Mul(Num(double one), Expr other) when one == 1 -> simplify(other);
            case Mul(Expr other, Num(double one)) when one == 1 -> simplify(other);
            case Mul(Num(double zero), Expr other) when zero == 0 -> new Num(0);
            case Neg(Neg(Expr inner)) -> simplify(inner);
            case Add(Expr left, Expr right) -> new Add(simplify(left), simplify(right));
            case Mul(Expr left, Expr right) -> new Mul(simplify(left), simplify(right));
            case Neg(Expr operand) -> new Neg(simplify(operand));
            case Num num -> num;
        };
    }
}
