package org.settings;

/**
 * Defines an element of the configuration file.
 * An element can be a KeyValue, a Group or any other 
 * construct.
 * This abstract class defines basic fields and methods.
 * @author Miguel Cardoso Martins
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
