package Rendering;

import java.util.Map;

class CodeBlockInfoMaster {
    private static CodeBlockInfoMaster instance = null;

    private final Map<String, CodeBlockInfo> loadedCodeBlockInfos;

    private CodeBlockInfoMaster() {
        this.loadedCodeBlockInfos = new JsonCodeBlockInfoLoader().loadAll();
    }


    public static CodeBlockInfoMaster instance() {
        if (instance == null) {
            instance = new CodeBlockInfoMaster();
        }

        return instance;
    }

    public CodeBlockInfo getByExtension(String fileExtension) {
        for (CodeBlockInfo info : loadedCodeBlockInfos.values()) {
            if (info.extensions().contains(fileExtension)) {
                return info;
            }
        }

        return null;
    }

    public CodeBlockInfo getCodeBlockInfo(String language) {
        if (loadedCodeBlockInfos.containsKey(language)) {
            return loadedCodeBlockInfos.get(language);
        }

        return CodeBlockInfo.DUMMY;
    }
}
