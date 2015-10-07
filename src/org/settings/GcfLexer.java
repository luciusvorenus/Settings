/*
  Settings 
  Copyright 2015 micama

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.settings;

import java.io.File;

/**
 * Lexer implementation class.
 * This class extends the abstract <code>Lexer</code> class and
 * implements its <code>nextToken</code> method, to 
 * tokenize the string gcf file format.
 */
class GcfLexer extends Lexer {
    
    /* flag when token is a global key */
    private boolean parsingGlobalKeys = true;
    
    /* flag when value is a global var */
    private boolean parsingGlobalVar = false;
    
    /* flag when token is the group name */
    private boolean parsingGroupName = false;
    
    /* flag when token is a key */
    private boolean parsingKey       = false;
    
    /* flag when token is a value */
    private boolean parsingValue     = false;

    /**
     * Create a Lexer to tokenize the gcf file format.
     * @param file the file whose content shall be tokenizied
     */
    GcfLexer(final File file) {
        super(file);
    }

    /**
     * Gets the next token from the stream of characters.
     * Comments and spaces are ignored.<br>
     * The boolean flags are intended to help the lexer
     * recognize with tokentype it is handling
     * at a given moment. For example a groupname and a key
     * are both simply text but have different tokentypes.
     * @return the next token
     */
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
                    parsingGlobalKeys = false; // global keys must come before any group
                    parsingGroupName = true;
                    parsingKey = false;
                    parsingValue = false;
                    return new Token(TokenType.GROUP_LBRACE, "[", lineNumber);
                case ']': 
                    consume(); 
                    parsingGroupName = false;
                    parsingKey = true;
                    parsingValue = false;
                    return new Token(TokenType.GROUP_RBRACE, "]", lineNumber);
                case '/':
                    consume();
                    return new Token(TokenType.GROUP_FSLASH, "/", lineNumber);
                case '=':
                    consume();
                    parsingKey = false;
                    parsingValue = true;
                    return new Token(TokenType.EQUAL_SIGN, "=", lineNumber);
                case '$':
                    consume();
                    return new Token(TokenType.GLOBAR_VAR_SYMBOL, "$", lineNumber);
                case '{':
                    consume();
                    parsingGlobalVar = true;
                    return new Token(TokenType.GLOBAL_VAR_LBRACE, "{", lineNumber);
                case '}':
                    consume();
                    parsingKey = true;
                    parsingValue = false;
                    parsingGlobalVar = false;
                    return new Token(TokenType.GLOBAL_VAR_RBRACE, "}", lineNumber);
                default:
                    // Check if it is a letter, start of string or start of number
                    if (Character.isLetter(c) || c =='"' || isNumber(c) || c == '-' || c == '+') {
                        if (parsingGroupName)  return new Token(TokenType.GROUP_NAME,groupnameText(), lineNumber);
                        else if (parsingKey)   return new Token(TokenType.KEY, keyText(), lineNumber);
                        else if (parsingGlobalVar) return new Token(TokenType.GLOBAL_VAR_NAME, globalVarText(), lineNumber);
                        else if (parsingValue) {
                            parsingKey = true;
                            parsingValue = false;
                            return value(c);
                        }
                        else if (parsingGlobalKeys) return new Token(TokenType.KEY, keyText(), lineNumber);
                    }
                    throw new GcfException("invalid character while parsing: \'"+c+"\' at line "+lineNumber);
            }
        }
        return new Token(TokenType.EOF, "EOF", lineNumber);
    }
    
    /**
     * Consumes all subsquent whitespaces.
     */
    private void ws() {
        while(isWhitespace(c)) {
            consume();
        }
    }
    
    /**
     * Consumes all text that is part of a comment.
     * A comment cannot be multiline, so this method 
     * consumes everthing right of the comment character until
     * the end of the line.
     */
    private void comment() {
        while(c != '\n') {
            consume();
        }
    }
    
    /**
     * Constructs the text for name of a group.
     * This method only gets used when the appropriate groupname flag 
     * has been set to true.
     * @return the groupname text
     */
    private String groupnameText() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(c);
            consume();
        } while(Character.isLetter(c) || isNumber(c) || c=='_' || c =='-');
        return sb.toString();
    }
    
    /**
     * Constructs the text for a key.
     * This method only gets used when the appropriate key flag
     * is set to true.
     * @return the key text
     */
    private String keyText() {
        if (Character.isLetter(c) == false) {
            throw new GcfException("key must start with a letter, found \'"+c+"\' at line "+lineNumber);
        }
        
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(c);
            consume();
        } while(Character.isLetter(c) || isNumber(c) || c=='_');
        return sb.toString();
    }
    
    /**
     * Checks if a specific character is a number.
     * @param ch the character to be checked.
     * @return true if character is a number, false otherwise.
     */
    private boolean isNumber(final char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    /**
     * Gets the token for a key value.
     * The value can a number, a boolean or a string.
     * For each one of these possibilities, there is 
     * a separate method to parse the respective text.
     * @param ch current char in the input stream
     * @return the token representing the value.
     */
    private Token value(final char ch) {
        String valueStr = "";
        if (isNumber(ch) || ch=='-' || ch=='+') {
            valueStr = numberValue(ch);
        }
        else if (c == '\"') {
            valueStr = stringValue(ch);
        }
        else {
            valueStr = booleanValue();
        }
        return new Token(TokenType.VALUE,valueStr,lineNumber);
    }
    
    /**
     * Parses the text for a numeric value.
     * Number can be an integer or a floating point, i.e.
     * containing a dot.
     * @param ch current char in the input stream
     * @return text of the numeric value
     */
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
    
    /**
     * Parses the text of a string value.
     * A string must be enclosed in double quotes.
     * Otherwise an exception is thrown.
     * The string text is parsed containing the double quotes
     * because those are important later when parsing the file, i.e.
     * to distinguish which class represents the value.
     * @param ch current char in the input stream
     * @return text of the string value with double quotes
     */
    private String stringValue(final char ch) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ch);
        
        consume();
        while(c != '\"') {
            sb.append(c);
            
            if (c == '\n') {
                throw new GcfException("string not correctly closed at line "+lineNumber);
            }
            
            consume();
        }
        sb.append(c);
        consume();

        return sb.toString();
    }
    
    /**
     * Parses the text for a boolean value.
     * A boolean value can only be "true" or "false".
     * In this application we also accept "True", "TRUE" and 
     * "False", "FALSE".
     * Here the value is parsed without checking the correct 
     * content of the text value. It just accepts letters.
     * The check wether the value is correct or not occurs 
     * during the parsing of the file.
     * @return text of a boolean value
     */
    private String booleanValue() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(c);
            consume();
        } while(Character.isLetter(c));
        return sb.toString();
    }
    
    /**
     * Parses the text of global variable.
     * It parses until if finds the symbol '}'.
     * @return the name of the variable
     */
    private String globalVarText() {
        final StringBuilder sb = new StringBuilder();
        do {
            if (!Character.isLetter(c) && !isNumber(c) && c!='_') {
                throw new GcfException("global variable can only contain letters, numbers and \'_\', found "+c+ " at line "+lineNumber);
            }
            if (c == '}') {
                break;
            }
            sb.append(c);
            consume();
            
        } while(Character.isLetter(c) || isNumber(c) || c=='_');
        return sb.toString();
    }

}
