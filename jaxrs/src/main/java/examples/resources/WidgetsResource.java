package examples.resources;

import examples.entity.Widget;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static examples.resources.WidgetCacheDatabase.CACHED_WIDGET_DATA;

@Path("/widgets")
public class WidgetsResource {
    @Path("/{id}")
    public WidgetResource getWidget(@PathParam("id") String id) {
        return new WidgetResource(id);
    }

    @GET
    public List<Widget> findAllWidget() {
        LinkedList<Widget> widgets = new LinkedList<>();
        CACHED_WIDGET_DATA.forEach((id, name) -> widgets.push(Widget.builder().id(id).name(name).build()));
        return widgets;
    }

    @Path("/configs")
    @GET
    public Map<String, String> retrieveConfigs() {
        return Map.of(
                "size", "60cm",
                "length", "20cm"
        );
    }

    @Path("/counts")
    @GET
    public int totalCount() {
        return 999;
    }
}
