/*
  Settings 
  Copyright 2015 micama

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.settings;

import java.util.Objects;

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
 */
final class KeyValue extends Element {
    
    /* Reference to the data buffer */
    private final Buffer buffer;
    
    /* The key */
    private String key;
    
    /* The value */
    private Object value;
    
    
    /**
     * Creates an instance of a KeyValue.
     * This special constructor is only called during parsing.
     * @param tokener reference to the file stream tokener
     * @param buffer reference to the data buffer
     * @throws  GcfException
     */
    /*package-privat*/ KeyValue(final String parent, final Buffer buffer, final Parser parser) throws GcfException {
        this.parent = parent;
        this.path = parent;
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
     * @param parser reference
     * @throws GcfException 
     */
    @Override
    /*package-privat*/ void parse(final Parser parser) throws GcfException {
        this.key = parser.match(TokenType.KEY);
        parser.match(TokenType.EQUAL_SIGN);
        
        if (parser.lookahead.getType().equals(TokenType.GLOBAR_VAR_SYMBOL)) {
            globalVar(parser);
        }
        else {
            normalValue(parser);
        }
        
        this.name = this.key;
    }
    
    /**
     * Parses the value in case it is a global variable reference.
     * @param parser reference to the parser
     */
    private void globalVar(final Parser parser) {
        parser.match(TokenType.GLOBAR_VAR_SYMBOL);
        parser.match(TokenType.GLOBAL_VAR_LBRACE);
        final String globalKey = parser.match(TokenType.GLOBAL_VAR_NAME);
        this.value = this.buffer.getGlobalValue(globalKey);
        parser.match(TokenType.GLOBAL_VAR_RBRACE);
    }
    
    /**
     * Parses the value in case it is a normal value.
     * A normal value is number, a string or a boolean.
     * @param parser reference to the parser
     */
    private void normalValue(final Parser parser) {
        final String valueStr = parser.match(TokenType.VALUE);
        this.value = parseValue(valueStr,parser.lookahead.getLineNumber());
    }
    
    /**
     * Parses the value of this KeyValue into an Object.
     * @param tok reference to the file stream tokener
     * @param strValue the value of this KeyValue as a string
     * @return the parsed value as an <code>Object</code>
     * @throws GcfException
     */
    /*package-privat*/ Object parseValue(final String strValue, final int lineNumber) throws GcfException {
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
                                throw new GcfException("mal formed string at line "+(lineNumber-1));
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final KeyValue other = (KeyValue) obj;
        return this.key.equals(other.key);
    }
    
}

