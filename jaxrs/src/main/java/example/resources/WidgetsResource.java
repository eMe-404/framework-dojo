package example.resources;

import example.entity.Widget;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;

@Path("/widgets")
public class WidgetsResource {
    @Path("/{id}")
    public WidgetResource getWidget(@PathParam("id") int id) {
        return new WidgetResource(id);
    }

    @GET
    public List<Widget> findAllWidget() {
        return List.of(new Widget("one"), new Widget("two"));
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
