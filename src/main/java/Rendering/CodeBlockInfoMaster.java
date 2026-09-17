package Rendering;

import java.util.HashMap;
import java.util.Map;

class CodeBlockInfoMaster {
    private static CodeBlockInfoMaster instance = null;
    private CodeBlockInfoMaster() {}

    private final Map<String, CodeBlockInfo> loadedCodeBlockInfos = new HashMap<>();

    public static CodeBlockInfoMaster instance() {
        if (instance == null) {
            instance = new CodeBlockInfoMaster();
        }

        return instance;
    }

    public CodeBlockInfo getCodeBlockInfo(String language) {
        if (loadedCodeBlockInfos.containsKey(language)) {
            return loadedCodeBlockInfos.get(language);
        }

        CodeBlockInfoLoader loader = new JsonCodeBlockInfoLoader();
        if (!loader.knowsLanguage(language)) {
            return null;
        }

        CodeBlockInfo codeBlockInfo = loader.load(language);
        loadedCodeBlockInfos.put(language, codeBlockInfo);

        return codeBlockInfo;
    }
}
