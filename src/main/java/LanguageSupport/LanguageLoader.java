package LanguageSupport;

import java.nio.file.Path;
import java.util.Map;

interface LanguageLoader {
    Map<String, Language> loadAll(Path path);
}
