package org.settings;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * 
 * @author Miguel Martins
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
            if (fileName.endsWith(ext)) {
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
     */
    private void open() {
        final Parser parser = new Parser(this.file,this.buffer,this.groupChanger);
    }
    
    /**
     * Gets 
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
        return this.buffer.getGroup("/").childGroups();
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
