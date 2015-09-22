package org.settings;

/**
 * Definition of all important tokens in the gcf file format.
 */
public enum TokenType {
    
    EOF("EOF","EOF"),
    
    GROUP_LBRACE("GROUP_LBRACE","["),
    
    GROUP_RBRACE("GROUP_RBRACE","]"),
    
    GROUP_NAME("GROUP_NAME","<groupname>"),
    
    GROUP_FSLASH("GROUP_FSLASH","/"),
    
    KEY("KEY","<key>"),
    
    EQUAL_SIGN("EQUAL_SIGN","="),
    
    VALUE("VALUE","<value>");
    
    
    private final String longName;
    private final String symbol;
    TokenType(final String category, final String symbol) {
        this.longName = category;
        this.symbol = symbol;
    }
    
    public String getLongName() {
        return this.longName;
    }

    public String getSymbol() {
        return this.symbol;
    }

    @Override
    public String toString() {
        return "<"+longName + ",\'" + symbol + "\'>";
    }
    
}
