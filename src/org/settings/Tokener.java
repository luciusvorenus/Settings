package org.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

/**
 * Helper class to retrieve tokens from a configuration files's char stream.
 * From a stream of characters, this class offers handy methods to 
 * retrieve whole tokens, which makes parsing of the file easier and 
 * more readable.
 * @author Miguel Martins
 */
final class Tokener {

    /* Reference to the character stream */
    private final InputStream stream;
    
    /* The previously read character */
    private char previous;
    
    /* Flag on wether to use the previous saved character as the next character */
    private boolean usePrevious;
    
    /* The current line number in the file */
    private int lineNumber ;

    /**
     * Creates an instance of the tokener and initializes state variables
     * @param stream the input stream for the configuration file
     */
    /*package-privat*/ Tokener(final InputStream stream) {
        this.stream = stream;
        this.previous = 0;
        this.usePrevious = false;
        this.lineNumber = 1;
    }

    /**
     * Reads a character from the file stream.
     * Increments the linenumber if the read character is a line break.
     * @return the read character.
     * @throws GcfException
     */
    private char readCharFromStream() throws GcfException {
        char c = 0;
        try {
            c = (char)this.stream.read();
        } catch(IOException e) {
            throw new GcfException("cannot read more chars from stream");
        }
        
        if (c == '\n') {
            this.lineNumber++;
        }
        
        return c;
    }
    
    /**
     * Checks if the end of file has been reached.
     * It accomplishes this by checking the number of bytes still
     * available to be read in the stream.
     * @return true if end of file has been reached, false otherwise
     */
    /*package-privat*/ boolean eof() {
        boolean isEOF = true;
        try {
            isEOF = this.stream.available() <= 0;
        } catch(IOException e) {
        }
        return isEOF;
    }

    /**
     * Gets the next character in the stream that obeys a certain predicate.
     * @param predicate the predicate to filter the character
     * @return the first occurence that obeys the predicate or 0 if no character is found
     * @throws GcfException
     */
    /*package-privat*/ char nextWithPredicate(final Predicate<Character> predicate) throws GcfException {
        char c = 0;
        while(this.eof() == false) {
            c = readCharFromStream();
            if (predicate.test(c)) {
                break;
            }
        }
        
        this.previous = c;
        this.usePrevious = false;
        
        return c;
    }

    /**
     * Gets the next character in the stream.
     * The character returned by this method depends on the use 
     * of the <code>back</code> method, in which case the 
     * <code>next</code> method returns the current character again.
     * If there was no backing up in the stream the this method 
     * returns indeed the next character in the stream.
     * @return the next character in the stream or the previous character
     * @throws GcfException
     */
    /*package-privat*/ char next() throws GcfException {
        char c = 0;
        if (this.usePrevious == true) {
            c = this.previous;
            this.usePrevious = false;
        }
        else {
            c = nextWithPredicate(ch -> true);
        }
        return c;
    }

    /**
     * Gets the next character in the stream that is not empty.
     * Empty characters are all that dont' represent a number, 
     * letter, punctuation or any kind of symbol. That includes 
     * space, tab, linebreak and carriage return.
     * @return next non-empty character in the stream
     * @throws GcfException 
     */
    /*package-privat*/ char nextNonEmpty() throws GcfException {
        return nextWithPredicate(c -> c!=' ' && c!='\t' && c!='\n' && c!='\r');
    }

    /**
     * Creates a string of characters until a specific character is encountered.
     * All characters are appended together (including empty characters)
     * to create the string.
     * @param delimiter the character to stop
     * @return the string created 
     * @throws GcfException
     */
    /*package-privat*/ String nextUntil(final char delimiter) throws GcfException {
        final StringBuilder sb = new StringBuilder();
        char c = 0;
        while((c=next()) != delimiter) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Gets the string of characters until the end of the line is reached
     * @return the string created
     * @throws GcfException 
     */
    /*package-privat*/ String nextUntilEndOfLine() throws GcfException {
        return nextUntil('\n');
    }
    
    /**
     * Backs up one character in the stream.
     * This method can only be called once consecutively. 
     * Otherwise it throws a <code>GcfException</code>.
     * This method affects the <code>next</code> method.
     * If this method is called, the next method will 
     * return the backed up character.
     * @throws GcfException
     */
    /*package-privat*/ void back() throws GcfException {
        if (this.previous == 0) {
            throw new GcfException("no characters available to backup");
        }
        if (this.usePrevious == true) {
            throw new GcfException("cannot backup 2 characters");
        }

        this.usePrevious = true;
    }

    /**
     * Gets the current linenumber in the file stream.
     * @return the line number
     */
    /*package-privat*/ int lineNumber() {
        return this.lineNumber;
    }
}
