package Controller.Modes;

import Controller.ViewPort;
import FileStorage.TextFile;

record ParsingContext(ViewPort viewPort, TextFile textFile, ModeManager modeManager) {
}
