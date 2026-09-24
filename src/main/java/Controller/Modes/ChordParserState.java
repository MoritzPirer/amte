package Controller.Modes;

interface ChordParserState {
    ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context);
}
