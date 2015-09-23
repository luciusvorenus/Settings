package org.settings;

/**
 * Defines a token in the gcf file format.
 * A token contains a type and a respective text.
 * The text can be the token symbol only, e.g. '['
 * or any text, e.g. the text in the groupname.
 * 
 */
class Token {
    
    private final TokenType type;
    private final String    text;
    
    public Token(final TokenType type, final String text) {
        this.type = type;
        this.text = text;
    }
    
    TokenType getType() {return this.type;}
    String    getText() {return this.text;} 

    @Override
    public String toString() {
//        return "<'"+this.text+"',"+this.type.getLongName()+">";
        return "<"+this.type.getLongName()+",\'"+this.text+"\'>";
    }
}
