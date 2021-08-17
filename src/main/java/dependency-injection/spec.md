## Reference implementation

The following dependency injection systems have passed the TCK:

- Google Guice 2.1
- KouInject
- OpenWebBeans
- Spring Framework 3.0
- Weld 1.0.0

# SBE

- This runtime environment is called the **container**
- A Java class is a managed bean if it meets all of the following conditions:
    - It is not an inner class.
    - It is a non-abstract class, or is annotated `@Decorator`.
    - It does not implement `javax.enterprise.inject.spi.Extension`.
    - It has an appropriate constructor - either:
        - the class has a constructor with no parameters, or
        - the class declares a constructor annotated `@Inject`.
    - e.g.

    ```java
    class Bar { .. }

    class PaymentProcessorImpl implements PaymentProcessor { ... }
    ```

- container responsible for typesafe dependency injection mechanism
    - e.g. The container provides built-in support for injection and contextual lifecycle management of the following kinds of bean
        - Managed beans
        - Producer methods and fields
- container can inject dependency by bean constructor

    ```java
    public class Bar {
    	private final Foo foo;

      @Inject
      Bar(Foo foo){
    		this.foo = foo;
      }
    } 
    ```

- If a bean class does not explicitly declare a constructor using @Inject, the constructor that accepts no parameters is the bean constructor.
- If a bean class has more than one constructor annotated @Inject, the container automatically detects the problem and treats it as a definition error.
- container can inject dependency by bean filed(non-static, non-final)

    ```java
    public class Bar {
    	@Inject private Foo foo;
    } 
    ```

- container can inject dependency by bean method(non-static, non-abstract, non-generic)

    ```java
    public class Bar {
    	private Foo foo;

    	@Inject public Foo setFoo(Foo foo){
    		this.foo = foo;
      }
    } 
    ```

- A bean class may declare multiple (or zero) initializer methods.
- The application may call initializer methods or bean constructor directly, but then no parameters will be passed to the method/constructor by the container, and there is also no lifecycle management for this instance
- For a given bean type, there may be multiple beans which implement the type
    - e.g.

    ```java
    class SynchronousPaymentProcessor implements PaymentProcessor {
        ...
    }

    class AsynchronousPaymentProcessor implements PaymentProcessor {
        ...
    }
    ```

- client can select specific bean implementation by `@Qualifier` without creating hard dependency between client and implementation
    - e.g.

    ```java
    @Synchronous
    class SynchronousPaymentProcessor
            implements PaymentProcessor {
        ...
    }

    @Asynchronous
    class AsynchronousPaymentProcessor
            implements PaymentProcessor {
        ...
    }

    //in the below case SynchronousPaymentProcessor will be injected
    @Inject @Synchronous PaymentProcessor paymentProcessor;

    // in the below case  AsynchronousPaymentProcessor will be injected
    @Inject @Asynchronous PaymentProcessor paymentProcessor;
    ```

- If an injection point declares no qualifier, the injection point has exactly one qualifier, the default qualifier @Default.
    - e.g.

    ```java
    //the following two are equivalent

    public class Order {
        @Inject
        public Order(@Default OrderProcessor processor) { ... }
    }

    public class Order {
        @Inject
        public Order(OrderProcessor processor) { ... }
    }
    ```

- custom qualifier type  should be defined as `@Retention(RUNTIME)` and should add @javax.inject.Qualifier meta-annotation
    - e.g.

    ```java
    @Qualifier
    @Retention(RUNTIME)
    @Target({METHOD, FIELD, PARAMETER, TYPE})
    public @interface Synchronous {}
    ```

- @Named can used as injection point qualifier, it will filed name as default member value
    - e.g.

    ```java
    @Inject @Named PaymentService paymentService;

    //above same as 
    @Inject @Named("paymentService") PaymentService paymentService;
    ```

- when container inject bean will follow certain order
    - injection > filed injection > method injection
    - super class injection > sub class injection
- all the bean have scope
    - e.g. they may be automatically created when needed and then automatically destroyed when the context in which they were created ends
- The process of matching a bean to an injection point is called typesafe resolution. Typesafe resolution usually occurs at application initialization time
- a bean type or bean name does not uniquely identify a bean. When resolving a bean at an injection point, the container considers bean type, qualifiers and selected alternatives
    - e.g

    ```java
    e.g.
    @PayBy(CHEQUE) class ChequePaymentProcessor implements PaymentProcessor { ... }
    @PayBy(CREDIT_CARD) class CreditCardPaymentProcessor implements PaymentProcessor { ... }

    //only check payment processor will be considared as candidate
    @Inject @PayBy(CHEQUE) PaymentProcessor paymentProcessor;
    ```

- the bean should have all the `required type` , if no required type was explicitly specified, the container assumes the required qualifier `@Default`
- the container should try to resolve **ambiguous dependency**
    - e.g. → the container should eliminate all eligible beans that are not alternatives, if there is exactly one bean remaining then select this bean, if more then one bean remains with alternative then select the highest priority
- A bean must declare all of the qualifiers that are specified at the injection point to be considered a candidate for injection.
- The container is required to support circularities in the bean dependency graph where at least one bean participating in every circular chain of dependencies has a normal scope, as defined in Normal scopes and pseudo-scopes. The container is not required to support circular chains of dependencies where every bean participating in the chain has a pseudo-scope.