package examples.resources;



import examples.entity.Attachment;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/widgets/{id}/{attachmentId}")
public class WidgetAttachmentsResource {
    @GET
    public Attachment findWidgetAttachmentById(@PathParam("attachmentId") int id) {
        return new Attachment("a attachment");
    }
}
