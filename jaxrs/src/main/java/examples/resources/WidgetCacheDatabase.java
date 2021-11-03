package examples.resources;

import java.util.Map;

import static java.util.Map.entry;


public class WidgetCacheDatabase {
    public static final Map<String, String> CACHED_WIDGET_DATA;

    static {
        CACHED_WIDGET_DATA = Map.ofEntries(
                entry("1", "first widget"),
                entry("2", "second widget"),
                entry("3", "third widget")
        );
    }

}
