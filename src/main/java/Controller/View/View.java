package Controller.View;

import Shared.TextPosition;

public record View(
    ViewComponent textArea,
    ViewComponent uiPanel,
    ViewComponent numberPanel,
    ViewComponent temporaryMessages,
    TextPosition cursorPositionInTextArea) { }
