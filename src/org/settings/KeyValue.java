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
    /*package-privat*/ KeyValue(final Tokener tokener,final Buffer buffer) throws GcfException {
        this.buffer = buffer;
        parse(tokener);
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
    /*package-privat*/ void parse(final Tokener tokener) throws GcfException {
        tokener.back(); // set tokener to before the first key character
        
        final String line = tokener.nextUntilEndOfLine();
        
        if (line.contains("=") == false) {
            throw new GcfException("key/value syntax: \"key = some_value\"");
        }
        
        final String[] parts = line.split("=");
        if (parts.length != 2) {
            throw new GcfException("key/value syntax: \"key = some_value\"");
        }
        
        this.key = parts[0].trim();
        if (GcfUtils.keyContainsIllegalCharacter(this.key)) {
            throw new GcfException("illegal character in key at line " + (tokener.lineNumber()-1));
        }
        
        final String valuePart = parts[1].trim();
        if (valuePart.startsWith(""+GcfUtils.GLOBAL_VAR_CHAR)) {
            final String str = valuePart.substring(1);
            if (!str.startsWith(""+GcfUtils.GLOBAL_VAR_LBRACE) || !str.endsWith(""+GcfUtils.GLOBAL_VAR_RBRACE)) {
                throw new GcfException("wrong syntax in global key reference at line "+(tokener.lineNumber()-1));
            }
            
            final String globalKey = str.substring(1, str.length()-1).trim();
            this.value = this.buffer.getGlobalValue(globalKey);
        }
        else {
            final int indexOfComment = valuePart.indexOf(GcfUtils.COMMENT_CHAR);
            final String realValue = (indexOfComment!=-1) ? valuePart.substring(0, indexOfComment).trim() : valuePart;
            if (GcfUtils.valueContainsIllegalCharacter(realValue)) {
                throw new GcfException("illegal character in value in line at "+(tokener.lineNumber()-1));
            }
            
            this.value = parseValue(tokener,realValue);
        }
    }
    
    /**
     * Parses the value of this KeyValue into an Object.
     * @param tok reference to the file stream tokener
     * @param strValue the value of this KeyValue as a string
     * @return the parsed value as an <code>Object</code>
     * @throws GcfException
     */
    /*package-privat*/ Object parseValue(final Tokener tok,final String strValue) throws GcfException {
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
                                throw new GcfException("mal formed string at line "+(tok.lineNumber()-1));
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

