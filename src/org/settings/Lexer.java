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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Lexer abstract class.
 * Contains support code to turn a stream characters into 
 * a stream of tokens.
 * It offers an abstract method <code>nextToken</code>, that shall
 * be implemented in the implementation class and offers an enumeration 
 * style of retrieving tokens.
 */
abstract class Lexer {
    
    /* Character denoting the end of file (as defined in java.io) */
   final char EOF = (char)-1;
    
    /* The text input to be splitted into tokens */
    private final String input;
    
    /* Current character pointer into the input text */
    private int p = 0;
    
    /* Current character in the input text */
    char c;
    
    int lineNumber;

    /**
     * Construct a lexer
     * @param file the file to be read
     */
    Lexer(final File file) {
        this.input = readFileContent(file);
        c = input.charAt(p);
        lineNumber = 1;
    }
    
    /**
     * Reads the file content.
     * It keeps the newline characters.
     * @param file the file to be processed
     * @return file content as a string
     */
    private String readFileContent(final File file) throws GcfException {
        final StringBuilder sb = new StringBuilder();
        try(final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while(reader.ready()) {
                sb.append(reader.readLine()).append("\n");
            }
        } catch(FileNotFoundException ex) {
            throw new GcfException("cannot find file to read: "+file.getAbsolutePath());
        } catch(IOException ex) {
            throw new GcfException("IO problem occurred while reading "+file.getAbsolutePath());
        }
        return sb.toString();
    }
    
    /**
     * Checks if a character is a whitespace.
     * A Whitespace can be a space, tab, newline or carriage return.
     * @param ch character to be checked
     * @return truw if character is a whitespace, false otherwise
     */
    boolean isWhitespace(final char ch) {
        return ch==' ' || ch=='\t' || ch=='\n' || ch=='\r';
    }
    
    /**
     * Moves the character pointer one place further and 
     * sets the current character.
     * If the pointer exceeds the length of the text, 
     * the current character is set to the EOF character,
     * so as to terminate the execution.
     */
    void consume() {
        if (c=='\n') lineNumber++;
        p++;
        if (p >= input.length()) c = EOF;
        else c = input.charAt(p);
    }
    
    /**
     * Gets the next token in the input text.
     * This method shall be implemented in the 
     * implementation class.
     * It facilitates an enumeration style processing of tokens.
     * @return the next token
     */
    abstract Token nextToken();
}
