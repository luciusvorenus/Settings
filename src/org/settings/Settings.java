package org.settings;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

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
 *     [Constants]
 *         int     = 10
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
 * The configuration data is saved as key/value pairs within a group.
 * The key names can be composed of letters, numbers and underscores, but
 * must start with a letter. The value can be integers, floats 
 * (with a dot as the decimal separator), booleans, strings
 * (enclosed in double quotes) or a reference to a global variable.<br>
 * The groups are defined using square brackets inbetween which 
 * the groupname is defined. The rules for the groupname are the 
 * same as for the keys.<br>
 * A group has a start and end definition (similar to XML). If 
 * a group is not closed correctly, either by mispelling of the 
 * group name, or missing the "group end tag", an exception is thrown,
 * during parsing.<br>
 * The content of a group can be key/values or other (sub)groups.<br><br>
 * 
 * To get access to a specific group one can use the <code>Settings</code>
 * instance as follows:
 * <blockquote>
 * <pre>
 *     final Settings set = new Settings(someFile);
 *     final Group constGroup = set.getGroup("/Constants/");
 * </pre>
 * </blockquote>
 * 
 * With the group instance one can get access to the configuration data
 * and/or subgroups.
 * <blockquote>
 * <pre>
 *     final String str = constGroup.readString("string");
 *     final Group subGroup = constGroup.getGroup("Sub/");
 * </pre>
 * </blockquote>
 * 
 * Notice how the path to retrieve a sub group is relative.<br><br>
 * 
 * Furthermore the Group class allows one to add new keys or 
 * change the values of existing keys, as well as adding and 
 * deleting subgroups.<br>
 * For any of these actions to take effect one has to explicity
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
     * Create <code>Settings</code> instance from 
     * a <code>File</code> object.
     * @param file the <code>File</code> object representing the config file
     */
    public Settings(final File file) {
        checkFileEnding(file.getAbsolutePath());
        this.file = file;
        this.buffer = new Buffer();
        this.groupChanger = new GroupChanger(this.buffer);
        
        open();
    }
    
    /**
     * Creates a <code>Settings</code> instance from the absolute 
     * path of the config file.
     * @param absFilePath the absolute file of the config file
     */
    public Settings(final String absFilePath) {
        this(new File(absFilePath));
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
