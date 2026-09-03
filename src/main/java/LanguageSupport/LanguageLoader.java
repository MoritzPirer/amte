package LanguageSupport;

interface LanguageLoader {
    boolean doesLanguageExist(String language);
    Language load(String language);
}
