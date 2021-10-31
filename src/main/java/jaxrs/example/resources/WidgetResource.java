package jaxrs.example.resources;

import jaxrs.example.entity.Widget;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/widget")
public class WidgetResource {

    @GET
    public Widget findWidget(@PathParam("id") int id) {
        return new Widget("1");
    }
}
