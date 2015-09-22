package org.settings;

/**
 * Defines a LL(k) parser, with k>=1.
 * This class defines generic code for a LL(k) parser 
 * such as retrieving and consuming tokens from the lexer, 
 * and saving lookahead tokens in a token array.
 * The token buffer saves the lookahead tokens in a circular manner,
 * i.e. the buffer has a fixed size, and once a token has been consumed 
 * it is replaced by the next token from the lexer.
 * This class offers no parsing logic, since it does not use 
 * the match method.
 * The logic for parsing the gcf file syntax shall be implemented 
 * in a derivate of this class.
 */
class Parser {
    
    /* Reference to the lexer */
    private final Lexer lexer;
    
    /* Size of the lookahead token buffer */
    private final int bufferSize;
    
    /* Array to hold lookahead tokens */
    private final Token[] buffer;
    
    /* Index of current lookahead token in the buffer array */
    private int p;
    
    /* Current lookahead token */
    Token lookahead;

    /**
     * Create an instance of Parser.
     * @param lexer reference to the lexer
     * @param bufferSize number of lokkahead tokens
     */
    Parser(final Lexer lexer,final int bufferSize) {
        this.lexer = lexer;
        this.bufferSize = bufferSize;
        this.buffer = new Token[bufferSize];
        for(int i=0; i<bufferSize; i++) this.buffer[i] = lexer.nextToken();
        p = 0;
        lookahead = this.buffer[p];
    }

    /**
     * Consumes a token from the lexer.
     * By consuming a token, the token at the current lookahead 
     * index is replaced with the next token from the lexer.
     * The lookahead index is moved one place forward.
     * This is done in a circular manner.
     */
    private void consume() {
        this.buffer[p] = lexer.nextToken();
        p = (p+1) % bufferSize;
        lookahead = this.buffer[p];
    }
    
    /**
     * Checkes if the passed token type matches the current lookahead.
     * If the check is successfull the token text (text in the gcf file)
     * is extracted and the current lookahead token is consumed.
     * @param type token type to be checked
     * @return true if the token types match, false otherwise
     */
    String match(final TokenType type) {
        if (type.equals(lookahead.getType())) {
            final String text = lookahead.getText();
            consume();
            return text;
        }
        
        throw new GcfException("expecting "+type+"; found \'"+lookahead.getText()+"\'");
    }
    
    /**
     * Retrieves a lookahead token for a specific index.
     * The index shall be at maximum bufferSize.
     * For convinience, the first lookahead token has index 1.
     * @param i the index of the lookahead token
     * @return the lookahead token at the specified index
     */
    Token LT(final int i) {
        return this.buffer[(p+i-1)%bufferSize];
    }
}
