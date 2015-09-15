package org.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Miguel Martins
 */
public final class Group extends Element {
    
    /* Holds all sun groups */
    private final Map<String,Group> subGroups = new LinkedHashMap<>();
    
    /* Holds all keys/values */
    private final Map<String,Object> keys     = new LinkedHashMap<>();
    
    /* Reference to the main data container */
    private Buffer buffer;
    
    /* Reference to the group changer */
    private GroupChanger groupChanger;
    
    
    /**
     * 
     * @param parent
     * @param tok 
     */
    Group(final String parent, final Parser parser, final Buffer buffer, final GroupChanger groupChanger) {
        this.parent = parent;
        this.buffer = buffer;
        this.groupChanger = groupChanger;
        parse(parser);
    }
    
    /**
     * 
     * @param parent
     * @param name 
     */
    public Group(final String parent, final String name) {
        this.parent = parent;
        this.name   = name;
        this.path   = parent + name + (parent.isEmpty()?"":"/");
    }
    
    /**
     * 
     * @param buffer 
     */
    void setBuffer(final Buffer buffer) {
        this.buffer = buffer;
    }
    
    /**
     * 
     * @return 
     */
    public Collection<Group> childGroups() {
        return Collections.unmodifiableCollection(this.subGroups.values());
    }
    
    /**
     * 
     * @return 
     */
    public Collection<String> childKeys() {
        return Collections.unmodifiableCollection(this.keys.keySet());
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public boolean hasKey(final String key) {
        return this.keys.containsKey(key);
    }
    
    /**
     * 
     * @param <T>
     * @param key
     * @param value 
     */
    public final <T extends Number> void addKey(final String key, final T value) {
        addObjKey(key, value);
    }
    
    /**
     * 
     * @param key
     * @param value 
     */
    public final void addKey(final String key, final String value) {
        addObjKey(key, value);
    }
    
    /**
     * 
     * @param key
     * @param value 
     */
    public final void addKey(final String key, final boolean value) {
        addObjKey(key, value);
    }
    
    /**
     * 
     * @param key
     * @param value 
     */
    void addObjKey(final String key, final Object value) {
        this.keys.putIfAbsent(key, value);
    }
    
    /**
     * 
     * @param key 
     */
    public void deleteKey(final String key) {
        this.keys.remove(key);
    }
    
    /**
     * 
     * @param key
     * @param value 
     */
    public void changeValue(final String key, final String value) {
        changeObjValue(key, value);
    }
    
    /**
     * 
     * @param <T>
     * @param key
     * @param value 
     */
    public <T extends Number> void changeValue(final String key, final T value) {
        changeObjValue(key, value);
    }
    
    /**
     * 
     * @param key
     * @param value 
     */
    private void changeObjValue(final String key, final Object value) {
        this.keys.put(key, value);
    }
    
    /**
     * 
     * @param relGroupPath
     * @return 
     */
    public Group getGroup(final String relGroupPath) {
        return this.groupChanger.changeGroup(relGroupPath);
    }
    
    /**
     * 
     * @param group 
     */
    public void addSubGroup(final Group group) {
        this.subGroups.putIfAbsent(group.getPath(), group);
        this.buffer.addGroup(group);
    }
    
    /**
     * 
     * @param name 
     */
    public void addSubGroup(final String name) {
        addSubGroup(new Group(this.path, name));
    }
    
    /**
     * 
     * @param groupName 
     */
    public void deleteSubGroup(final String groupName) {
        final String absolutePath = this.path + groupName + "/";
        if (this.subGroups.containsKey(absolutePath) == false) {
            throw new GcfException("no such group to delete: \"" + getPath() + groupName + "/\"");
        }
        
        this.subGroups.remove(absolutePath);
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public Object readValue(final String key) {
        if (!this.keys.containsKey(key)) {
            throw new GcfException("no key \""+key+"\" in group \""+this.path+"\"");
        }
        
        return this.keys.get(key);
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public short readShort(final String key) {
        short s = 0;
        try {
            s = Short.parseShort(readValue(key).toString());
        } catch(NumberFormatException ex) {
            throw new GcfException("value "+readValue(key)+ " cannot be parse as an short");
        }
        return s;
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public int readInt(final String key) {
        int nr = 0;
        try {
            nr = Integer.parseInt(readValue(key).toString());
        } catch(NumberFormatException ex) {
            throw new GcfException("value "+readValue(key)+ " cannot be parse as an integer");
        }
        return  nr;
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public float readFloat(final String key) {
        float f = 0.f;
        try {
            f = Float.parseFloat(readValue(key).toString());
        } catch(NumberFormatException ex) {
            throw new GcfException("value "+readValue(key)+ " cannot be parse as an float");
        }
        return f;
    }
    
    /**
     * 
     * @param key
     * @return 
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
     * 
     * @param key
     * @return 
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
     * 
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
     * 
     * @param tokener 
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
        parser.match(TokenType.GROUP_NAME);
        parser.match(TokenType.GROUP_RBRACE);
        
        // add this group to the global data container
        this.buffer.addGroup(this);
    }

    private void groupContent(final Parser parser) {
        while(!parser.lookahead.type.equals(TokenType.EOF)) {
            if (parser.lookahead.type.equals(TokenType.KEY)) {
                final KeyValue kv = new KeyValue(parser,this.buffer);
                this.keys.putIfAbsent(kv.getKey(), kv.getValue());
            }
            else if (parser.lookahead.type.equals(TokenType.GROUP_LBRACE) && 
                     !parser.LT(2).type.equals(TokenType.GROUP_FSLASH)) {
                final Group subGroup = new Group(this.path,parser,this.buffer,this.groupChanger);
                this.subGroups.putIfAbsent(subGroup.getPath(),subGroup);
            }
            else if (parser.lookahead.type.equals(TokenType.GROUP_LBRACE) && 
                     parser.LT(2).type.equals(TokenType.GROUP_FSLASH)) {
                break;
            }
            else {
                throw new Error("wrong token: " + parser.lookahead);
            }
        }
    }
}

