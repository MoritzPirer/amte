package Controller.Modes;

import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;

record ParsingContext(ViewPort viewPort, TextFile textFile, ModeManager modeManager) {
}
