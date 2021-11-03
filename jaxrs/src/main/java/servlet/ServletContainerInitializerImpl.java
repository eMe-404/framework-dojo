package servlet;

import applications.GeneralRestfulApplication;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.ws.rs.core.Application;
import java.util.Set;

@HandlesTypes(Application.class)
public class ServletContainerInitializerImpl implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        ctx.addServlet("JAXRSServlet", GeneralRestfulApplication.class);
    }
}
