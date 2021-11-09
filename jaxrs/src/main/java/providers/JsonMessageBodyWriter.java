package providers;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import examples.entity.Widget;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(value = APPLICATION_JSON)
public class JsonMessageBodyWriter implements MessageBodyWriter<Widget> {
    @Override
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(Widget.class);
    }

    @Override
    public void writeTo(Widget widget,
                        Class type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        final PrintWriter printWriter = new PrintWriter(entityStream);
        printWriter.print("test");
        printWriter.flush();
        printWriter.close();
    }
}
