# Using Contexts and Dependency Injection (CDI)

## Introduction

This article is about *Jakarta Contexts and Dependency Injection*, which is an official
[standard](https://jakarta.ee/specifications/cdi/4.1/jakarta-cdi-spec-4.1) with multiple implementations.

The *lifecycle contexts* part of *CDI* is mostly ignored in this article. The focus is mainly on
the *dependency injection* part, covering only the basics. CDI features such as *event notification*,
*decorators*, *disposer methods* etc. are also out of scope.

## Dependency injection

Let's first briefly discuss the notion of *dependency injection* in general, without referring to CDI.

In Java "enterprise" applications, the use of *dependency injection* is a widely used best practice.
Instead of the inflexible approach of having instances of classes internally creating their dependencies themselves,
these dependencies of the instance of a class are *injected* into the class instance at "construction
time". Often, this injection is *constructor-based*.

The notion of *dependency injection* is explained well in a
[Wikipedia article on Dependency Injection](https://en.wikipedia.org/wiki/Dependency_injection).
In this article, compare the Java example without dependency injection with the example that uses
constructor injection (or the one using setter injection). The example without dependency injection
uses a hard-coded dependency, which is quite inflexible and very hard to test in general.
On the other hand, the example with dependency injection is quite flexible, enabling different implementations
of the dependencies to be injected, including "mock" implementations for testing purposes.

The example without dependency injection:

```java
public class Client {
  private Service service;

  Client() {
    // The dependency is hard-coded.
    this.service = new ExampleService();
  }
}
```

The example with constructor-based dependency injection:

```java
public class Client {
    private Service service;

    // The dependency is injected through a constructor.
    Client(final Service service) {
        if (service == null) {
            throw new IllegalArgumentException("service must not be null");
        }
        this.service = service;
    }
}
```

Just imagine that in the example without dependency injection the created dependency is a
`javax.sql.DataSource`. Surely the properties of the `DataSource` could be looked up from some
property file (which one would depend on some "profile", such as "dev", "prod" etc.), but this gets
quite problematic very quickly, and certainly does not help in testing the `Client` class.
It would also violate the *Single Responsibility Principle*.

In "enterprise" Java, *dependency injection* has been popularized by the *Spring framework*.

It is preferable to use *Java interfaces* as the *required type* at *injection points*.
This does not always apply, of course, but it should be a common practice for "application components"
such as DAO (data access object) types, service types, etc.

There are multiple reasons for using Java interface types at injection points:
* Writing a Java interface forces us to think about the *API contract* of the component, without being distracted by any implementation details
  * Often, multiple implementations of the interface will "organically start to appear", in particular for testing purposes
  * So the idea is *not* to start with implementation classes and to then discover the common contract as Java interface
  * Instead, the idea is to *first think about the contract* as Java interface, and then let implementation follow
* If needed, the interface allows for different implementations to be injected, such as a synchronous and asynchronous implementation of a certain dependency
* This *flexibility* becomes really beneficial for testing purposes
  * For example, for testing purposes it may be desirable to have very lightweight service/DAO implementations that do not need any database, for example
  * So, when unit testing a Jakarta REST resource, why not use an injected service implementation requiring no database?
  * This may also reduce the need for traditional mocking (using Mockito etc.), which is often sensitive in that it may depend on internal implementation details
* In a layered application architecture, interfaces as "layer contracts" offer good *isolation* between layers
  * That is, higher layers are shielded from implementation details of lower layers
  * Again, this increases flexibility (especially w.r.t. testing)
* The dependency injection is still *compile-time typesafe*, just as class-based dependency injection

It is also desirable to use *constructor-based injection* and stay away from field-based injection.
In general, it is a best practice to have constructors create fully initialized objects, thus making
*reasoning about the possible state(s) of the object* easier. Besides that, there is a very practical
reason to use constructor-based injection (and at least stay away from field-based injection), and that
is the possibility to *manually create* an instance of the dependency in testing scenarios.

For example, when unit testing a Jakarta REST resource, and using a light-weight "test" service implementation
as dependency, it should be possible to just create the JAX-RS resource in a oneliner, using 2 constructor
calls (namely one for the service, and one for the JAX-RS resource).

So, in a *layered web application architecture*, layers expose themselves via Java interfaces.
In particular, the service and DAO layers offer their API contracts as Java interfaces.
Again, a Jakarta REST resource (in the web layer) depending on an injected service of a service interface
type is quite flexible for testing purposes, where the injected service implementation can be quite
lightweight, requiring no database connection. A plain thread-safe Java collection can play the role
of a database in testing scenarios.

In such a *layered web application architecture*, there would also be a *model* "layer" (although the
word layer does not really apply here). Typically, it would contain *immutable Java record classes*,
which make great *data transfer objects* (DTOs) to be passed around in and across layers.
It is natural to have different *data classes* for the same domain model "entity". It is not wise
to use the same class for mapping to JSON, XML, database records etc. Keep them as distinct classes.
After all, according to the *Single Responsibility* principle mentioned in *SOLID*, a class should have
only one reason to change. That's very hard to accomplish if the same data class maps to different
representations such as JSON, XML and database records. This is especially the case for *JPA entity*
classes. They make very poor DTOs, and should be short-lived and not escape the `EntityManager`
(or Hibernate `Session`) in which they are used.

Ok, we cannot manually "wire" an application using just Java interfaces, because interfaces have no
constructors. So we need some *dependency injection container* to do the wiring for us. In Spring
applications that would be the Spring container (`ApplicationContext`). In Jakarta EE applications that
would be the *CDI container*.

To summarize the *best practices* mentioned so far, keep the following in mind:
* *Dependency injection* is much more flexible (and "testable") than internal hard-coded dependency creation
* *Interface-based* dependency injection is much more flexible (and "testable") than class-based dependency injection
* *Constructor-based* dependency injection (where the constructor can also be invoked manually) is much more flexible (and "testable") than field-based dependency injection

The next section is about the *dependency injection* part of the *CDI container*.

## Dependency injection through CDI

*CDI* is everywhere in Jakarta EE applications, although more and more CDI is its own solution on top
of which Jakarta EE uses it quite extensively.

So it is important to understand CDI. I feel it is often poorly understood (and I am also learning
myself about CDI). What I often encounter is a mix of annotations on a class, including the CDI `Inject`
annotation, and also the `Resource` annotation for JNDI resources, which cannot be set on constructor
parameters. We can do better than that, as we will see. Recall the best practices mentioned in the
preceding section, in particular constructor-based injection. That's where CDI can really help us.

So let's discuss the basics of CDI dependency injection, and how it supports the above-mentioned
best practices.

To understand CDI dependency injection, we first have to understand *qualifiers* in CDI.

### Qualifiers

At *injection points* we not only provide required types (so preferably interface types where
feasible), but also 0 or more *required qualifiers*. These qualifiers are *Java annotations*,
and they play an important role in injection resolution, as we will see.

What is a *qualifier*? Let's consider an example of a qualifier annotation definition from the CDI
specification:

```java
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface Synchronous {
}
```

A *qualifier type* is a *Java annotation type* with the following extra constraints:
* It has the `Qualifier` meta-annotation
* It has *retention* `RUNTIME`, so Java reflection-based tooling such as CDI has access to the annotation at runtime
* Typically, but not necessarily, the *target* includes `METHOD`, `FIELD`, `PARAMETER` and `TYPE`
* Typically, but not necessarily, it contains meta-annotation `Documented` as well

Qualifier annotations can also *contain members*. For example, a `Color` qualifier can contain the actual
color as annotation member. These annotation members are also important for resolving dependencies at injection
points, should at least one of the required qualifiers contain annotation members.

Qualifier annotation members may be meta-annotated with the `Nonbinding` meta-annotation. Qualifier
annotation members that are meta-annotated as being `Nonbinding` are *ignored* when two annotations
of the qualifier type are compared for equality.

We can also create our own *qualifiers* in the application code base. If it makes sense, by all means
introduce qualifier types as part of the application code base.

### Managed beans, and their types and qualifiers

In CDI, there are not that many restrictions on what classes can act as a *managed bean class*.
Interfaces can not act as managed bean classes. The most important restrictions on managed bean classes are:
* The class is *not an inner class*
* The class is a *non-abstract class*
* The class has an appropriate *constructor*
  * Either a *no-parameter constructor*
  * Or a *constructor* annotated with the `Inject` annotation

A managed bean class has a set of *bean types*. Roughly the set of bean types of a managed bean class
is the class and all its super-types, including interface types, if any, and type `java.lang.Object`.
Only *legal bean types* are included, which roughly means that *type variables* and *parameterized
types containing wildcards* are excluded from the set of bean types of a managed bean class.

The set of bean types can be used for dependency injection resolution, so it may be desirable to limit
that set of bean types. In particular, if a managed bean type implements a Java interface, we may want
to include that interface type as bean type, yet exclude the managed bean type itself as bean type.

For example:

```java
@Typed({PaymentService.class})
public class PaymentServiceImpl implements PaymentService {
    // ...
}
```

So, in this example the managed bean class is `PaymentServiceImpl`, but only interface type `PaymentService`
and its supertypes are bean types of the managed bean. In this case that would probably be `PaymentService`
and `java.lang.Object`.

Recalling the best practices mentioned earlier, it is important to use this `Typed` annotation in order
to *prevent class-based dependency injection* for implementation classes that implement appropriate
interfaces. If we don't, then we would not enforce the best practice of interface-based dependency injection.

Besides *bean types*, a *managed bean class* contains 0 or more (explicit) *qualifiers*. These
qualifiers are the *qualifier* annotations of the managed bean class.

If an injection point has no qualifier, the implicit qualifier is `Default`. All injection points have
implicit qualifier `Any`, so for practical purposes we can ignore the latter qualifier.

So, among the important properties of a managed bean class are:
* the *bean types* of the managed bean class
* the *qualifiers* of the managed bean class

These two properties are important for dependency injection resolution, which is the topic of the next
section.

### Typesafe resolution of injection

Matching managed beans to injection points is called *typesafe resolution*. See
[typesafe resolution](https://jakarta.ee/specifications/cdi/4.1/jakarta-cdi-spec-4.1#typesafe_resolution).

This is resolution at the type level, so it has no relationship to (contextual) instances of the bean,
or to their scopes. That is entirely unrelated. Typesafe resolution typically takes place at
application *initialization* time.

It is very important to understand that *typesafe resolution* is not based on just the *required types*
at the injection point, but on the *combination of required type and required qualifiers*.

So if the bean class contains a custom qualifier (and therefore does not contain implicit qualifier
`Default`) and at the injection point no qualifier is mentioned (so the implicit qualifier is `Default`
there) there is *no match*. So, injection resolution is based on *type plus qualifiers*, not on just
the type.

So, a *bean is assignable to an injection point* if:
* The bean has a bean type that matches the *required type* at the injection point, and
* The bean has *all required qualifiers* (again mind qualifier `Default` if no qualifiers were explicitly given)

Again, if the required qualifier annotation contains annotation members, they are used in this matching process.
Yet `Nonbinding` annotated qualifier annotation members are *ignored* when comparing two instances of
the qualifier annotation type.

Of course, it is an error if no bean is assignable to an injection point. If, however, multiple beans
are assignable, there are some rules that may help in resolving the "best match". This is not discussed
here.

Note that *dependency injection* in CDI is *type-safe*, requiring no JNDI name "string matching". This is a
substantial advantage of CDI compared to JNDI resource injection based on JNDI names. CDI's dependency injection
is based on "qualified types", so to speak, where the qualifiers themselves are instances of annotation
types.

It might be interesting to note that qualifier types occur in places where we might not directly
expect them. For example, `org.eclipse.microprofile.health.Liveness` and `org.eclipse.microprofile.health.Readiness`
are qualifier types because they are meta-annotated as `Qualifier` and have retention `RUNTIME`.
So we have qualifiers even where we might not expect them, e.g. in standard health checks.
Indeed, Jakarta EE and MicroProfile use CDI a lot themselves.

### Producer methods and fields

So-called *producer methods* may act as a source of objects to be injected. See
[producer methods](https://jakarta.ee/specifications/cdi/4.1/jakarta-cdi-spec-4.1#producer_method).

Producer methods are methods where the return type is annotated with annotation `Produces`.

This return type may have multiple qualifier annotations that we also encounter on managed beans. Of course,
these annotations play a role in typesafe injection resolution, in the same way that managed bean qualifier
annotations do.

As for the *bean types of a producer method*, they depend on the *method return type* itself.
These types include the return type and its supertypes, but excludes any type that is not a
*legal bean type* (such as type variables).

Again, keeping the above-mentioned best practices in mind, by all means use *interface types* as
producer method return types to the extent feasible.

Besides producer methods we can also have *producer fields*, whether static or non-static.

For the bean types of producer fields analogous remarks apply as to producer methods.

Producer fields play a very important role in retaining *constructor-based injection* while depending
on `Resource`-annotated JNDI resources as well. The idea is to turn the JNDI resource into a
producer field, so that only constructor-based injection is needed to inject them.

For example:

```java
@Dependent
public class DataSourceProducer {

    @Produces
    @PaymentDataSource
    @Resource(name = "jdbc/paymentDataSource")
    private DataSource dataSource;
}
```

The JNDI resource for the `DataSource` is turned into a producer field, which can then be used for
constructor-based typesafe injection.

This can become impractical if there is a lot of configuration data in the form of JNDI "resources".
Yet for configuration data, consider the use of
[MicroProfile Config](https://download.eclipse.org/microprofile/microprofile-config-3.1/microprofile-config-spec-3.1.html),
which is fully aware of CDI and plays well with it.

### CDI introspection

It can be helpful for our understanding to use "CDI introspection" in an application, in order to find the
managed beans. Consider filtering the results on the package names, in order to exclude managed beans
from libraries. The following code can be used as a starting point:

```java
        BeanManager beanManager = CDI.current().getBeanManager();
        // This returns all CDI beans, of any type and any set of qualifiers
        // Recall that all CDI beans (implicitly) have qualifier Any
        // If we leave out this qualifier annotation argument, qualifier Default is assumed
        Set<Bean<?>> allCdiBeans = beanManager.getBeans(Object.class, Any.Literal.INSTANCE);
```

The returned beans have properties such as:
* Name (typically not needed for injection resolution)
* Bean class (fully qualified class name)
* The bean types
* The qualifiers
* The scope (not treated here)
* So-called stereotypes (not discussed here)
* Injection points

Be careful when interpreting the results. If a bean class has a producer method, it is likely that
there are 2 entries for the same bean class, but with different bean types (one entry for the bean
class, and one entry for the producer method return type).

## Additional remarks

Finally, I would like to conclude with some remarks.

Adding annotations to a class may be intrusive, depending on the role and expected processing of
the annotation. Also, some sets of annotations should not be used together. A clear example
is JAXB annotations and JSONB annotations. Yet also EJB-defining annotations should not be used
on CDI managed beans if they do not offer anything of value for that bean. For example, the
`Stateless` annotation for stateless session beans is not always needed. If there is no reason for the
CDI managed bean to be an EJB (in this case a stateless session bean), what is the point of adding that
annotation? The principle I have in mind here is the *principle of least power*. All enabled power (in this
case in the form of additional annotations) comes with *additional mental load* (and potentially with
additional performance degradation).

Starting with CDI 4.0, the default *bean discovery mode* is "annotated". This is a sensible default
(from CDI 4.0 onwards). This roughly means that only "annotated" classes on the class path are included as
CDI beans. More precisely, only bean classes with
[bean defining annotations](https://jakarta.ee/specifications/cdi/4.1/jakarta-cdi-spec-4.1#bean_defining_annotations)
are discovered as bean classes in this bean discovery mode.

With a bean discovery mode of "annotated" there are fewer "unexpected bean classes". That is a good
thing, because that also means that it is easier to get a clear mental picture of the application
wiring, without there being any implicit unexpected application wiring. This unexpected application
wiring is probably class-based rather than interface-based, which defeats the purpose of
interface-based injection.

Below the bean discovery mode is chosen explicitly, but it could have been left out for version 4.0:

```xml
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
       bean-discovery-mode="annotated" version="4.0">
    <!-- bean-discovery-mode "annotated" (the default since version 4.0), to prevent the occurrence of lots of "unexpected" CDI beans -->
</beans>
```

In this article *scoping* (so the "context part" of CDI) was ignored. The specification requires bean
classes to be *serializable*, depending on the scope. I'm not certain this is needed in practice (maybe
it is), but Java serialization is seriously broken (as discussed in the book Effective Java by Joshua Bloch),
so I'm curious how to best approach this "requirement".

Sometimes *byte code manipulation* is needed by CDI to "decorate" classes with additional behaviour.
For example, an application-scoped managed bean with request-scoped dependencies must create fresh instances
of those dependencies for each request. For that, CDI needs to extend and enhance the bean class through
byte code manipulation. This means that the managed bean class must be extensible. So it cannot be final,
it cannot have final public instance methods, etc. The bean class must also have a non-private no-parameter
constructor. Bean classes with pseudo-scope `Dependent` do not have these restrictions, though.

Finally, it has been said multiple times before, but:
* Do use *CDI's dependency injection* (and contexts as well, of course)
* Use *Java interfaces* as API contracts at layer boundaries, also for the managed bean's bean types (specified by a `Typed` annotation)
  * Also use Java interfaces as producer method return types
* Use *constructor-based injection* creating fully initialized classes, enabling manual object construction in test code
  * Producer methods may be used as an alternative (cf. constructors versus factory methods)
  * Producer methods may also be accompanied by disposer methods for proper cleanup (which have not been discussed here)
* Turn JNDI resources into producer fields
* Consider the use of MicroProfile Config for configuration data
