package org.settings;


/**
 *
 * @author Miguel Cardoso Martins
 */
class GcfParser extends Parser {
    
    private final Buffer dataBuffer;
    private final GroupChanger groupChanger;

    GcfParser(final Lexer lexer, final int bufferSize, final Buffer buffer, final GroupChanger groupChanger) {
        super(lexer,bufferSize);
        this.dataBuffer = buffer;
        this.groupChanger = groupChanger;
    }
    
    void body() {
        while(!lookahead.getType().equals(TokenType.EOF)) {
            if (lookahead.getType().equals(TokenType.GROUP_LBRACE) && 
                !LT(2).getType().equals(TokenType.GROUP_FSLASH)) {
                group();
            }
            else {
                throw new Error("wrong token: " + lookahead);
            }
        }
    }
    
    private void group() {
        final Group group = new Group("/", this, dataBuffer, groupChanger);
        dataBuffer.addTopGroup(group);
    }
}
