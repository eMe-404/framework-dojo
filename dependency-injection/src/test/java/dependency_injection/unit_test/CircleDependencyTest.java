package dependency_injection.unit_test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dependency_injection.DojoContainer;
import dependency_injection.DojoContextHelper;
import dependency_injection.examples.CircleDependencyOne;
import dependency_injection.exception.DojoContextInitException;
import org.junit.jupiter.api.Test;

class CircleDependencyTest {
    @Test
    void should_throw_exception_if_there_is_circular_dependency() {
        final DojoContainer dojoContainer = DojoContextHelper.newContainer();

        assertThatThrownBy(() -> dojoContainer.register(CircleDependencyOne.class))
                .isInstanceOf(DojoContextInitException.class)
                .hasMessage("circular dependency encountered");
    }
}
