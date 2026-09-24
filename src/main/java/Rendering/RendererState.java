package Rendering;

import SpellChecking.SpellChecker;

interface RendererState {
    StyleData[] parse(String paragraph, SpellChecker spellChecker);
}
