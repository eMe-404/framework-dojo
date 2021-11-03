package examples.resources;

import examples.entity.Widget;
import lombok.NoArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Map;
import java.util.Objects;

import static examples.resources.WidgetCacheDatabase.CACHED_WIDGET_DATA;
import static java.util.Map.entry;

@Path("/widget")
@NoArgsConstructor
public class WidgetResource {

    private String id;

    public WidgetResource(String id) {
        this.id = id;
    }

    @GET
    public Widget findWidget() {
        // TODO: 2021/11/3 how link the id from path parameter
        String name = CACHED_WIDGET_DATA.get(id);
        if (Objects.isNull(name)) {
            throw new NotFoundException("widget not found");
        }

        return Widget.builder()
                .name(name)
                .id(id)
                .build();
    }

    @POST
    public void storeWidget() {

    }
}
