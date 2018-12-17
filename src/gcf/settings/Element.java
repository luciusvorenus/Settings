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
 * Defines an element of the configuration file.
 * An element can be a KeyValue, a Group or any other 
 * construct.
 * This abstract class defines basic fields and methods.
 */
abstract class Element {
    
    /* The name of the element */
    String name;
    
    /* The parent group of this element */
    String parent;
    
    /* The absolute path of this element */
    String path;
    
    /**
     * Abstract method to be implemented by specific elements.
     * All code regarding the parsing of one element's content 
     * should be placed here.
     * @param tokener reference to the file stream tokener
     */
    abstract void parse(final Parser parser);
    
    /**
     * Gets the element's name.
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Gets the element's path.
     * @return the path
     */
    public String getPath() {
        return this.path;
    }
    
    /**
     * Gets the element's parent.
     * @return the parent
     */
    public String getParent() {
        return this.parent;
    }
}
