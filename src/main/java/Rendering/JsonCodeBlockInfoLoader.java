package Rendering;

import Debugging.DebugLogger;
import Shared.Utility.FileParsingUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class JsonCodeBlockInfoLoader implements CodeBlockInfoLoader {

    @Override
    public Map<String, CodeBlockInfo> loadAll() {
        Map<String, CodeBlockInfo> codeBlockInfoMap = new HashMap<>();
        String folderPath = FileParsingUtility.getCodeBlockInfoFolder();
        Path[] files = FileParsingUtility.filesInFolder(Path.of(folderPath));

        DebugLogger.info("RENDERING/LOADING", "Loading " + files.length + " files from " + folderPath);
        for (Path path : files) {
            CodeBlockInfo info = load(path);
            if (info != null) {
                addAllNames(codeBlockInfoMap, info);
            }
            else {
                DebugLogger.warn("RENDERING/LOADING", "ignoring unparsable file: " + path);
            }
        }

        return codeBlockInfoMap;
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

    private void addAllNames(Map<String, CodeBlockInfo> codeBlockInfoMap, CodeBlockInfo codeBlockInfo) {
        for (String name : codeBlockInfo.names()) {
            DebugLogger.info("RENDERING/LOADING", "adding language alias: " + name);
            codeBlockInfoMap.put(name, codeBlockInfo);
        }
    }

}
