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


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holds all parsed data.
 * This class defines a wrapper around the map that holds all groups 
 * per absolute group path.
 * It defines convinience methods to make data handling more easy and readable.
 */
final class Buffer {
    
    /* Map to hold all parsed group data */
    private final Map<String,Group> content;
    
    /**
     * Creates and initiates the buffer.
     * Adds the root group to the main map container.
     */
    /*package-privat*/ Buffer() {
         this.content = new LinkedHashMap<>();
         
         /*
          * Add the root group to the content map.
          * This group is NOT accessible from outside, i.e. 
          * it is invisible and unaccessible to the client. 
          * But is important to list all top level groups 
          * and to hold the global keys.
          */
         final Group rootGroup = new Group("", "/", this);
         this.content.put(rootGroup.getName(), rootGroup);
    }
    
    /**
     * Gets the value corrsponding to a global key.
     * If the key doesn't exist an exception is thrown.
     * The return is an Object since this is the way that 
     * the values to the keys are stored internally.
     * @param globalKey the global key
     * @return value with respect to the key as an object
     */
    /*package-privat*/ Object getGlobalValue(final String globalKey) throws GcfException {
        final Group rootGroup = this.content.get("/");
        if (!rootGroup.hasKey(globalKey)) {
            throw new GcfException("no global key \""+globalKey+"\"");
        }
        
        return rootGroup.readValue(globalKey);
    }
    
    /**
     * Adds a key/value as a global key, so that it can be
     * later referenced in the config file.
     * The value is passed as an Object since all values 
     * are internally stored as such.
     * To make the key/value global they are added as 
     * child elements of the root group \"/\", which 
     * is not visible to the client.
     * @param key the key 
     * @param obj the value
     */
    /*package-privat*/ void addGlobalKey(final String key, final Object obj) {
        this.content.get("/").addObjKey(key,obj);
    }
    
    /**
     * Adds a group which is a sub group of the non-accessible root group /.
     * @param group the subgroup
     */
    /*package-privat*/ void addTopGroup(final Group group) {
        // add to the main data buffer
        this.content.putIfAbsent(group.getPath(), group);
        
        // set as subgroup of the root group
        this.content.get("/").addSubGroup(group);
    }
    
    /**
     * Adds a group to the container.
     * @param group the group to add
     */
    /*package-privat*/ void addGroup(final Group group) {
        this.content.putIfAbsent(group.getPath(), group);
    }
    
    /**
     * Checks if a group path is present int the data container.
     * The data container saves its data as group per absolute 
     * group path.
     * @param groupPath the absolute path of the group to be checked
     * @return true if the group's path is present in the container, false otherwise
     */
    /*package-privat*/ boolean containsGroup(final String groupPath) {
        return this.content.containsKey(groupPath);
    }
    
    /**
     * Gets a group from the data container.
     * The group is retrieved according to its absolute path.
     * @param absoluteGroupPath the absolute path of the group
     * @return the group object if its absolute path is present
     * @throws GcfException
     */
    /*package-privat*/ Group getGroup(final String absoluteGroupPath) throws GcfException {
        if (this.content.containsKey(absoluteGroupPath) == false) {
            throw new GcfException(
                        "group \"" + absoluteGroupPath + "\" does not exist"
                        );
        }
        
        return this.content.get(absoluteGroupPath);
    }
    
    /**
     * Gets all subgroups for specified path.
     * @param groupPath the absolute group path
     * @return unmodifiable collection of sub groups
     */
    Collection<Group> subGroupsForPath(final String groupPath) {
        return Collections.unmodifiableCollection(
            this.content
                .entrySet()
                .stream()
                .map(e -> e.getValue())
                .filter(g -> g.getParent().equals(groupPath))
                .collect(Collectors.toList())
        );
    }
    
    /**
     * Deletes the subgroup and all its subgroups for the specified path.
     * @param groupPath absolute group path
     */
    void deleteSubGroup(final String groupPath) {
        this.content.entrySet().removeIf(e -> e.getKey().startsWith(groupPath));
    }
}
