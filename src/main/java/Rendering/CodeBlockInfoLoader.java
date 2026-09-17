package Rendering;

interface CodeBlockInfoLoader {
    boolean knowsLanguage(String language);

    CodeBlockInfo load(String language);
}
