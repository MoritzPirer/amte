package Controller.Modes;

import Controller.Action.CompoundAction;
import Controller.Action.DeleteAction;
import Controller.Action.InsertAction;
import Shared.TextPosition;

class ExpectReplaceValueState implements ChordParserState {
    private final ChordParser chordParser;

    public ExpectReplaceValueState(ChordParser chordParser) {
        this.chordParser = chordParser;
    }

    @Override
    public ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context) {
        assert buffer.length() == 2;

        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        char replacement = buffer.get().charAt(1);

        chordParser.changeState(new ExpectOperatorState(chordParser));
        buffer.clear();
        return new ParseSnapshot(ParseStatus.FINISHED, new CompoundAction(
            new DeleteAction(cursor, context.textFile().nextPosition(cursor)),
            new InsertAction(String.valueOf(replacement), cursor)
        ));
    }
}
