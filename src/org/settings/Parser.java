package org.settings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents the parser of the configuration file.
 * In this class the parsing of the file's content is kicked off.
 * Only the top-level elements in the file area identified, i.e.
 * top-level groups and global keys.
 * The parsing of this top-level elements and their respective sub elements
 * is delegated to the subu elements themselves.
 * Once the top-level elements have been recognized and parsed 
 * their are added to the main data container.
 * @author Miguel Cardoso Martins
 */
class Parser {
   
    /**
     * Creates a new instance of the parser.
     * @param file The file to be parsed
     * @param buffer reference to the main data container
     * @param groupChanger reference to the group changer
     * @throws GcfException
     */
    /*package-privat*/ Parser(final File file, final Buffer buffer, final GroupChanger groupChanger) throws GcfException {
        parse(file,buffer,groupChanger);
    }

    /**
     * Kicks off the parsing of the file.
     * This method parses the top-level elements in the file,
     * and delegates their actual content parsing to the elements themselves.
     * When an element has been identified and parsed it is added to the main
     * data container.
     * @param file the file to be parsed 
     * @param buffer reference to the main data container
     * @param groupChanger reference to the group changer
     * @throws GcfException
     */
    private void parse(final File file, final Buffer buffer, final GroupChanger groupChanger) throws GcfException {
        try(final InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            
            final Tokener tok = new Tokener(stream);
            while(!tok.eof()) {
                final char next = tok.nextNonEmpty();

                if (next == GcfUtils.COMMENT_CHAR) {
                    // skip over comment
                    tok.nextUntilEndOfLine();
                }
                else if (Character.isLetter(next)) {
                    final KeyValue kv = new KeyValue(tok,buffer);
                    buffer.addGlobalKey(kv.getKey(), kv.getValue());
                }
                else if (next == GcfUtils.GROUP_LBRACE && tok.next()!= GcfUtils.GROUP_END_CHAR) {
                    final Group group = new Group("/",buffer,groupChanger,tok);
                    buffer.addTopGroup(group);
                }
                else if (next == ' ' || next == '\n' || next == '\t' || next == '\b') {
                    // skip over empty spaces
                }
                else {
                    throw new GcfException("illegal character at line "+tok.lineNumber());
                }
            }
            
        } catch(FileNotFoundException ex) {
            throw new GcfException("File not found: \""+file.getAbsolutePath()+"\"");
        } catch(IOException ex) {
            throw new GcfException("IO problem occurred while reading \""+file.getAbsolutePath()+"\"");
        }
    }
}
    
