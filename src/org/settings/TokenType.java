package org.settings;

/**
 *
 * @author Miguel Cardoso Martins
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
    
    
    private final String category;
    TokenType(final String category) {
        this.category = category;
    }
    
    public String getCategory() {
        return this.category;
    }
    
}
