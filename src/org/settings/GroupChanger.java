package org.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to assist in the change groups and formatting of 
 * their paths.
 * @author Miguel Cardoso Martins
 */
final class GroupChanger {
    
    /* Referece to the currently opened group */
    private Group currentGroup;    
    
    /* Reference to the data container */
    private final Buffer buffer;
    
    /**
     * Creates an instance of the group changer.
     * @param buffer reference to the data container
     */
    /*package-privat*/ GroupChanger(final Buffer buffer) {
        this.buffer = buffer;
        this.currentGroup = this.buffer.getGroup("/");
    }
    
    /**
     * Changes the current group according to the given path.
     * The path can be absolute or relative.
     * If the path is relative the absolute path will be created
     * internally.
     * The current opened group field is set to the group specified 
     * by the input group path, if the such path is present in the 
     * data container.
     * The current group then returned.
     * @param changeGroupPath the relativ or absolute path to change to
     * @return the group object of the newly changed group
     * @throws GcfException 
     */
    /*package-privat*/ Group changeGroup(final String changeGroupPath) throws GcfException {
        if (changeGroupPath.isEmpty()) {
            throw new GcfException("trying to access group with empty name not possible!");
        }
        
        if (isRootPath(changeGroupPath)) {
            throw new GcfException("\"/\" is not a valid group name");
        }
        
        final String newGroupPath = changePath(changeGroupPath);
        
        this.currentGroup = this.buffer.getGroup(newGroupPath);
        return this.currentGroup;
    }
    
    /**
     * Chacks if a given path is the (non-accessible) root path.
     * @param path the oath to be checked
     * @return true if the input path equals the root path, false otherwise
     */
    private boolean isRootPath(final String path) {
        return path.equals("/");
    }
    
    /**
     * Constructs a path out of the current group path and 
     * the input path.
     * This method is called when the <code>openGroup()</code> 
     * method is called, so that the correct group is opened.
     * @param path
     * @return the new constructed path
     */
    private String changePath(final String path) {
        final StringBuilder builder = new StringBuilder();
        final String newPath = formatPath(path);
        if (isPathAbsolute(path)) {
            builder.append(newPath);
        }
        else {
            if (isPathChangeForward(newPath)) {
                builder.append(this.currentGroup.getPath()).append(newPath);
            }
            else {
                final int depth = backwardChangeDepth(newPath);
                String endOfNewPath = path.replaceAll("\\.\\./", "");
                endOfNewPath = (endOfNewPath.isEmpty()) ? ("") : (formatPath(endOfNewPath));
                Group parent = this.currentGroup;
                for(int i=0; i<depth; i++) {
                    parent = this.buffer.getGroup(parent.getParent());

                }
                builder.append(parent.getPath()).append(endOfNewPath);
            }
        }
        return builder.toString();
    }
    
    /**
     * Checks if the input path is absolut (i.e. start with a /)
     * @param path the path to be checked
     * @return true if input path is absolut
     */
    private boolean isPathAbsolute(final String path) {
        return path.startsWith("/");
    }
    
    /**
     * Checks if the input path change is forward or backward
     * @param pathChange the input path change 
     * @return true if path change is forward
     */
    private boolean isPathChangeForward(final String pathChange) {
        return pathChange.startsWith("../") == false;
    }
    
    /**
     * Gets the depth of a backward path change, i.e. 
     * the number of times one has to go high up the 
     * path hierarchy to change to the new path.
     * @param pathChange the relative new path
     * @return the number of backward changes
     */
    private int backwardChangeDepth(final String pathChange) {
        Pattern p = Pattern.compile("\\.\\.\\/");
        Matcher m = p.matcher(pathChange);
        
        int count = 0;
        while (m.find()){
            count +=1;
        }
        return count;
    }
    
    /**
     * Checks the input path is ends with a foward slash or not.
     * In case it does not, the missing forward slash is added.
     * @param path
     * @return the path in required format
     */
    public static String formatPath(final String path) {
        if (path.endsWith("/")) {
            return path;
        }
        return path+"/";
    }
}
