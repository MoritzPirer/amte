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

    /**
     * retrieves the CodeBlockInfo matching the given file extension
     * @param fileExtension the file extension that corresponds to the desired language
     * @return the CodeBlockInfo, or null if no CodeBlockInfo is associated with this extension
     */
    public CodeBlockInfo getByExtension(String fileExtension) {
        for (CodeBlockInfo info : loadedCodeBlockInfos.values()) {
            if (info.extensions().contains(fileExtension)) {
                return info;
            }
        }

        return null;
    }

    /**
     * retrieves the CodeBlockInfo for the requested language
     * @param language the language for which to retrieve the CodeBlockInfo
     * @return the CodeBlockInfo, or null if none is associated with this extension
     */
    public CodeBlockInfo getCodeBlockInfo(String language) {
        if (loadedCodeBlockInfos.containsKey(language)) {
            return loadedCodeBlockInfos.get(language);
        }

        return CodeBlockInfo.DUMMY;
    }
}
