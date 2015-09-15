package org.settings;

/**
 *
 * @author Miguel Cardoso Martins
 */
public class Token {
    
    final TokenType type;
    final String    text;

    public Token(final TokenType type, final String text) {
        this.type = type;
        this.text = text;
    }

    @Override
    public String toString() {
        return "<'"+this.text+"',"+this.type.getCategory()+">";
    }
    
}
