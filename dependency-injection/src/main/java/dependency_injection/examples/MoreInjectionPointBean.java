package dependency_injection.examples;

import javax.inject.Inject;

public class MoreInjectionPointBean {
    @Inject
    public MoreInjectionPointBean() {
    }

    @Inject
    public MoreInjectionPointBean(String message) {

    }
}
