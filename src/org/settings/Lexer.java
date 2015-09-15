package org.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Miguel Cardoso Martins
 */
abstract class Lexer {
    
    public static char EOF = (char)-1;
    public static final int EOF_TYPE = 1;
    private final String input;
    private int p = 0;
    char c;

    public Lexer(final File file) {
        this.input = readFileContent(file);
        c = input.charAt(p);
    }
    
    private String readFileContent(final File file) {
        final StringBuilder sb = new StringBuilder();
        try(final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while(reader.ready()) {
                sb.append(reader.readLine()).append("\n");
            }
        } catch(FileNotFoundException ex) {
            throw new GcfException("cannot find file to read");
        } catch(IOException ex) {
            throw new GcfException("IO problem occurred");
        }
        return sb.toString();
    }
    
    boolean isWhitespace(final char ch) {
        return ch==' ' || ch=='\t' || ch=='\n' || ch=='\r';
    }
    
    void ws() {
        while(isWhitespace(c)) {
            consume();
        }
    }
    
    void comment() {
        while(c != '\n') {
            consume();
        }
    }
    
    void consume() {
        p++;
        if (p >= input.length()) c = EOF;
        else c = input.charAt(p);
    }
    
    abstract Token nextToken();
    abstract String getTokenName(final TokenType type);
}
