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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        final ArrayList<Class<?>> rootResourceClasses = new ArrayList<>(restfulApplication.getClasses());

        MatchedResource matchedResource = requestDispatcher.matchRequestHandler(req, rootResourceClasses, servletContext);
        Object deserializedObject = deserializeRequestBody(req, matchedResource);

        handlePostRequest(resp, servletContext, matchedResource, deserializedObject);
    }

    private Object deserializeRequestBody(HttpServletRequest req, MatchedResource matchedResource) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Method matchedResourceMethod = matchedResource.getMatchedResourceMethod();
        if (matchedResourceMethod.getParameterCount() >1) {
            throw new RuntimeException("post request only can accept one request body param");
        }
        Class<?> postRequestMethodParameterType = matchedResourceMethod.getParameterTypes()[0];

        return objectMapper.readValue(req.getInputStream(), postRequestMethodParameterType);
    }

    @SneakyThrows
    private void handlePostRequest(HttpServletResponse resp, ServletContext servletContext, MatchedResource matchedResource, Object obj) {
        Method requestHandlerMethod = matchedResource.getMatchedResourceMethod();
        Object response;

        Object capturingGroup = servletContext.getAttribute(CAPTURING_GROUP);
        if (Objects.nonNull(capturingGroup)) {
            //TODO need to handle sub resource with post condition
            Constructor<?> declaredConstructor = matchedResource.getMatchedResourceClass().getDeclaredConstructor(String.class);
            response = requestHandlerMethod.invoke(declaredConstructor.newInstance(capturingGroup.toString()), obj);
            servletContext.removeAttribute(CAPTURING_GROUP);
        } else {
            response = requestHandlerMethod.invoke(restfulApplication.retrieveInstanceByName(matchedResource.getMatchedResourceClass().getSimpleName()), obj);
        }

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
            throws IllegalAccessException, InvocationTargetException {
        Object response;
        Object capturingGroup = servletContext.getAttribute(CAPTURING_GROUP);
        if (Objects.nonNull(capturingGroup)) {
            response = requestHandlerMethod.invoke(restfulApplication.retrieveInstanceByName(matchedResource.getMatchedResourceClass().getSimpleName()), capturingGroup);
            servletContext.removeAttribute(CAPTURING_GROUP);
        } else {
            response = requestHandlerMethod.invoke(restfulApplication.retrieveInstanceByName(matchedResource.getMatchedResourceClass().getSimpleName()));
        }
        return response;
    }
}
