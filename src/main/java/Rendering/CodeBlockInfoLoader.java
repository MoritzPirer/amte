package Rendering;

import java.util.Map;

interface CodeBlockInfoLoader {

    boolean knowsLanguage(String language);
    CodeBlockInfo loadByLanguage(String language);

    boolean knowsExtension(String extension);
    CodeBlockInfo loadByExtension(String extension);
}
