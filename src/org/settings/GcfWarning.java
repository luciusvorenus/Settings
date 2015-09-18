package org.settings;

/**
 * Defines a warning to be used when something related 
 * to the use of the settings classes is not quite wrong to 
 * be an exception but could eventually be done in a better way.
 * Also for potentially supsicious code.
 */
class GcfWarning {
    
    public static void printWarning(final String text) {
        System.err.println("*** GcfWarning: " + text + " ***");
    }
    
}
