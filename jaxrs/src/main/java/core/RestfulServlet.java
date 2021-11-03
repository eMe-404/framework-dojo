package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import examples.resources.WidgetResource;
import examples.resources.WidgetsResource;
import models.MatchedResource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "RestServlet", urlPatterns = {"/"}, loadOnStartup = 1)
public class RestfulServlet extends HttpServlet {

    public static final String CAPTURING_GROUP = "capturingGroup";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        RequestDispatcher requestDispatcher = new RequestDispatcher();
        MatchedResource matchedResource = null;
        Class<?> requestHandlerRootResource = null;
        try {
            matchedResource = requestDispatcher.matchRequestHandler(req, List.of(WidgetsResource.class, WidgetResource.class), servletContext);
            requestHandlerRootResource = matchedResource.getMatchedResourceClass();
            Method requestHandlerMethod = matchedResource.getMatchedResourceMethod();
            Object response;
            Object capturingGroup = servletContext.getAttribute(CAPTURING_GROUP);
            if (Objects.nonNull(capturingGroup)) {
                response = requestHandlerMethod.invoke(requestHandlerRootResource.getDeclaredConstructor(String.class).newInstance(capturingGroup.toString()));
                servletContext.removeAttribute(CAPTURING_GROUP);
            } else {
                response = requestHandlerMethod.invoke(requestHandlerRootResource.getDeclaredConstructor().newInstance());
            }
            ObjectMapper objectMapper = new ObjectMapper();
            resp.getWriter().write(objectMapper.writeValueAsString(response));
            resp.setStatus(200);
            resp.setContentType("application/json");
            resp.getWriter().flush();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            if (e.getCause() instanceof ClassNotFoundException) {
                resp.setStatus(404);
            } else {
                resp.setStatus(500);
            }
            resp.getWriter().flush();

        } catch (RuntimeException exp) {
            if (exp.getCause() instanceof ClassNotFoundException) {
                resp.setStatus(404);
                resp.getWriter().flush();
            } else {
                throw exp;
            }

        }
        finally {
            servletContext.removeAttribute(CAPTURING_GROUP);
        }
    }
}
