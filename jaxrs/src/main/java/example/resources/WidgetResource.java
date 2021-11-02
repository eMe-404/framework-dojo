package example.resources;

import example.entity.Widget;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/widget")
public class WidgetResource {

    private final int id;

    public WidgetResource(int id) {
        this.id = id;
    }

    @GET
    public Widget findWidget() {
        return new Widget(String.valueOf(id));
    }

    @POST
    public void storeWidget() {

    }
}
