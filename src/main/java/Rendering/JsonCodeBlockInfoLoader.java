package Rendering;

import java.util.HashSet;
import java.util.Set;

class JsonCodeBlockInfoLoader implements CodeBlockInfoLoader {

    @Override
    public boolean knowsLanguage(String language) {
        System.out.println("knowsLanguage: _" + language + "_");

        if (language.equals("java")) {
            System.out.println("yuh huh");
            return true;
        }

        System.out.println("Nuh uh");
        return false;
    }

    @Override
    public CodeBlockInfo load(String language) {
        if (!language.equals("java")) {
            return null;
        }

        return new CodeBlockInfo(
            Set.of("private", "static", "final", "int", "void"),
            Set.of("stream", "get", "put", "size", "isEmpty"),
            Set.of("//"),
            Set.of(new StringPair("/*", "*/")),
            Set.of(new StringPair("\"", "\""), new StringPair("'", "'")),
            Set.of(" ", ".", ",", "-", "+", "/", "*", "=", ">", "<", "?", "/*", "*/", "//", "\n")
        );
    }
}
