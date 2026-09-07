package Controller.Modes;

import Controller.ViewPort;

interface ChordParserState {
    ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context);
}
