package core;

import applications.GeneralRestfulApplication;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import examples.entity.Widget;
import lombok.SneakyThrows;
import models.MatchedResource;

@WebServlet(name = "RestServlet", urlPatterns = {"/"}, loadOnStartup = 1)
public class RestfulServlet extends HttpServlet {

    public static final String CAPTURING_GROUP = "capturingGroup";
    private ServletContext servletContext;
    private RequestDispatcher requestDispatcher;
    private GeneralRestfulApplication restfulApplication;

    @Override
    public void init() throws ServletException {
        super.init();
        restfulApplication = new GeneralRestfulApplication();
        restfulApplication.scanPackage();
        servletContext = getServletContext();
        requestDispatcher = new RequestDispatcher();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final ArrayList<Class<?>> rootResourceClasses = new ArrayList<>(restfulApplication.getClasses());

            MatchedResource matchedResource = requestDispatcher.matchRequestHandler(req, rootResourceClasses, servletContext);
            handleRequest(resp, servletContext, matchedResource);

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException | IOException | RuntimeException e) {
            handleException(resp, e);
        } finally {
            servletContext.removeAttribute(CAPTURING_GROUP);
        }

    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final ArrayList<Class<?>> rootResourceClasses = new ArrayList<>(restfulApplication.getClasses());

        MatchedResource matchedResource = requestDispatcher.matchRequestHandler(req, rootResourceClasses, servletContext);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Widget widget = objectMapper.readValue(req.getInputStream(), Widget.class);

        handlePostRequest(resp, servletContext, matchedResource, widget);
    }

    @SneakyThrows
    private void handlePostRequest(HttpServletResponse resp, ServletContext servletContext, MatchedResource matchedResource, Object argument) {
        Method requestHandlerMethod = matchedResource.getMatchedResourceMethod();
        Object response1;
        Object capturingGroup = servletContext.getAttribute(CAPTURING_GROUP);
        if (Objects.nonNull(capturingGroup)) {
            Constructor<?> declaredConstructor = matchedResource.getMatchedResourceClass().getDeclaredConstructor(String.class);
            response1 = requestHandlerMethod.invoke(declaredConstructor.newInstance(capturingGroup.toString()), argument);
            servletContext.removeAttribute(CAPTURING_GROUP);
        } else {
            response1 = requestHandlerMethod.invoke(matchedResource.getMatchedResourceClass().getDeclaredConstructor().newInstance(), argument);
        }
        Object response = response1;
        printResponse(resp, response);

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
