package jaxrs.utils;

public class URIHelper {
    public static String normalizePath(String rootPath) {
        return rootPath + "(/.*)?";
    }
}
