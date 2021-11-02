package utils;

public class URIHelper {
    public static String normalizePath(String rootPath) {
//        String encodedPath = URLEncoder.encode(rootPath, StandardCharsets.UTF_8);
        String pathVariableReplacedPath = rootPath.replaceAll("\\{[a-zA-Z]+}", "([^/]+?)");
        if (pathVariableReplacedPath.endsWith("/")) {
            pathVariableReplacedPath = pathVariableReplacedPath.substring(0, pathVariableReplacedPath.length() - 1);
        }

        return pathVariableReplacedPath + "(/.*)?";
    }
}
