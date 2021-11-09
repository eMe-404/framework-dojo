package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import examples.resources.WidgetResource;
import examples.resources.WidgetsResource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import models.MatchedResource;

@WebServlet(name = "RestServlet", urlPatterns = {"/"}, loadOnStartup = 1)
public class RestfulServlet extends HttpServlet {

    public static final String CAPTURING_GROUP = "capturingGroup";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        ServletContext servletContext = getServletContext();
        RequestDispatcher requestDispatcher = new RequestDispatcher();
        try {
            MatchedResource matchedResource = requestDispatcher.matchRequestHandler(req, retrieveAllResource(), servletContext);
            handleRequest(resp, servletContext, matchedResource);

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException | IOException | RuntimeException e) {
            handleException(resp, e);
        } finally {
            servletContext.removeAttribute(CAPTURING_GROUP);
        }
    }

    private List<Class<?>> retrieveAllResource() {
        return List.of(WidgetsResource.class, WidgetResource.class);
    }


    private void handleRequest(HttpServletResponse resp, ServletContext servletContext, MatchedResource matchedResource)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, IOException {
        Method requestHandlerMethod = matchedResource.getMatchedResourceMethod();
        Object response = invokeResource(servletContext, matchedResource, requestHandlerMethod);
        printResponse(resp, response);


    }

    @SneakyThrows
    private void handleException(HttpServletResponse resp, Exception e) {
        if (e.getCause() instanceof ClassNotFoundException) {
            resp.setStatus(404);
        } else {
            resp.setStatus(500);
        }
        resp.getWriter().flush();
    }

    private void printResponse(HttpServletResponse resp, Object response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        resp.getWriter().write(objectMapper.writeValueAsString(response));
        resp.setStatus(200);
        resp.setContentType("application/json");
        resp.getWriter().flush();
    }

    private Object invokeResource(ServletContext servletContext, MatchedResource matchedResource, Method requestHandlerMethod)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Object response;
        Object capturingGroup = servletContext.getAttribute(CAPTURING_GROUP);
        if (Objects.nonNull(capturingGroup)) {
            response = requestHandlerMethod.invoke(
                    matchedResource.getMatchedResourceClass().getDeclaredConstructor(String.class).newInstance(capturingGroup.toString()));
            servletContext.removeAttribute(CAPTURING_GROUP);
        } else {
            response = requestHandlerMethod.invoke(matchedResource.getMatchedResourceClass().getDeclaredConstructor().newInstance());
        }
        return response;
    }
}
