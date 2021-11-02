package dependency_injection.bean;

import dependency_injection.annotation.DojoComponent;

@DojoComponent
public class MyGreeter {

    public static final String WELCOME_MESSAGE = "hello from MyGreeter";

    public String sayHi() {
        return WELCOME_MESSAGE;
    }
}
