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
package gcf.settings;

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
    private final int       lineNumber;
    
    Token(final TokenType type, final String text, final int lineNumber) {
        this.type = type;
        this.text = text;
        this.lineNumber = lineNumber;
    }
    
    TokenType getType() {return this.type;}
    String    getText() {return this.text;} 
    int       getLineNumber() {return this.lineNumber;}

    @Override
    public String toString() {
        return "<"+this.type.getLongName()+",\'"+this.text+"\'>";
    }
}
