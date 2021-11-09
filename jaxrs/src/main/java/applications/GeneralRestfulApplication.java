package applications;

import java.util.Set;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.core.Application;
import java.io.IOException;

public class GeneralRestfulApplication extends Application implements Servlet {
    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public Set<Class<?>> getClasses() {
        return super.getClasses();
    }
}
