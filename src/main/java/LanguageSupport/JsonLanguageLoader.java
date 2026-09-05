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
    public boolean doesLanguageExist(String language) {
        Path path = Path.of(FileParsingUtility.getLanguageFolder());
        for (Path filePath : FileParsingUtility.filesInFolder(path)) {
            if (language.equals(languageFileName(filePath))) {
                return true;
            }
        }

        return false;
    }

    private String languageFileName(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private record LanguageItem(String key, String value) {}

    @Override
    public Language load(String language) {
        Path path = Path.of(FileParsingUtility.getLanguageFolder() + language + ".json");

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
