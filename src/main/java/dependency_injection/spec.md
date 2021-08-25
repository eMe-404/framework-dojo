## Reference implementation

The following dependency injection systems have passed the TCK:

- Google Guice 2.1
- KouInject
- OpenWebBeans
- Spring Framework 3.0
- Weld 1.0.0

# Q&A
* can we include `notNull` assertion?
* can we one fixture's result as a parameter of another fixture?
* can not run whole file test
* hard to write input related SBE 
* 


# SBE

### in Dojo context the runtime environment is called `container`, and it will be instantiated during the application start time

```java
class FooTest {

    @Test
    testMethod() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.getDojoContainer();
        assert dojoContainer != null;
    }

}
```

### A Java class is a managed bean if it meets all the following conditions:

- It is not an inner class.
- It has an appropriate constructor - either:
    - the class has a constructor with no parameters, or
    - the class declares a constructor annotated `@Inject`.
- the class annotated with @DojoComponent

```java

@DojoComponent
class Bar {
}

@DojoComponent
class PaymentProcessorImpl implements PaymentProcessor {
}
```

### all **managed bean** will manged by **DojoContainer** and loaded at application start time

```java
class FooTest {
    @Test
    testMethod() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.getDojoContainer();
        MyGreeter greeter = (MyGreeter) dojoContainer.retrieveBean("myGreeter");
        String helloMessage = greeter.sayHi();
        assertThat(helloMessage).isEqualTo("hello stranger!");
    }
}      
```

[comment]: <> (- container responsible for typesafe dependency injection mechanism)

[comment]: <> (    - e.g. The container provides built-in support for injection and contextual lifecycle management of the Managed beans)

### a managed bean can declare @Inject annotation at constructor to inject the dependency

```java
    public class Baz {
    private final MyGreeter greeter;

    @Inject
    Baz(Greeter greeter) {
        this.greeter = greeter;
    }

    public String retrieveGreeterBless() {
        return greeter.sayHi();
    }
}

public class BazTest {
    @Test
    testMethod() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.getDojoContainer();
        MyGreeter greeter = (MyGreeter) dojoContainer.retrieveBean("myGreeter");
        String fromGreeter = greeter.sayHi();
        Baz baz = (Baz) dojoContainer.retrieveBean("baz");
        String fromBaz = baz.retrieveGreeterBless();
        assertThat(fromBaz).isEqualTo(fromGreeter);
    }
}
  ```

### If a bean class does not explicitly declare a constructor using @Inject, the constructor that accepts no parameters is the bean constructor.

```java

@DojoComponent
public class MyGreeter implements Greeter {
    public String sayHi() {
        return "hello from MyGreeter";
    }
}

@DojoComponent
public class Baz {
    public final Greeter greeter;

    @Inject
    Baz(Greeter greeter) {
        this.greeter = greeter;
    }

    public String retrieveGreeterBless() {
        return greeter.sayHi();
    }
}

public class BazTest {
    @Test
    public void testMethod() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.getDojoContainer();
        Baz baz = (Baz) dojoContainer.retrieveBean("baz");
        assertThat(baz.greeter).instanceOf(MyGreeter);
    }
}
```

### If a bean class has more than one constructor annotated @Inject, the container automatically detects the problem and treats it as a definition error.

- e.g.

```java

@DojoComponent
public class MyGreeter implements Greeter {
    private FirstDependency a;
    private SecondDependency b;
    private ThridDependency c;

    @Inject
    MyGreeter(FirstDependency a, SecondDependency b) {
        this.a = a;
        this.b = b;
    }

    @Inject
    MyGreeter(ThridDependency c) {
        this.c = c;
    }

    public String sayHi() {
        return "hello from MyGreeter";
    }
}

public class BazTest {
    @Test
    public void testMethod() {
        assertThatThrownBy(() -> DojoContextUtils.initApplication())
                .isInstanceOf(DojoContextInitException.class)
                .message("failed to resolve bean MyGreeter, there is more than two constructor annotated with @Inject");
    }
}

```

### container can inject dependency by bean filed(non-static, non-final)

```java
    public class Bar {
    @Inject
    private Foo foo;
} 
```

### container can inject dependency by bean method(non-static, non-abstract, non-generic)

```java
    public class Bar {
    private Foo foo;

    @Inject
    public Foo setFoo(Foo foo) {
        this.foo = foo;
    }
} 
```

[comment]: <> (- The application may call initializer methods or bean constructor directly, but then no parameters will be passed to the method/constructor by the container, and there is also no lifecycle management for this instance)

### For a given bean type, there may be multiple beans which implement the type, client can select specific bean implementation by `@Qualifier`

```java

@DojoComponent
@Synchronous
class SynchronousPaymentProcessor implements PaymentProcessor {
}

@DojoComponent
@Asynchronous
class AsynchronousPaymentProcessor implements PaymentProcessor {
}

@DojoComponent
class Foo {
    // below @Synchronous  are self defined @Qualifier  
    @Inject
    @Synchronous
    PaymentProcessor paymentProcessor;
}

public class FooTest {
    @Test
    public void testMethod() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.getDojoContainer();
        Foo foo = (Foo) dojoContainer.retrieveBean("foo");
        assertThat(foo.paymentProcessor).instanceOf(SynchronousPaymentProcessor);
    }
}

class Baz {
    // below @Asynchronous  are self defined @Qualifier
    @Inject
    @Asynchronous
    PaymentProcessor paymentProcessor;
}

public class BazTest {
    @Test
    public void testMethod() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.getDojoContainer();
        Baz baz = (Baz) dojoContainer.retrieveBean("baz");
        assertThat(baz.paymentProcessor).instanceOf(AsynchronousPaymentProcessor);
    }
}
```

### If an injection point declares no qualifier, the injection point has exactly one qualifier, the default qualifier @Default.

```java
    //the following two are equivalent

public class Order {
    @Inject
    public Order(@Default OrderProcessor processor) {
    }
}

public class Order {
    @Inject
    public Order(OrderProcessor processor) {
    }
}

```

### custom qualifier type  should be defined as `@Retention(RUNTIME)` and should add `@javax.inject.Qualifier` meta-annotation

```java

@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface Synchronous {
}
```

### @Named can be used as injection point qualifier, and can also be used to select implementation with `value` member

```java
class Foo {
    @Inject
    @Named
    PaymentService paymentService;

    //above same as, if not provided member value then the type with first letter underscored is the default member value
    @Inject
    @Named("paymentService")
    PaymentService paymentService;

    //below will select AsynchronousPaymentProcessor as the injected dependency
    @Inject
    @Named("AsynchronousPaymentProcessor")
    PaymentService paymentService;
}
```

### when container inject bean will follow certain order

- injection > filed injection > method injection
- super class injection > subclass injection

### all the bean have scope, the default scope is @Singleton

- e.g. they may be automatically created when needed and then automatically destroyed when the context in which they
  were created ends

### a bean type or bean name does not uniquely identify a bean. When resolving a bean at an injection point, the container considers bean type, qualifiers and selected alternatives

```java
class Foo {
    @PayBy(CHEQUE)
    class ChequePaymentProcessor implements PaymentProcessor {
    }

    @PayBy(CREDIT_CARD)
    class CreditCardPaymentProcessor implements PaymentProcessor {
    }

    //only check payment processor will be considered as candidate
    @Inject
    @PayBy(CHEQUE)
    PaymentProcessor paymentProcessor;
}

```

### the bean should have all the `required type` , if no required type was explicitly specified, the container assumes the required qualifier `@Default`

```java

@DojoComponent
@Default
class SynchronousPaymentProcessor implements PaymentProcessor {

}

@DojoComponent
class AsynchronousPaymentProcessor implements PaymentProcessor {

}

@DojoComponent
class Foo {
    // SynchronousPaymentProcessor will be injected as it has the @Default Qualifier 
    @Inject
    PaymentProcessor paymentProcessor;
}
```

### the container should try to resolve **ambiguous dependency**

- e.g. → the container should eliminate all eligible beans that are not alternatives, if there is exactly one bean
  remaining then select this bean, if more than one bean remains with alternative then select the highest priority

```java

@DojoComponent
@Priority(5)
class SynchronousPaymentProcessor implements PaymentProcessor {

}

@DojoComponent
@Priority(1) //highest priority
class AsynchronousPaymentProcessor implements PaymentProcessor {

}

@DojoComponent
class Foo {
    // AsynchronousPaymentProcessor will be injected as it has high priority then the other candidate 
    @Inject
    PaymentProcessor paymentProcessor;
}
```

### The container is required to support circular dependency (TBC)

e.g. A-> B with construction injection, B -> A with construction injection will throw exception 