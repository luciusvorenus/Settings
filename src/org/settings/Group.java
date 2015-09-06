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
    Group(final String parent, final Buffer buffer, final GroupChanger groupChanger, final Tokener tok) {
        this.parent = parent;
        this.buffer = buffer;
        this.groupChanger = groupChanger;
        parse(tok);
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
    void parse(final Tokener tokener) {
        // set tokener to after the '['
        tokener.back();
        
        // parse group header
        parseGroupHeader(tokener);
        
        // parse possible keys and sub groups
        parseGroupContent(tokener);
        
        // add this group to the global data container
        this.buffer.addGroup(this);
    }
        
    /**
     * 
     * @param tok
     * @param groupHeader 
     */
    private void parseGroupHeader(final Tokener tok) {
        final String groupHeader = tok.nextUntilEndOfLine();
        
        if (groupHeader.contains(""+GcfUtils.GROUP_RBRACE) == false) {
            throw new GcfException("Group header must end with an \'"+GcfUtils.GROUP_RBRACE+"\'");
        }
        
        final String[] parts = groupHeader.split(""+GcfUtils.GROUP_RBRACE);
        
        // check if rest syntax is correct
        // check if there is text to the right and if so, if it is a comment
        if (parts.length > 1) {
            final String commentSection = parts[1].trim();
            if (!commentSection.startsWith(""+GcfUtils.COMMENT_CHAR)) {
                throw new GcfException("comments to the right of a group header must start with \'"+GcfUtils.COMMENT_CHAR+"\'");
            }
        }
        
        // Set and check the group's name
        this.name = parts[0].trim();
        if (this.name.isEmpty()) {
            throw new GcfException("No name of group set at line "+(tok.lineNumber()-1));
        }
        
        // set the absolute path to the group
        this.path = this.parent + this.name + "/";
    }
    
    /**
     * 
     * @param tok
     * @param dataBuffer 
     */
    private void parseGroupContent(final Tokener tok) {
        boolean footerParsed = false;
        while(tok.eof() == false) {
            final char next = tok.nextNonEmpty();
            
            if (next == GcfUtils.COMMENT_CHAR) {
                tok.nextUntilEndOfLine();
            }
            else if (Character.isLetter(next)) {
                final KeyValue kv = new KeyValue(tok,this.buffer);
                this.keys.putIfAbsent(kv.getKey(), kv.getValue());
            }
            else if (next == GcfUtils.GROUP_LBRACE) {
                final char afterNext = tok.next();
                if (afterNext != GcfUtils.GROUP_END_CHAR) {
                    final Group subGroup = new Group(this.path,this.buffer,this.groupChanger,tok);
                    this.subGroups.putIfAbsent(subGroup.getPath(),subGroup);
                }
                else {
                    parseGroupFooter(tok);
                    footerParsed = true;
                    break;
                }
            }
        }
        
        if (!footerParsed) {
            throw new GcfException("group \""+this.path+"\" not closed correctly at line "+(tok.lineNumber()));
        }
    }

    /**
     * 
     * @param tok 
     */
    private void parseGroupFooter(final Tokener tok) {
        final String footer = tok.nextUntil(GcfUtils.GROUP_RBRACE);
        if (!footer.equals(this.name)) {
            throw new GcfException("Group \""+this.name+"\" not correctly closed at line "+(tok.lineNumber()));
        }
        
    }

}

