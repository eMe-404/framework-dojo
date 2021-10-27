package jaxrs.example.resources;

import jaxrs.example.entity.Widget;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/widgets/{id}")
public class WidgetsResourceWithPathVariable {
    @GET
    public Widget findWidgetById(@PathParam("id") int id) {
        return new Widget("one");
    }
}
