package jaxrs.tests;

import jaxrs.core.RequestDispatcher;
import jaxrs.example.resources.WidgetAttachmentResource;
import jaxrs.example.resources.WidgetResource;
import jaxrs.example.resources.WidgetsResourceWithPathVariable;
import jaxrs.example.resources.WidgetsResourceWithoutPathVariable;
import jaxrs.model.RootResourceClassMatchingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequestDispatcherTests {


    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        requestDispatcher = new RequestDispatcher();
    }

    @Nested
    class WhenCandidate {
        @Test
        void should_identify_candidate_root_resource_classes_matching_the_request_given_single_root_path_without_path_variable() {
            final String requestUriPath = "/widgets";
            final List<Class<?>> rootResourceClasses = List.of(WidgetsResourceWithoutPathVariable.class, WidgetResource.class);

            RootResourceClassMatchingResult candidateResourceClasses = requestDispatcher.findCandidateResourceClasses(requestUriPath, rootResourceClasses);

            assertThat(candidateResourceClasses.getNotMatchedCapturingGroup()).isNull();
            assertThat(candidateResourceClasses.getMatchedRootClasses()).containsExactly(WidgetsResourceWithoutPathVariable.class);
        }

        @Test
        void should_identify_candidate_root_resource_classes_matching_the_request_given_root_path_with_path_variable() {
            final String pathVariable = "1";
            final String requestUriPath = "/widgets/" + pathVariable;
            final List<Class<?>> rootResourceClasses = List.of(WidgetsResourceWithPathVariable.class, WidgetResource.class);

            RootResourceClassMatchingResult candidateResourceClasses = requestDispatcher.findCandidateResourceClasses(requestUriPath, rootResourceClasses);

            assertThat(candidateResourceClasses.getNotMatchedCapturingGroup()).isEqualTo(pathVariable);
            assertThat(candidateResourceClasses.getMatchedRootClasses()).containsExactly(WidgetsResourceWithPathVariable.class);
        }

        @Test
        void should_throw_NotFoundException_given_no_matched_root_resource() {
            final String requestUriPath = "/customer/2066";
            final List<Class<?>> rootResourceClasses = List.of(WidgetsResourceWithoutPathVariable.class, WidgetResource.class);

            assertThatThrownBy(() -> requestDispatcher.findCandidateResourceClasses(requestUriPath, rootResourceClasses))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    class TestNest {
        @Test
        void name() {

        }
    }
}
