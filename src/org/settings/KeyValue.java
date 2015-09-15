package org.settings;

/**
 * Defines a key-value pair in the configuration file.
 * A key-value in the gcf configuration format is composed
 * of a <code>String</code> (key) and an <code>Object</code> (value).
 * The reason for the value to be an <code>Object</code> is that
 * the value can be a number, a boolean or a string, and we'd
 * like to infer the value's type later in the code, e.g.
 * <pre>
 *     Object value = ...; // some value
 *     if (value instanceof String) {
 *         // do something as a string
 *     }
 * </pre>
 * @author Miguel Cardoso Martins
 */
final class KeyValue extends Element {
    
    /* The key */
    private String key;
    
    /* The value */
    private Object value;
    
    /* Reference to the data buffer */
    private final Buffer buffer;
    
    
    /**
     * Creates an instance of a KeyValue.
     * This special constructor is only called during parsing.
     * @param tokener reference to the file stream tokener
     * @param buffer reference to the data buffer
     * @throws  GcfException
     */
    /*package-privat*/ KeyValue(final Parser parser,final Buffer buffer) throws GcfException {
        this.buffer = buffer;
        parse(parser);
    }
    
    /**
     * Gets the key of this KeyValue
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Gets the value of this KeyValue
     * @return the value
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Sets the value for this KeyValue
     * @param value the new value
     */
    public void setValue(final Object value) {
        this.value = value;
    }
    
    /**
     * Parses the KeyValue components.
     * Comments are ignored, both above and to the right of the KeyValue.
     * @param tokener reference to the file stream tokener
     * @throws GcfException 
     */
    @Override
    /*package-privat*/ void parse(final Parser parser) throws GcfException {
        final String keyStr = parser.match(TokenType.KEY);
        parser.match(TokenType.EQUAL_SIGN);
        final String valueStr = parser.match(TokenType.VALUE);
        
        this.key = keyStr;
        this.value = parseValue(valueStr);
    }
    
    /**
     * Parses the value of this KeyValue into an Object.
     * @param tok reference to the file stream tokener
     * @param strValue the value of this KeyValue as a string
     * @return the parsed value as an <code>Object</code>
     * @throws GcfException
     */
    /*package-privat*/ Object parseValue(final String strValue) throws GcfException {
        Object obj = null;
        try {
            obj = Integer.parseInt(strValue);
        } catch(NumberFormatException ex1) {
            try {
                obj = Long.parseLong(strValue);
            } catch(NumberFormatException ex2) {
                try {
                    obj = Double.parseDouble(strValue);
                } catch(NumberFormatException ex3) {
                    try {
                        obj = parseBooleanValue(strValue);
                    } catch(NumberFormatException ex4) {
                        if (strValue.length()>0) {
                            if (!strValue.startsWith("\"") || !strValue.endsWith("\"")) {
                                throw new GcfException("mal formed string");
                            }
                            obj = strValue.substring(1, strValue.length()-1).trim();
                        }
                        else {
                            obj = "";
                        }
                    }
                }
            }
        }
        return obj;
    }
    
    /**
     * Parses a boolean value.
     * Parsing a boolean is not as straight forward as it may seems.
     * One cannot simply use <code>Boolean.parseBoolean</code>, since 
     * every value that isn't a boolean, e.g. "abc", still would get 
     * parsed a false. In this however we want an exception to 
     * be raised.
     * Therefore this method checks first if the string value in its
     * lower case form is indeed a boolean and then parses it.
     * Otherwise it raises an exception.
     * @param value the string value
     * @return the parsed boolean value
     * @throws GcfException 
     */
    /*package-privat*/ boolean parseBooleanValue(final String value) throws GcfException {
        // cannot check for NumberFormatException because every value
        // that isn't a boolean still gets parsed to false.
        final String strValue = value.toLowerCase();
        boolean boolValue = false;
        if (strValue.equals("true") || strValue.equals("false")) {
            boolValue = Boolean.parseBoolean(strValue);
        }
        else {
            throw new NumberFormatException(value + " cannot be parsed as boolean");
        }
        return boolValue;
    }
    
}

