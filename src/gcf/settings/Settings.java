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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Main class to handle gcf-configutation files.<br>
 * GCF stands for "Group Configuration File", i.e. the 
 * data is saved in Groups. This class offers access to those 
 * groups which in turn offer access to the underlying configuration 
 * data.<br>
 * The basic syntax is as follow:<br>
 * <pre>
 * --------- GCF START------------
 *     global_var = "some var"
 * 
 *     # Some multiline
 *     # comment
 *     [Constants]
 *         int     = 10 # some inline comment
 *         float   = 12.48
 *         boolean = true
 *         string  = "some string"
 *         var     = ${global_var}
 *         [Sub]
 *             nr = 1000
 *         [/Sub]
 *     [/Constants]
 * --------- GCF END -------------
 * </pre>
 * 
 * The configuration data is saved as key/value pairs within groups.<br>
 * The key names can be composed of letters, numbers, underscores,
 * dots and dashes, but must start with a letter.<br>
 * The values can be integers, floats (with a dot as the decimal separator), 
 * booleans, strings (enclosed in double quotes) or a reference to a 
 * global variable.<br>
 * A group is defined using square brackets inbetween which 
 * the groupname is defined.<br>
 * It has an opening and closing statement (similar to XML). If 
 * a group is not closed properly, either by mispelling of the 
 * group name, or missing the "end group" syntax, an exception is thrown,
 * during parsing.<br>
 * The rules for the groupname are the same as for the keys, with the
 * exception that groupnames can start with numbers.<br>
 * The content of a group can consist of key/values and/or other (sub)groups.<br>
 * Comments are started with a '#' symbol.<br><br>
 * 
 * To get access to a specific group one uses the <code>Settings</code>
 * class as follows:
 * <blockquote>
 * <pre>
 *     final Settings set = new Settings(someFile);
 *     final Group constGroup = set.getGroup("/Constants/");
 * </pre>
 * </blockquote>
 * 
 * With the Settings instance reference one can get access to the configuration 
 * data and/or the subgroups.
 * <blockquote>
 * <pre>
 *     // Get a reference to the "Constants" group
 *     final Group constGroup = constGroup.getGroup("/Constants/");
 * 
 *     // Read a value for the key "string"
 *     final String str = constGroup.readString("string");
 * 
 *     // Get a reference to the subgroup "Sub"
 *     final Group subGroup = constGroup.getGroup("Sub/");
 * </pre>
 * </blockquote>
 * 
 * A group is retrieved using its full path, so as if it was in a 
 * (Unix) filesystem. A subgroup on the other hand is retrieved using
 * the relative path, starting at the parent.<br>
 * To access the value of a key, one passes the key name as a string
 * to the respective type method, e.g. <code>readInt</code> 
 * to retrieve an integer.<br><br>
 * 
 * Besides allowing the retrieval of subgroups, the Group class allows 
 * the adding of new keys or changing the values of existing keys, as well as 
 * adding and deleting subgroups.<br>
 * For any of these actions to take effect though, one has to explicitly
 * save these changes
 * <blockquote>
 * <pre>
 *     // create this key/value inside the Constants group
 *     constGroup.addKey("some_new_key",9999.99);
 * 
 *    // save the changes
 *    set.save();
 * </pre>
 * </blockquote>
 */
public class Settings {

    /* The source file to be parsed */
    private final File file;
    
    /* The allowed config file extensions */
    private final String[] FILE_EXTENSIONS = {"gcf","Gcf","GCF"};
    
    /* The data buffer with all group information */
    private final Buffer buffer;
    
    /* Help class to change groups */
    private final GroupChanger groupChanger;
    
    /**
     * Creates a <code>Settings</code> instance from the absolute 
     * path of the config file.
     * @param absFilePath the absolute file of the config file
     */
    public Settings(final String absFilePath) {
        Objects.requireNonNull(absFilePath, "file object is null");
        checkFileEnding(absFilePath);
        this.file = new File(absFilePath);
        this.buffer = new Buffer();
        this.groupChanger = new GroupChanger(this.buffer);
        
        open();
    }
    
    /**
     * Checks if the config file's extension is correct.
     * @param fileName the name of the file to be parsed
     * @throws GcfException
     */
    private void checkFileEnding(final String fileName) throws GcfException {
        boolean passed = false;
        for(String ext : FILE_EXTENSIONS) {
            if (fileName.endsWith("."+ext)) {
                passed = true;
                break;
            }
        }
        if (!passed) {
            throw new GcfException("file must have one of the following extension: "+Arrays.asList(FILE_EXTENSIONS));
        }
    }
    
    /**
     * Opens and parses the file content.
     * The file is parsed with a LL(2) parser.
     */
    private void open() {
        final Lexer lexer = new GcfLexer(this.file);
        final GcfParser parser = new GcfParser(lexer, 2, this.buffer, this.groupChanger);
        parser.body();
    }
    
    /**
     * Gets a group by its name or full path.
     * @param absGroupPath the absolute path of the group requested
     * @return the group requested with respect to the absolute path
     */
    public Group getGroup(final String absGroupPath) {
        return this.groupChanger.changeGroup(absGroupPath);
    }
    
    /**
     * Gets the top level child groups.
     * @return collection of the top level groups.
     */
    public Collection<Group> childGroups() {
        return this.buffer.subGroupsForPath("/");
    }

    /**
     * Saves the changes made to file.
     */
    public void save() {
        saveToFile(this.file);
    }

    /**
     * Saves the changes made to a specified file.
     * @param newFile the file to which the changes are to be saved.
     */
    public void saveToFile(final File newFile) {
        final GcfWriter gcfWriter = new GcfWriter(newFile,this.buffer);
        gcfWriter.writeFile();
    }
}
