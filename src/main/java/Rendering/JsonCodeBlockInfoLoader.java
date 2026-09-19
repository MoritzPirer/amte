package Rendering;

import Debugging.DebugLogger;
import Shared.Utility.FileParsingUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class JsonCodeBlockInfoLoader implements CodeBlockInfoLoader {
    private final Map<String, Path> languageToFile;
    private final Map<String, Path> extensionToFile;

    private final Map<String, CodeBlockInfo> languageToInfo = new HashMap<>();
    private final Map<String, CodeBlockInfo> extensionToInfo = new HashMap<>();

    public JsonCodeBlockInfoLoader() {
        languageToFile = new HashMap<>();
        extensionToFile = new HashMap<>();
        prepareKnownFiles();

        System.gc(); // might not be necessary
    }

    @Override
    public boolean knowsLanguage(String language) {
        return languageToFile.containsKey(language.toLowerCase());
    }

    @Override
    public CodeBlockInfo loadByLanguage(String language) {
        language = language.toLowerCase();

        if (!knowsLanguage(language)) {
            throw new IllegalStateException("unknown language " + language);
        }

        if (languageToInfo.containsKey(language)) {
            return languageToInfo.get(language);
        }

        CodeBlockInfo info = load(languageToFile.get(language));
        if (info == null) {
            throw new RuntimeException("language had malformatted file: " + language);
        }

        addToCache(info);

        return info;
    }

    @Override
    public boolean knowsExtension(String extension) {
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }

        return extensionToFile.containsKey(extension.toLowerCase());
    }

    @Override
    public CodeBlockInfo loadByExtension(String extension) {
        String normalizedExtension = normalizeExtension(extension);
        if (!knowsExtension(normalizedExtension)) {
            throw new IllegalArgumentException("unknown extension: " + normalizedExtension);
        }

        if (extensionToInfo.containsKey(normalizedExtension)) {
            return extensionToInfo.get(normalizedExtension);
        }

        CodeBlockInfo info = load(extensionToFile.get(normalizedExtension));
        if (info == null) {
            throw new RuntimeException("extension had malformatted file: " + normalizedExtension);
        }

        addToCache(info);

        return info;
    }

    private static String normalizeExtension(String extension) {
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }

        return extension.toLowerCase();
    }

    private void addToCache(CodeBlockInfo info) {
        for (String name : info.names()) {
            languageToInfo.put(name, info);
        }
        for (String extension : info.extensions()) {
            extensionToInfo.put(extension, info);
        }
    }

    private void prepareKnownFiles() {
        String folderPath = FileParsingUtility.getCodeBlockInfoFolder();
        Path[] files = FileParsingUtility.filesInFolder(Path.of(folderPath));

        DebugLogger.info("RENDERING/LOADING", "Loading " + files.length + " files from " + folderPath);
        for (Path path : files) {
            preparePath(path);
        }
    }

    private void preparePath(Path path) {
        CodeBlockInfo info = load(path);
        if (info == null) {
            DebugLogger.warn("RENDERING/LOADING", "ignoring unparsable file: " + path);
            return;
        }

        for (String extension : info.extensions()) {
            extensionToFile.put(normalizeExtension(extension), path);
        }
        for (String name : info.names()) {
            languageToFile.put(name.toLowerCase(), path);
        }
    }

    private CodeBlockInfo load(Path path) {
        try {
            String content = FileParsingUtility.readFile(path);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, CodeBlockInfo.class);
        }
        catch (IOException e) {
            DebugLogger.warn("RENDERING/LOADING", "ignoring unparsable file: " + path
                + "(" + e.getMessage() + ")");
            return null;
        }
    }
}
