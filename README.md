# Try-openliberty

This project contains several Maven projects targetting the Open Liberty application server.
It is about learning and creating some "reference projects" that may be helpful as starting point for real projects.

## Reference material

This project uses some reference material itself, such as:
* [Open Liberty Guides](https://openliberty.io/guides/)
* [Open Liberty Documentation](https://openliberty.io/docs/latest/overview.html)
* [Open Liberty Feature Overview](https://openliberty.io/docs/latest/reference/feature/feature-overview.html)
* [Jakarta EE Documentation](https://jakarta.ee/learn/#documentation)
* [Jakarta EE Specifications](https://jakarta.ee/specifications/)
* [Jakarta EE 11 API Documentation](https://jakarta.ee/specifications/platform/11/apidocs/)
* [Jakarta EE XML Schemas](https://jakarta.ee/xml/ns/jakartaee/)

This project is also influenced by the material in the book Effective Java, 3rd Edition, by Joshua Bloch,
as well as by functional programming practices. As an example of the latter, the data models are largely represented
as (deeply) immutable Java records, using Guava immutable collections rather than plain standard Java collections.
The project is also influenced by the common best practice of using application layers in combination with Java interfaces
and concrete implementation classes (e.g. for the "service" layer).

In these respects, this project deviates from the sample code on the internet, and from a mind set of "using annotations for
everything".

## Project structure

The Maven projects in this repository have a relatively straightforward project structure:
1. They are to a large extent "regular" Maven Java projects
2. In particular, they are Jakarta EE Maven projects producing a WAR file
3. As Jakarta EE Maven projects targetting an Open Liberty application server, they contain a `server.xml` file (set)

For more information on the specifics, see the reference material mentioned above.

As a Jakarta EE Maven project producing a WAR file, the project:
* has a Maven POM file where the `packaging` is `war`
* has dependencies such as `jakarta.platform:jakarta.jakartaee-api` (scope `provided`) and `org.eclipse.microprofile:microprofile` (scope `provided`, type `pom`) in the POM file, among other dependencies
* has a POM file for which the POM files in the above-mentioned Open Libery guides can be used as starting point
* has ("Maven standard") source directories such as `src/main/java` and `src/test/java`
* has a `src/main/webapp` directory, and if needed a `src/main/webapp/WEB-INF/web.xml` file

As a project targetting the Open Liberty application server, the project:
* depends on the `io.openliberty.tools:liberty-maven-plugin` in its POM file
* contains a `src/main/liberty/config/server.xml` file (or a file set making up the server configuration)

The first thing the `server.xml` file must contain is a complete set of *features*. The feature set can be fine-grained
(e.g. `restfulWS-3.1`, `jsonb-3.0` etc.) or coarse-grained (e.g. `jakartaee-10.0`). The latter is easy from a configuration
point of view, but probably transitively pulls in a lot of fine-grained features that are not used.

In a way this *feature set* is the starting point for reasoning about the project. Obviously, the dependencies in the POM file
(in particular the ones with scope `provided`) must be complete w.r.t. the enabled features.

For the remainder of the `server.xml` configuration, it may be desirable to consult the above-mentioned Open Liberty guides.

Also note how much of the (overridable) "Liberty configuration" for port, host, context root etc. is done in the POM file.

