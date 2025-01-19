/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.tryopenliberty.quoteswebapp.rest.introspect;

import eu.cdevreeze.tryopenliberty.quoteswebapp.dao.QuoteJdbcDao;
import eu.cdevreeze.tryopenliberty.quoteswebapp.rest.introspect.cdi.ApplicationBasePackage;
import eu.cdevreeze.tryopenliberty.quoteswebapp.service.QuoteService;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Internal introspection resource.
 * <p>
 * Recall that CDI performs type-safe resolution at injection points based on required type plus required qualifiers.
 *
 * @author Chris de Vreeze
 */
@Path("cdi-beans")
public class IntrospectionResource {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private final String applicationBasePackage;

    @Inject
    public IntrospectionResource(@ApplicationBasePackage String applicationBasePackage) {
        this.applicationBasePackage = applicationBasePackage;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray findAllCdiBeans() {
        BeanManager beanManager = CDI.current().getBeanManager();
        // This returns all CDI beans, of any type and any set of qualifiers
        // Recall that all CDI beans (implicitly) have qualifier Any
        // If we leave out this qualifier annotation argument, qualifier Default is assumed
        Set<Bean<?>> allCdiBeans = beanManager.getBeans(Object.class, Any.Literal.INSTANCE);
        return jsonProvider
                .createArrayBuilder(allCdiBeans.stream().map(this::convertToJson).toList())
                .build();
    }

    @GET
    @Path("application-beans")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray findAllCdiBeansInApplicationCode() {
        BeanManager beanManager = CDI.current().getBeanManager();
        // This returns all CDI beans in the given namespace, of any type and any set of qualifiers
        // Recall that all CDI beans (implicitly) have qualifier Any
        // If we leave out this qualifier annotation argument, qualifier Default is assumed
        Set<Bean<?>> cdiBeans = beanManager.getBeans(Object.class, Any.Literal.INSTANCE)
                .stream()
                .filter(bean ->
                        Optional.ofNullable(bean.getBeanClass().getPackage())
                                .stream()
                                .anyMatch(p -> p.getName().startsWith(applicationBasePackage))
                )
                .collect(Collectors.toSet());
        return jsonProvider
                .createArrayBuilder(cdiBeans.stream().map(this::convertToJson).toList())
                .build();
    }

    @GET
    @Path("quote-service-proxy-class")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getQuoteServiceProxyClass() {
        QuoteService quoteService = CDI.current().select(QuoteService.class, Default.Literal.INSTANCE).get();
        return convertToJson(quoteService);
    }

    @GET
    @Path("quote-dao-proxy-class")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getQuoteDaoProxyClass() {
        QuoteJdbcDao quoteDao = CDI.current().select(QuoteJdbcDao.class, Default.Literal.INSTANCE).get();
        return convertToJson(quoteDao);
    }

    private JsonObject convertToJson(Bean<?> bean) {
        return jsonProvider.createObjectBuilder()
                .add(
                        "name",
                        Optional.ofNullable(bean.getName())
                                .map(jsonProvider::createValue)
                                .map(v -> (JsonValue) v)
                                .orElse(JsonValue.NULL))
                .add("beanClass", bean.getBeanClass().toString())
                .add(
                        "types",
                        jsonProvider.createArrayBuilder(
                                bean.getTypes().stream()
                                        .map(tp -> jsonProvider.createValue(tp.toString()))
                                        .toList()
                        )
                )
                .add(
                        "qualifiers",
                        jsonProvider.createArrayBuilder(
                                bean.getQualifiers().stream()
                                        .map(qual -> jsonProvider.createValue(qual.toString()))
                                        .toList()
                        )
                )
                .add("scope", bean.getScope().toString()) // should never be null
                .add(
                        "stereotypes",
                        jsonProvider.createArrayBuilder(
                                bean.getStereotypes().stream()
                                        .map(ster -> jsonProvider.createValue(ster.toString()))
                                        .toList()
                        )
                )
                .add(
                        "injectionPoints",
                        jsonProvider.createArrayBuilder(
                                bean.getInjectionPoints().stream()
                                        .map(ip -> jsonProvider.createValue(ip.toString()))
                                        .toList()
                        )
                )
                .build();
    }

    private JsonObject convertToJson(QuoteService quoteService) {
        return jsonProvider.createObjectBuilder()
                .add("object", quoteService.toString())
                .add("class", convertToJson(quoteService.getClass()))
                .build();
    }

    private JsonObject convertToJson(QuoteJdbcDao quoteDao) {
        return jsonProvider.createObjectBuilder()
                .add("object", quoteDao.toString())
                .add("class", convertToJson(quoteDao.getClass()))
                .build();
    }

    private JsonObject convertToJson(Class<?> clazz) {
        return jsonProvider.createObjectBuilder()
                .add("class", clazz.getName())
                .add("superClass", Optional.ofNullable(clazz.getGenericSuperclass()).map(Type::getTypeName).orElse(""))
                .add(
                        "interfaces",
                        jsonProvider.createArrayBuilder(
                                Arrays.stream(clazz.getGenericInterfaces()).map(Type::getTypeName).toList()
                        )
                )
                .add(
                        "declaredFields",
                        jsonProvider.createArrayBuilder(
                                Arrays.stream(clazz.getDeclaredFields()).map(Field::toString).toList()
                        )
                )
                .add(
                        "declaredConstructors",
                        jsonProvider.createArrayBuilder(
                                Arrays.stream(clazz.getDeclaredConstructors()).map(Constructor::toString).toList()
                        )
                )
                .add(
                        "declaredMethods",
                        jsonProvider.createArrayBuilder(
                                Arrays.stream(clazz.getDeclaredMethods()).map(Method::toString).toList()
                        )
                )
                .add(
                        "declaredAnnotations",
                        jsonProvider.createArrayBuilder(
                                Arrays.stream(clazz.getDeclaredAnnotations()).map(Annotation::toString).toList()
                        )
                )
                .build();
    }
}
