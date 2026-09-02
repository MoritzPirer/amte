package LanguageSupport;

import Shared.Utility.FileParsingUtility;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class JsonLanguageLoader implements LanguageLoader {
    @Override
    public Map<String, Language> loadAll(Path path) {
        Map<String, Language> languages = new HashMap<>();

        for (Path filePath : FileParsingUtility.filesInFolder(path)) {
            String languageName = languageFileName(filePath);
            Language content = load(filePath);

            if (content != null) {
                languages.put(languageName, content);
            }
        }

        return languages;
    }

    private String languageFileName(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private Language load(Path path) {
        String[] items;
        try {
            String jsonString = FileParsingUtility.readFile(path);
            items = FileParsingUtility.splitIntoItems(jsonString);
        }
        catch (IOException e) {
            return null;
        }

        Map<String, String> texts = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        for (String itemString : items) {
            try {
                LanguageItem item = mapper.readValue(itemString, LanguageItem.class);
                if (item == null || item.key() == null || item.value() == null) {
                    continue;
                }

                texts.put(item.key(), item.value());
            }
            catch (JacksonException ignored) {
                //ignore misformatted entries
            }
        }

        return new Language(texts);
    }
}
