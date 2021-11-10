package examples.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;


public class WidgetCacheDatabase {
    public static final Map<String, String> CACHED_WIDGET_DATA;

    static {
        Map<String, String> unmodifiableMap = Map.ofEntries(
                entry("1", "first widget"),
                entry("2", "second widget"),
                entry("3", "third widget")
        );
        CACHED_WIDGET_DATA = new HashMap<>(unmodifiableMap);
    }

}
