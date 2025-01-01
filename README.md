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

