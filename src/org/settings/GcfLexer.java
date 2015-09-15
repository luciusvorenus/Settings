package org.settings;

import java.io.File;

/**
 *
 * @author Miguel Cardoso Martins
 */
class GcfLexer extends Lexer {
    
    private boolean parsingGroupName = false;
    private boolean parsingKey       = false;
    private boolean parsingValue     = false;

    public GcfLexer(final File file) {
        super(file);
    }

    @Override
    Token nextToken() {
        while(c != EOF) {
            switch(c) {
                case ' ':
                case '\t': 
                case '\n': 
                case '\r': 
                    ws(); // skip all whitespaces
                    continue;
                case '#':
                    comment(); // skip all characters until end of line (\n)
                    continue;
                case '[': 
                    consume(); 
                    parsingGroupName = true;
                    parsingKey = false;
                    parsingValue = false;
                    return new Token(TokenType.GROUP_LBRACE, "[");
                case ']': 
                    consume(); 
                    parsingGroupName = false;
                    parsingKey = true;
                    parsingValue = false;
                    return new Token(TokenType.GROUP_RBRACE, "]");
                case '/':
                    consume();
                    return new Token(TokenType.GROUP_FSLASH, "/");
                case '=':
                    consume();
                    parsingKey = false;
                    parsingValue = true;
                    return new Token(TokenType.EQUAL_SIGN, "=");
                default:
                    if (Character.isLetter(c)|| isNumber(c) || c =='"') {
                        if (parsingGroupName)  return new Token(TokenType.GROUP_NAME,groupnameText());
                        else if (parsingKey)   return new Token(TokenType.KEY, keyText());
                        else if (parsingValue) {
                            parsingKey = true;
                            parsingValue = false;
                            return value(c);
                        }
                    }
                    throw new Error("invalid character \'"+c+"\'");
            }
        }
        return new Token(TokenType.EOF, "EOF");
    }
    
    private String groupnameText() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(c);
            consume();
        } while(Character.isLetter(c) || isNumber(c) || c=='_' || c =='-');
        return sb.toString();
    }
    
    private String keyText() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(c);
            consume();
        } while(Character.isLetter(c) || isNumber(c) || c=='_');
        return sb.toString();
    }
    
    private boolean isNumber(final char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    private Token value(final char ch) {
        String valueStr = "";
        if (isNumber(ch)) {
            valueStr = numberValue(ch);
        }
        else if (c == '\"') {
            valueStr = stringValue(ch);
        }
        else {
            valueStr = booleanValue();
        }
        return new Token(TokenType.VALUE,valueStr);
    }
    
    private String numberValue(final char ch) {
        StringBuilder sb = new StringBuilder();
        sb.append(ch);
        consume();
        while(isNumber(c) || c=='.') {
            sb.append(c);
            consume();
        }
        return sb.toString();
    }
    
    private String stringValue(final char ch) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ch);
        
        consume();
        while(c != '\"') {
            sb.append(c);
            
            if (c == '\n') {
                throw new Error("string not correctly closed");
            }
            
            consume();
        }
        sb.append(c);
        consume();

        return sb.toString();
    }
    
    private String booleanValue() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(c);
            consume();
        } while(Character.isLetter(c));
        return sb.toString();
    }

    @Override
    String getTokenName(final TokenType type) {
        return type.getCategory();
    }

}
