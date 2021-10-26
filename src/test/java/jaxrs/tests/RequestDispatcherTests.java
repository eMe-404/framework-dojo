package jaxrs.tests;

import jaxrs.core.RequestDispatcher;
import jaxrs.example.resources.WidgetResource;
import jaxrs.example.resources.WidgetsResource;
import jaxrs.model.RootResourceClassMatchingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestDispatcherTests {


    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        requestDispatcher = new RequestDispatcher();
    }

    @Test
    void should_identify_candidate_root_resource_classes_matching_the_request() {
        final String requestUriPath = "/widgets";
        final List<Class<?>> rootResourceClasses = List.of(WidgetsResource.class, WidgetResource.class);

        RootResourceClassMatchingResult candidateResourceClasses = requestDispatcher.findCandidateResourceClasses(requestUriPath, rootResourceClasses);

        assertThat(candidateResourceClasses.getNotMatchedCapturingGroup()).isNull();
        assertThat(candidateResourceClasses.getMatchedRootClasses()).containsExactly(WidgetsResource.class);
    }
}
