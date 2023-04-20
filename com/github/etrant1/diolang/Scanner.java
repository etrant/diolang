package com.github.etrant1.diolang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.etrant1.diolang.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("OH?",    IF);
        keywords.put("INSTEAD OF RUNNING AWAY, YOU'RE COMING RIGHT TO ME?",   ELSE);
        keywords.put("MUDA! MUDA! MUDA! MUDA! MUDA!",  WHILE);
        keywords.put("HOHO",    FUNCTION);
        keywords.put("GOODBYE, JOJO!",   RETURN);
        keywords.put("SPEEDWAGON",  TRUE);
        keywords.put("DARIO",    FALSE);
        keywords.put("KONO",   VAR);
        keywords.put("DA!",   VAR_TERMINATOR);
        keywords.put("GO OUT THERE AND TAKE THIS USELESS WORLD FOR ALL YOU CAN GET!",  OBJECT);
        keywords.put("HINJAKU!",    NULL);
        keywords.put("WRYYY!",   PRINT);
        keywords.put("I DON'T LET ANYONE SWAGGER OVER ME!",  AND);
        keywords.put("ORA!",  OR);
        keywords.put("ZA WARUDO!",  BREAK);
    }

    Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case '*': addToken(STAR); break;
            case '/': addToken(SLASH); break;
            case '"': string(); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;


            case 'ゴ':
                // Comment
                if (match('ゴ')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                break;

            default:
                if (isDigit(c)){
                    number();
                } else if (isAlpha(c)){
                    identifier();
                }
                else {
                    Dio.error(line, "Unexpected character.");
                }
        }
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        // TODO: Space-separated keywords
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Dio.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}

// TODO: Implied semicolons
// Commit, move to next task