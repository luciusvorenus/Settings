package org.settings;

/**
 * Utilities class for the Settings classes.
 * @author Miguel Cardoso Martins
 */
class GcfUtils {
    
    // Private method: no instantiation
    private GcfUtils() {
        
    }
    
    /* Defintion of the major syntax characters */
    public static char COMMENT_CHAR      = '#';
    public static char GROUP_LBRACE      = '[';
    public static char GROUP_RBRACE      = ']';
    public static char GROUP_END_CHAR    = '/';
    public static char GLOBAL_VAR_CHAR   = '$';
    public static char GLOBAL_VAR_LBRACE = '{';
    public static char GLOBAL_VAR_RBRACE = '}';

    static boolean keyContainsIllegalCharacter(final String text) {
        return text.contains(" ")                  ||
               text.contains("\"")                 ||
               text.contains("=")                  ||
               text.contains(""+COMMENT_CHAR)      ||
               text.contains(""+GLOBAL_VAR_CHAR)   ||
               text.contains(""+GLOBAL_VAR_LBRACE) ||
               text.contains(""+GLOBAL_VAR_RBRACE) ||
               text.contains(""+GROUP_END_CHAR)    ||
               text.contains(""+GROUP_LBRACE)      ||
               text.contains(""+GROUP_RBRACE);
    }
    
    static boolean valueContainsIllegalCharacter(final String text) {
        return text.contains("#");
    }
}
