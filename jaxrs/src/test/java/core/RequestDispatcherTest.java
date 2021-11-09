package core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import examples.resources.WidgetResource;
import examples.resources.WidgetsResource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import models.MatchedResource;
import models.RootResourceClassMatchingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RequestDispatcherTest {


    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        requestDispatcher = new RequestDispatcher();
    }

    @Test
    void should_chose_request_handler_method_based_on_http_request() {
        String resourceMethod = "findWidget";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn("/widgets/3");
        given(request.getMethod()).willReturn("GET");

        MatchedResource matchedResource =
                requestDispatcher.matchRequestHandler(request, List.of(WidgetsResource.class), Mockito.mock(ServletContext.class));

        assertThat(matchedResource.getMatchedResourceMethod().getName()).isEqualTo(resourceMethod);

    }

    @Nested
    class MatchRootResourceClasses {
        @Test
        void should_identify_candidate_root_resource_classes_matching_the_request_given_single_root_path_without_path_variable() {
            final String requestUriPath = "/widgets";
            final List<Class<?>> rootResourceClasses = List.of(WidgetsResource.class, WidgetResource.class);

            RootResourceClassMatchingResult candidateResourceClasses =
                    requestDispatcher.findCandidateResourceClasses(requestUriPath, rootResourceClasses);

            assertThat(candidateResourceClasses.getNotMatchedCapturingGroup()).isNull();
            assertThat(candidateResourceClasses.getMatchedRootClasses()).containsExactly(WidgetsResource.class);
        }

        @Test
        void should_identify_candidate_root_resource_classes_matching_the_request_given_root_path_with_path_variable() {
            final String pathVariable = "/configs";
            final String requestUriPath = "/widgets" + pathVariable;
            final List<Class<?>> rootResourceClasses = List.of(WidgetsResource.class, WidgetResource.class);

            RootResourceClassMatchingResult candidateResourceClasses =
                    requestDispatcher.findCandidateResourceClasses(requestUriPath, rootResourceClasses);

            assertThat(candidateResourceClasses.getNotMatchedCapturingGroup()).isEqualTo(pathVariable);
            assertThat(candidateResourceClasses.getMatchedRootClasses()).containsExactly(WidgetsResource.class);
        }

        @Test
        void should_throw_NotFoundException_given_no_matched_root_resource() {
            final String requestUriPath = "/customer/2066";
            final List<Class<?>> rootResourceClasses = List.of(WidgetsResource.class, WidgetResource.class);

            assertThatThrownBy(() -> requestDispatcher.findCandidateResourceClasses(requestUriPath, rootResourceClasses))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    class MatchResourceMethods {
        @Test
        void should_return_all_methods_in_root_resource_classes_given_capturing_group_empty() {
            String capturingGroup = "/";
            Class<WidgetsResource> resourceClassOne = WidgetsResource.class;
            String expectedMethodsName = "findAllWidget";

            Set<Method> matchedMethod =
                    requestDispatcher.matchResourceMethods(capturingGroup, Set.of(resourceClassOne), Mockito.mock(ServletContext.class));
            Set<String> methodNames = matchedMethod.stream().map(Method::getName).collect(Collectors.toSet());

            assertThat(methodNames).contains(expectedMethodsName);
        }

        @Test
        void should_return_matched_sub_resource_method_given_root_resource_class_contains_sub_resource_method() {
            String subResourceMethodName = "retrieveConfigs";

            Set<Method> matchedMethods =
                    requestDispatcher.matchResourceMethods("/configs", Set.of(WidgetsResource.class), Mockito.mock(ServletContext.class));
            Set<String> methodNames = matchedMethods.stream().map(Method::getName).collect(Collectors.toSet());

            assertThat(methodNames).containsExactly(subResourceMethodName);
        }

        @Test
        void should_return_matched_resource_method_given_sub_resource_locator() {
            String resourceMethod = "findWidget";

            Set<Method> matchedMethods =
                    requestDispatcher.matchResourceMethods("/1", Set.of(WidgetsResource.class), Mockito.mock(ServletContext.class));
            Set<String> methodNames = matchedMethods.stream().map(Method::getName).collect(Collectors.toSet());

            assertThat(methodNames).contains(resourceMethod);
        }
    }
}
