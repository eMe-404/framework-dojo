package dependency_injection.examples;

import dependency_injection.annotation.DojoComponent;
import javax.inject.Inject;

@DojoComponent
public class Bar {
    @Inject
    private MyGreeter myGreeter;
    @Inject
    private Foo foo;

    public String retrieveGreeterBless() {
        return myGreeter.sayHi();
    }

    public String retrieveCombinedMessage() {
        return myGreeter.sayHi() + " AND " + foo.sayHi();
    }
}
