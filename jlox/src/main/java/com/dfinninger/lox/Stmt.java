package com.dfinninger.lox;

import java.util.List;

abstract class Stmt {

    interface Visitor<R> {

        R visitExpressionStmt(Expression stmt);

        R visitPrintStmt(Print stmt);
    }

    static class Expression extends Stmt {
        final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class Print extends Stmt {
        final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    abstract <R> R accept(Visitor<R> visitor);
}
