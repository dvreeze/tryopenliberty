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

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Internal introspection resource.
 *
 * @author Chris de Vreeze
 */
@Path("cdi-beans")
public class IntrospectionResource {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private final String applicationBasePackage;

    @Inject
    public IntrospectionResource(@Named("applicationBasePackage") String applicationBasePackage) {
        this.applicationBasePackage = applicationBasePackage;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray findAllCdiBeans() {
        BeanManager beanManager = CDI.current().getBeanManager();
        Set<Bean<?>> allCdiBeans = beanManager.getBeans(Object.class);
        return jsonProvider
                .createArrayBuilder(allCdiBeans.stream().map(this::convertToJson).toList())
                .build();
    }

    @GET
    @Path("application-beans")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray findAllCdiBeansInApplicationCode() {
        BeanManager beanManager = CDI.current().getBeanManager();
        Set<Bean<?>> cdiBeans = beanManager.getBeans(Object.class)
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
                .add(
                        "scope",
                        Optional.ofNullable(bean.getScope())
                                .map(Class::toString)
                                .map(jsonProvider::createValue)
                                .map(v -> (JsonValue) v)
                                .orElse(JsonValue.NULL))
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
}
