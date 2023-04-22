package diolang.lexer;

import diolang.Dio;
import diolang.Token;
import diolang.TokenType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static diolang.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("OH?", IF);
        keywords.put("INSTEAD OF RUNNING AWAY, YOU'RE COMING RIGHT TO ME?",   ELSE);
        keywords.put("IF YOU'D RATHER DIE THEN CLIMB THOSE STAIRS.",    WHILE);
        keywords.put("TAKE THIS USELESS WORLD FOR ALL YOU CAN GET.",    FUNCTION);
        keywords.put("GOODBYE, JOJO!",  RETURN);
        keywords.put("SPEEDWAGON",  TRUE);
        keywords.put("DARIO",   FALSE);
        keywords.put("STAND POWER!",   OBJECT);
        keywords.put("MENACING...",  VAR);
        keywords.put("HINJAKU!",    NULL);
        keywords.put("WRYYY!",   PRINT);
        keywords.put("AND",  AND);
        keywords.put("OR",  OR);
        keywords.put("NOT", NOT);
        keywords.put("ZA WARUDO!",  BREAK);
    }

    public Scanner(String source) {
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
            case '-': addToken(DASH); break;
            case '+': addToken(PLUS); break;
            case '*': addToken(STAR); break;
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
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                else {
                    addToken(SLASH);
                }
                break;
            case '"': string(); break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                addToken(NEWLINE);
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
        String text = source.substring(start, current);
        TokenType type = null;

        for ( String key : keywords.keySet() ) {
            String firstWord = key.split(" ")[0];
            String fullSentence = StringUtils.substring(source, start, start + key.length());
            if (text.equals(firstWord) && key.equals(fullSentence)) {
                type = keywords.get(key);
                current = key.length() + start;
                break;
            }
        }

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