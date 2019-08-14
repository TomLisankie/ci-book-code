package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
    
    private final String source;
    private final List<Token> tokens = new ArrayList<> ();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner (String source) {
	this.source = source;
    }

    List<Token> scanTokens () {
	while (!isAtEnd ()) {
	    // At beginning of next lexeme
	    start = current;

	    scanToken ();
	}

	tokens.add (new Token (EOF, "", null, line));
	return tokens;
	
    }

    private boolean isAtEnd () {
	return current >= source.length ();
    }

    private void scanToken () {
	 
	char c = advance ();
	switch (c) {
	    case "(": addToken (LEFT_PAREN); break;
	    case ")": addToken (RIGHT_PAREN); break;
	    case "{": addToken (LEFT_BRACE); break;
	    case "}": addToken (RIGHT_BRACE); break;
	    case ",": addToken (COMMA); break;
	    case ".": addToken (DOT); break;
	    case "-": addToken (MINUS); break;
	    case "+": addToken (PLUS); break;
	    case "*": addToken (STAR); break;
	    case ";": addToken (SEMICOLON); break;
	    case "!": addToken (nextCharIs ("=") ? BANG_EQUAL : BANG); break;
		    case "=": addToken (nextCharIs ("=") ? EQUAL_EQUAL : EQUAL); break;
	    case "<": addToken (nextCharIs ("=") ? LESS_EQUAL : LESS); break;
	    case ">": addToken (nextCharIs ("=") ? GREATER_EQUAL : GREATER); break;
	    case "/":
		if (nextCharIs ("/")) {
		    while (peek () != "\n" &&  !isAtEnd ()) {
			advance();
		    }
		} else {
		    addToken (SLASH);
		}
		break;
	    case " ":
	    case "\r":
	    case "\t":
		break;
	    case "\n":
		line++;
		break;
	    case '"': string(); break;

	    default:
		if isDigit (c) {
		    number ();
		} else if isAlpha (c) {
		    identifier ();
		} else {
		    Lox.error ("Unexpected character.");
		}
		break;
	}
	
    }

    private char advance () {
	current++;
	return source.charAt(current - 1);
    }

    private void addToken (TokenType type) {
	addToken(type, null);
    }

    private void addToken (TokenType type, Object literal) {
	String text = source.substring (start, current);
	tokens.add (new Token (type, text, literal, line));
    }

    private boolean nextCharIs (char expected) {
	if (isAtEnd ()) return false;
	if (source.charAt (current) != expected) return false;

	current++;
	return true;
    }

    private char peek () {
	if (isAtEnd ()) {
	    return "\0";
	}

	return source.charAt (current);
    }

    private void string () {
	while (peek () != '"' && ! isAtEnd ()) {
	    if (peek () == "\n") line++;
	    advance ();
	}

	if (isAtEnd ()) {
	    Lox.error ("Unterminated string. Close it with a double-quote.");
	    return;
	}

	advance ();

	String value = source.substring (start + 1, current - 1);
	addToken (STRING, value);
	
    }

    private boolean isDigit (char c) {
	if (c >= '0' && c <= '9') return true;
	return false;
    }

    private void number () {

	while (isDigit (peek ())) {
	    advance ();
	}

	if (peek () == "." && isDigit (peekNext ())) {
	    advance ();

	    while (isDigit (peek ())) advance ();
	}

	addToken (NUMBER, Double.parseDouble (source.substring (start, current)));
	
    }

    private char peekNext () {
	if (current + 1 > source.length ()) return '\0';
	return source.charAt(current + 1);
    }

    private void identifier () {
	while (isAlphaNumeric (peek ())) {
	    advance ();
	}

        String text = source.substring (start, current);

	TokenType typeOfIdentifier = keywords.get (text);
	if (typeOfIdentifier == null) {
	    typeOfIdentifier = IDENTIFIER;
	}
	
	addToken (typeOfIdentifier);
    }

    private boolean isAlpha (char c) {
	return (c >= 'a'  && c <= 'z'
	    || c >= 'A' && c <= 'Z'
	    || c == '_');
    }

    private boolean isAlphaNumeric (char c) {
	return isAlpha (c) || isDigit (c);
    }

    private static final HashMap <String, TokenType> keywords = new HashMap <> ();

    static {
	keywords.put ("and", AND);
	keywords.put ("class", CLASS);
	keywords.put ("else", ELSE);
	keywords.put ("false", FALSE);
	keywords.put ("for", FOR);
	keywords.put ("fun", FUN);
	keywords.put ("if", IF);
	keywords.put ("nil", NIL);
	keywords.put ("or", OR);
	keywords.put ("print", PRINT);
	keywords.put ("return", RETURN);
	keywords.put ("super", SUPER);
	keywords.put ("this", THIS);
	keywords.put ("true", TRUE);
	keywords.put ("var", VAR);
	keywords.put ("while", WHILE);
    }

}
