package com.craftinginterpreters.lox;

// was gonna name this "Expression", but the book insists "Expr" is common place in compilers
abstract class Expr {
    static class Binary extends Expr {

	final Expr left;
	final Token operator;
	final Expr right;
	
	Binary (Expr left, Token operator, Expr right) {
	    this.left = left;
	    this.operator = operator;
	    this.right = right;
	}
    }
}
