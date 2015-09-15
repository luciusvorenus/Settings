package org.settings;

/**
 *
 * @author Miguel Cardoso Martins
 */
abstract class Parser {
    
    private final Lexer lexer;
    private final int bufferSize;
    private final Token[] buffer;
    private int p;
    Token lookahead;

    Parser(final Lexer lexer,final int bufferSize) {
        this.lexer = lexer;
        this.bufferSize = bufferSize;
        this.buffer = new Token[bufferSize];
        for(int i=0; i<bufferSize; i++) this.buffer[i] = lexer.nextToken();
        p = 0;
        lookahead = this.buffer[p];
    }

    private void consume() {
        this.buffer[p] = lexer.nextToken();
        p = (p+1) % bufferSize;
        lookahead = this.buffer[p];
    }
    
    String match(final TokenType type) {
        if (type.equals(lookahead.type)) {
            final String text = lookahead.text;
            consume();
            return text;
        }
        
        throw new GcfException("expecting "+lexer.getTokenName(type)+"; found "+lookahead);
    }
    
    Token LT(final int i) {
        return this.buffer[(p+i-1)%bufferSize];
    }
}
