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

package eu.cdevreeze.tryopenliberty.quoteswebapp.rest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.QuoteList;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Quotes endpoint integration test, requiring a running server.
 *
 * @author Chris de Vreeze
 */
public class QuotesResourceIntegrationTest {

    private static final Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testFindAllQuotes() {
        URI uri = createUri("quotes-app", "quotes");

        try (Client client = ClientBuilder.newClient();
             Response response = client.target(uri).request().get()) {

            assertEquals(
                    Response.Status.OK.getStatusCode(),
                    response.getStatus(),
                    "Incorrect response code from " + uri);

            assertEquals(
                    MediaType.APPLICATION_JSON_TYPE,
                    response.getMediaType(),
                    "Incorrect media type from " + uri);

            String jsonString = response.readEntity(String.class);
            QuoteList quoteList = QuoteList.fromJsonbProxy(jsonb.fromJson(jsonString, QuoteList.JsonbProxy.class));
            ImmutableList<Quote> quotes = quoteList.quotes();

            assertFalse(quotes.isEmpty());

            Quote anExpectedQuote = new Quote(
                    "If you can learn how to use your mind, anything is possible.",
                    "Wim Hof",
                    ImmutableSet.of("inner strength")
            );

            assertTrue(quotes.stream().anyMatch(quote -> quote.equals(anExpectedQuote)));
        }
    }

    @Test
    public void testFindQuotesByAuthor() {
        URI uri = createUri("quotes-app", "quotes/attributedTo/Wim Hof");

        try (Client client = ClientBuilder.newClient();
             Response response = client.target(uri).request().get()) {

            assertEquals(
                    Response.Status.OK.getStatusCode(),
                    response.getStatus(),
                    "Incorrect response code from " + uri);

            assertEquals(
                    MediaType.APPLICATION_JSON_TYPE,
                    response.getMediaType(),
                    "Incorrect media type from " + uri);

            String jsonString = response.readEntity(String.class);
            QuoteList quoteList = QuoteList.fromJsonbProxy(jsonb.fromJson(jsonString, QuoteList.JsonbProxy.class));
            ImmutableList<Quote> quotes = quoteList.quotes();

            assertFalse(quotes.isEmpty());

            assertEquals(
                    Set.of("Wim Hof"),
                    quotes.stream().map(Quote::attributedTo).collect(Collectors.toSet())
            );

            Quote anExpectedQuote = new Quote(
                    "If you can learn how to use your mind, anything is possible.",
                    "Wim Hof",
                    ImmutableSet.of("inner strength")
            );

            assertTrue(quotes.stream().anyMatch(quote -> quote.equals(anExpectedQuote)));
        }
    }

    @Test
    public void testFindQuotesBySubject() {
        URI uri = createUri("quotes-app", "quotes/subject/hidden knowledge");

        try (Client client = ClientBuilder.newClient();
             Response response = client.target(uri).request().get()) {

            assertEquals(
                    Response.Status.OK.getStatusCode(),
                    response.getStatus(),
                    "Incorrect response code from " + uri);

            assertEquals(
                    MediaType.APPLICATION_JSON_TYPE,
                    response.getMediaType(),
                    "Incorrect media type from " + uri);

            String jsonString = response.readEntity(String.class);
            QuoteList quoteList = QuoteList.fromJsonbProxy(jsonb.fromJson(jsonString, QuoteList.JsonbProxy.class));
            ImmutableList<Quote> quotes = quoteList.quotes();

            assertFalse(quotes.isEmpty());

            assertTrue(
                    quotes.stream().allMatch(q -> q.subjects().contains("hidden knowledge"))
            );

            Quote anExpectedQuote = new Quote(
                    "We now have the technology to bring ET home.",
                    "Ben Rich",
                    ImmutableSet.of("hidden knowledge")
            );

            assertTrue(quotes.stream().anyMatch(quote -> quote.equals(anExpectedQuote)));
        }
    }

    private URI createUri(String applicationPath, String resourcePath) {
        String host = "localhost";
        int port = Integer.parseInt(System.getProperty("http.port"));
        String context = "/" + System.getProperty("context.root");
        String path = String.format("%s/%s/%s", context, applicationPath, resourcePath);
        try {
            return new URI("http", null, host, port, path, null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
