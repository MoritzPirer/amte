package Rendering;

class CodeBlockInfoMaster {
    private static CodeBlockInfoMaster instance = null;

    private final CodeBlockInfoLoader loader;

    private CodeBlockInfoMaster() {
        this.loader = new JsonCodeBlockInfoLoader();
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
        if (loader.knowsExtension(fileExtension)) {
            return loader.loadByExtension(fileExtension);
        }

        return null;
    }

    /**
     * retrieves the CodeBlockInfo for the requested language
     * @param language the language for which to retrieve the CodeBlockInfo
     * @return the CodeBlockInfo, or null if none is associated with this extension
     */
    public CodeBlockInfo getByLanguage (String language) {
        if (loader.knowsLanguage(language)) {
            return loader.loadByLanguage(language);
        }

        return CodeBlockInfo.DUMMY;
    }
}
