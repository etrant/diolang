package diolang;

import diolang.lexer.Scanner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Dio {
    static boolean hadError = false;
    static List<Token> tempTokens = null;
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: dio [script]");
            System.exit(64);
        }
        runFile(args[0]);
    }
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
    }
    public static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        tempTokens = tokens;

        // TODO: For now, just print the tokens.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}