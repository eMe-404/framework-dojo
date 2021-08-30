package dependency_injection.bean;


import dependency_injection.annotation.DojoComponent;

@DojoComponent
public class Foo {
    public static final String WELCOME_MESSAGE = "hello from Foo";

    public String sayHi() {
        return WELCOME_MESSAGE;
    }

    public String retrieveProcessorName() {
        return null;
    }
}
