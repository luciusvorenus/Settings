package org.settings;

/**
 * Definition of all important tokens in the gcf file format.
 */
enum TokenType {
    
    EOF("EOF","EOF"),
    
    GROUP_LBRACE("GROUP_LBRACE","["),
    
    GROUP_RBRACE("GROUP_RBRACE","]"),
    
    GROUP_NAME("GROUP_NAME","<groupname>"),
    
    GROUP_FSLASH("GROUP_FSLASH","/"),
    
    KEY("KEY","<key>"),
    
    EQUAL_SIGN("EQUAL_SIGN","="),
    
    VALUE("VALUE","<value>"),
    
    GLOBAR_VAR_SYMBOL("GLOBAL_VAR","$"),
    
    GLOBAL_VAR_RBRACE("GLOBAL_VAR_RBRACE","{"),
    
    GLOBAL_VAR_LBRACE("GLOBAL_VAR_LBRACE","}"),
    
    GLOBAL_VAR_NAME("GLOBAL_VAR_NAME","<global_var_name>");
    
    
    private final String longName;
    private final String symbol;
    
    TokenType(final String category, final String symbol) {
        this.longName = category;
        this.symbol = symbol;
    }
    
    String getLongName() {
        return this.longName;
    }

    String getSymbol() {
        return this.symbol;
    }

    @Override
    public String toString() {
        return "<"+longName + ",\'" + symbol + "\'>";
    }
    
}
