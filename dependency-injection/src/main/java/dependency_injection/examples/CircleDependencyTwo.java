package dependency_injection.examples;

import javax.inject.Inject;

public class CircleDependencyTwo {
    private final CircleDependencyOne circleDependencyOne;

    @Inject
    public CircleDependencyTwo(final CircleDependencyOne circleDependencyOne) {
        this.circleDependencyOne = circleDependencyOne;
    }

    public CircleDependencyOne getCircleDependencyOne() {
        return circleDependencyOne;
    }
}
