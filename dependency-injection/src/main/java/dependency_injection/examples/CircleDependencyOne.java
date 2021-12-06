package dependency_injection.examples;

import javax.inject.Inject;

public class CircleDependencyOne {
    private final CircleDependencyTwo circleDependencyTwo;

    @Inject
    public CircleDependencyOne(final CircleDependencyTwo circleDependencyTwo) {
        this.circleDependencyTwo = circleDependencyTwo;
    }

    public CircleDependencyTwo getCircleDependencyTwo() {
        return circleDependencyTwo;
    }
}
