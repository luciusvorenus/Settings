package org.settings;


/**
 * Parser implementation class for the gcf file format.
 * It extends the <code>Parser</code> abstract class that 
 * defines a LL(k) parser, with k>=1. It offers generic support 
 * code for the implementation.
 * For the specific case of the gcf file format, we only need 
 * a LL(2) parser, i.e. a maximum of two lookahead tokens 
 * at any given time.
 * This class contains methods that correspond to rules
 * in the gcf file format.
 * The body method offers an entry point for the parsing of the file,
 * parsing only the top level elements.
 * The parsing of sub level elements (e.g. groups within groups),
 * is delegated to the elements themselves.
 */
class GcfParser extends Parser {
    
    /* Reference to the main data buffer */
    private final Buffer dataBuffer;
    
    /* Reference to the groupchanger utility class */
    private final GroupChanger groupChanger;

    /**
     * Create a parser instance.
     * @param lexer         reference to the lexer
     * @param bufferSize    amount of lookahead tokens
     * @param buffer        reference to the main data buffer
     * @param groupChanger  reference to the groupchanger utility class
     */
    GcfParser(final Lexer lexer, final int bufferSize, final Buffer buffer, final GroupChanger groupChanger) {
        super(lexer,bufferSize);
        this.dataBuffer = buffer;
        this.groupChanger = groupChanger;
    }
    
    /**
     * Parses the top level elements.
     * This method recognizes and delegates the parsing of 
     * elements in the gcf file format that are not within a group, 
     * i.e. a top level element.
     */
    void body() {
        while(!lookahead.getType().equals(TokenType.EOF)) {
            if (lookahead.getType().equals(TokenType.GROUP_LBRACE) && 
                !LT(2).getType().equals(TokenType.GROUP_FSLASH)) {
                group();
            }
            else {
                throw new GcfException("expecting group. found " + lookahead);
            }
        }
    }
    
    /**
     * Creates a top level group and adds it to the main data buffer.
     * The parsing of the group is delegated to the group itself.
     */
    private void group() {
        final Group group = new Group("/", this, dataBuffer, groupChanger);
        dataBuffer.addTopGroup(group);
    }
}
