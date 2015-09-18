package org.settings;

/**
 * Definition of all important tokens in the gcf file format.
 */
public enum TokenType {
    
    EOF("EOF"),
    
    GROUP_LBRACE("GROUP_LBRACE"),
    
    GROUP_RBRACE("GROUP_RBRACE"),
    
    GROUP_NAME("GROUP_NAME"),
    
    GROUP_FSLASH("GROUP_FSLASH"),
    
    KEY("KEY"),
    
    EQUAL_SIGN("EQUAL_SIGN"),
    
    VALUE("VALUE");
    
    
    private final String longName;
    TokenType(final String category) {
        this.longName = category;
    }
    
    public String getLongName() {
        return this.longName;
    }
    
}
