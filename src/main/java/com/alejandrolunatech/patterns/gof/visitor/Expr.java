package com.alejandrolunatech.patterns.gof.visitor;

/** A small expression tree. Sealed records replace the Visitor's accept/visit double dispatch. */
public sealed interface Expr {

    record Num(double value) implements Expr {
    }

    record Add(Expr left, Expr right) implements Expr {
    }

    record Mul(Expr left, Expr right) implements Expr {
    }

    record Neg(Expr operand) implements Expr {
    }
}
