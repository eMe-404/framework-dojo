package dependency_injection.bean;

import dependency_injection.annotation.DojoComponent;
import javax.inject.Inject;

@DojoComponent
public class Baz {
    private final MyGreeter myGreeter;

    @Inject
    public Baz(final MyGreeter myGreeter) {
        this.myGreeter = myGreeter;
    }

    public String retrieveGreeterBless() {
        return myGreeter.sayHi();
    }
}
