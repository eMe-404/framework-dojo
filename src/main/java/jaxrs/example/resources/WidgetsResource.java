package jaxrs.example.resources;

import jaxrs.example.entity.Widget;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/widgets")
public class WidgetsResource {
    @GET
    @Path("/{id}")
    public Widget findWidgetById(@PathParam("id") int id) {
        return new Widget("one");
    }

    @GET
    public List<Widget> findAllWidget() {
        return List.of(new Widget("one"), new Widget("two"));
    }
}
