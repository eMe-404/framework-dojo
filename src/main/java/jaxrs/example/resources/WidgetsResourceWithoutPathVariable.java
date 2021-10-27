package jaxrs.example.resources;

import jaxrs.example.entity.Widget;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/widgets")
public class WidgetsResourceWithoutPathVariable {
    @GET
    public List<Widget> findAllWidget() {
        return List.of(new Widget("one"), new Widget("two"));
    }
}
