package org.settings;

/**
 *
 * @author Miguel Cardoso Martins
 */
public class Token {
    
    private final TokenType type;
    private final String    text;

    public Token(final TokenType type, final String text) {
        this.type = type;
        this.text = text;
    }
    
    TokenType getType() {
        return this.type;
    }
    
    String getText() {
        return this.text;
    } 

    @Override
    public String toString() {
        return "<'"+this.text+"',"+this.type.getCategory()+">";
    }
    
}
