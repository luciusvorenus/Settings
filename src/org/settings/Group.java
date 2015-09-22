package org.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents a group in the gcf file format.
 * A group has a name, and can contain child key/values and 
 * sub groups (which in turn can themselves contain subgroups, etc).
 */
public final class Group extends Element {
    
    /* Holds all keys/values */
    private final Map<String,Object> keys = new LinkedHashMap<>();
    
    /* Reference to the main data container */
    private Buffer buffer;
    
    /* Reference to the group changer */
    private GroupChanger groupChanger;
    
    
    /**
     * Package privat constructor to create a Group while parsing.
     * @param parent the path of the parent group
     * @param parser refrence to the parser
     * @param buffer reference to the data buffer
     * @param groupChanger reference to the group changer
     */
    Group(final String parent, final Parser parser, final Buffer buffer, final GroupChanger groupChanger) {
        this.parent = parent;
        this.buffer = buffer;
        this.groupChanger = groupChanger;
        parse(parser);
    }
    
    /**
     * Creates a group.
     * @param parent the path of the parent, containing, group.
     * @param name the name of the group to be created
     */
    public Group(final String parent, final String name) {
        this.parent = parent;
        this.name   = name;
        this.path   = parent + name + (parent.isEmpty()?"":"/");
    }
    
    /**
     * Utility method to set the buffer when the group is 
     * created using the public method.
     * @param buffer reference to the buffer
     */
    void setBuffer(final Buffer buffer) {
        this.buffer = buffer;
    }
    
    /**
     * Gets all childgroups as an unmodifiable collection.
     * @return the subgroups
     */
    public Collection<Group> childGroups() {
        return this.buffer.subGroupsForPath(this.path);
    }
    
    /**
     * Gets all subkeys as an unmodifiable collection.
     * @return the subkeys
     */
    public Collection<String> childKeys() {
        return Collections.unmodifiableCollection(this.keys.keySet());
    }
    
    /**
     * Checks if a key exists within this group.
     * @param key the key string
     * @return true if the key exists in this group, false otherwise
     */
    public boolean hasKey(final String key) {
        return this.keys.containsKey(key);
    }
    
    /**
     * Adds a key and a value to this group.
     * The value can be any numeric type (that extends from <code>Number</code>).
     * @param <T> 
     * @param key the key string
     * @param value the (unboxed) numeric value
     */
    public final <T extends Number> void addKey(final String key, final T value) {
        addObjKey(key, value);
    }
    
    /**
     * Adds a key and a string value.
     * @param key the key string
     * @param value the value string
     */
    public final void addKey(final String key, final String value) {
        addObjKey(key, value);
    }
    
    /**
     * Adds a key and a boolean value.
     * @param key the key string
     * @param value the boolean value
     */
    public final void addKey(final String key, final boolean value) {
        addObjKey(key, value);
    }
    
    /**
     * Adds a key and a value to the child keys container.
     * @param key the key string
     * @param value the value object
     */
    void addObjKey(final String key, final Object value) {
        this.keys.putIfAbsent(key, value);
    }
    
    /**
     * Deletes the key (and respective value) from this group.
     * The key/value is deleted if the key is present in the group,
     * otherwise nothing happens.
     * @param key the key string
     */
    public void deleteKey(final String key) {
        this.keys.remove(key);
    }
    
    /**
     * Changes the value of an already existing key.
     * If the key does not exist, an exception is thrown.
     * @param key the key string
     * @param value the value as a string
     */
    public void changeValue(final String key, final String value) throws GcfException {
        changeObjValue(key, value);
    }
    
    /**
     * Changes the value of an already existing key.
     * If the key does not exist, an exception is thrown.
     * The value must be any kind of a numeric value, i.e.
     * extends <code>Number</code>.
     * @param <T>
     * @param key the key string
     * @param value the (unboxed) numeric value
     */
    public <T extends Number> void changeValue(final String key, final T value) throws GcfException {
        changeObjValue(key, value);
    }
    
    /**
     * Changes the value of an already existing key.
     * If the key does not exist, an exception is thrown.
     * @param key the key string
     * @param value the value as an Object
     */
    private void changeObjValue(final String key, final Object value) throws GcfException {
        if (hasKey(key) == false) {
            throw new GcfException("cannot change value for key \""+key+"\" in group \""+getPath()+"\". No such key!");
        }
        this.keys.put(key, value);
    }
    
    /**
     * Retrieves a subgroup within this group.
     * If the relative group is not present, an exception is thrown.
     * If the group path passed is absolute, an exception is thrown, 
     * since for retrieving absolute groups the instance of the
     * <code>Settings</code> class and its <code>getGroup</code> method
     * should be used instead.
     * @param groupPath the relative group path
     * @return the group object
     */
    public Group getGroup(final String groupPath) throws GcfException {
        if (this.groupChanger.isPathAbsolute(groupPath)) {
            throw new GcfException("passed absolute group path to retrieve sub group");
        }
        
        return this.groupChanger.changeGroup(groupPath);
    }
    
    /**
     * Adds a a group as a sub group to this group.
     * If the there is already a group with the exact
     * same path within this group, then 
     * the group is not added as a sub group, nor is the 
     * original group overriden.
     * Too keep the internal data buffer in a correct state,
     * one has to add the subgroup to the subgroups of this group,
     * as well as to the data buffer.
     * @param group the group object.
     */
    public void addSubGroup(final Group group) {
        this.buffer.addGroup(group);
    }
    
    /**
     * Add an empty group with the specified name.
     * If the groups path is already present within this 
     * group as a subgroup, then the group is not added
     * as a subgroup, and the original group is not overriden.
     * @param name the name of the group to be created
     */
    public void addSubGroup(final String name) {
        addSubGroup(new Group(this.path, name));
    }
    
    /**
     * Deletes a subgroup by its relative group name.
     * If the group is not present within this group 
     * an exception is thrown.
     * @param groupName the name of the subgroup to be deleted
     */
    public void deleteSubGroup(final String groupName) {
        final String absolutePath = this.path + groupName + "/";
        this.buffer.deleteSubGroup(absolutePath);
    }
    
    /**
     * Reads a value by its key string.
     * If the key is not present within this group, 
     * an exception is thrown.
     * @param key the key string
     * @return the value as a Object
     */
    Object readValue(final String key) throws GcfException {
        if (!this.keys.containsKey(key)) {
            throw new GcfException("no key \""+key+"\" in group \""+this.path+"\"");
        }
        
        return this.keys.get(key);
    }
    
    /**
     * Reads a value as a short.
     * If the key is not present or if the value 
     * cannot be parsed as a short, an exception 
     * is thrown.
     * @param key the key string
     * @return the value as a short
     */
    public short readShort(final String key) throws GcfException {
        short s = 0;
        try {
            s = Short.parseShort(readValue(key).toString());
        } catch(NumberFormatException ex) {
            throw new GcfException("value "+readValue(key)+ " cannot be parse as an short");
        }
        return s;
    }
    
    /**
     * Reads a value as a int.
     * If the key is not present or if the value 
     * cannot be parsed as a int, an exception 
     * is thrown.
     * @param key the key string
     * @return the value as an int
     */
    public int readInt(final String key) throws GcfException {
        int nr = 0;
        try {
            nr = Integer.parseInt(readValue(key).toString());
        } catch(NumberFormatException ex) {
            throw new GcfException("value "+readValue(key)+ " cannot be parse as an integer");
        }
        return  nr;
    }
    
    /**
     * Reads a value as a float.
     * If the key is not present or if the value 
     * cannot be parsed as a float, an exception 
     * is thrown.
     * @param key the key string
     * @return the value as a float
     */
    public float readFloat(final String key) throws GcfException {
        float f = 0.f;
        try {
            f = Float.parseFloat(readValue(key).toString());
        } catch(NumberFormatException ex) {
            throw new GcfException("value "+readValue(key)+ " cannot be parse as an float");
        }
        return f;
    }
    
    /**
     * Reads a value as a double.
     * If the key is not present or if the value 
     * cannot be parsed as a double, an exception 
     * is thrown.
     * @param key the key string
     * @return the value as a double
     */
    public double readDouble(final String key) {
        double d = 0.;
        try {
            d = Double.parseDouble(readValue(key).toString());
        } catch(NumberFormatException ex) {
            throw new GcfException("value "+readValue(key)+ " cannot be parse as a double");
        }
        return d;
    }
    
    /**
     * Reads the key's value as a string.
     * It uses the <code>toString()</code> method to 
     * return the string value.
     * If the key is not present in this group an exception 
     * is thrown.
     * If the value is not defined as a string in the file, a warning 
     * is printed.
     * @param key the key string
     * @return the value as a string
     */
    public String readString(final String key) {
        final Object obj = readValue(key);
        if (!(obj instanceof String)) {
            final String msg = String.format("value for key \"%s\" in group %s is not a string. Use appropriate type.",
                                             key,this.path);
            GcfWarning.printWarning(msg);
        }
        return obj.toString();
    }
    
    /**
     * Gets the key value as a boolean.
     * If the key is not present within this group an exception
     * is thrown.
     * If the value is not a boolean an exception is thrown.
     * @param key
     * @return 
     */
    public boolean readBoolean(final String key) {
        final String value = readValue(key).toString().toLowerCase();
        if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        }
        
        throw new GcfException("value for key \""+key+"\" is not a boolean");
    }
    
    /**
     * Parses the current group by checking its syntax tokenwise.
     * The presence of each mandatory token is checked by using the 
     * <code>match()</code> method from the <code>Parser</code>,
     * to check if the tokens are present at all and in the correct order.
     * This is done by checking the token type.
     * If there is a mismatch between actual and demanded token type
     * an exception is thrown.
     * @param parser reference to the parser object
     */
    @Override
    void parse(final Parser parser) {
        // Group header
        parser.match(TokenType.GROUP_LBRACE);
        this.name = parser.match(TokenType.GROUP_NAME);
        parser.match(TokenType.GROUP_RBRACE);
        this.path = this.parent + this.name + "/";
        
        // Group content
        groupContent(parser);
        
        // Group footer
        parser.match(TokenType.GROUP_LBRACE);
        parser.match(TokenType.GROUP_FSLASH);
        final String closingGroupname = parser.match(TokenType.GROUP_NAME);
        if (this.name.equals(closingGroupname) == false) {
            throw new GcfException("group \""+this.path+"\" not correctly closed");
        }
        
        parser.match(TokenType.GROUP_RBRACE);
        
        // add this group to the global data container
        this.buffer.addGroup(this);
    }

    /**
     * Parses the group content.
     * Here the group content is parsed using two tokens of lookahead,
     * as part of the LL(2) parser architecture.
     * @param parser reference to the parser object 
     */
    private void groupContent(final Parser parser) {
        while(!parser.lookahead.getType().equals(TokenType.EOF)) {
            if (parser.lookahead.getType().equals(TokenType.KEY)) {
                final KeyValue kv = new KeyValue(getPath(),parser);
                this.keys.putIfAbsent(kv.getKey(), kv.getValue());
            }
            else if (parser.lookahead.getType().equals(TokenType.GROUP_LBRACE) && 
                     !parser.LT(2).getType().equals(TokenType.GROUP_FSLASH)) {
                final Group subGroup = new Group(this.path,parser,this.buffer,this.groupChanger);
            }
            else if (parser.lookahead.getType().equals(TokenType.GROUP_LBRACE) && 
                     parser.LT(2).getType().equals(TokenType.GROUP_FSLASH)) {
                break;
            }
            else {
                throw new GcfException("expecting subgroup or keyvalue. found " + parser.lookahead);
            }
        }
    }
    
}

