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
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.QuoteData;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.QuoteList;
import eu.cdevreeze.tryopenliberty.quoteswebapp.service.impl.DummyQuoteServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Quotes endpoint unit test. We exploit the following facts in this unit test: interface-based wiring,
 * and implementation classes that can also be manually "wired" in unit tests. The latter is enabled
 * by constructor-based injection, which can also be used without CDI to construct an object.
 * <p>
 * Also note that we do not need any mocking here, unless we consider the dummy QuoteService implementation
 * to be a mock. Anyway, we do not need to (repeatedly) program any mocking behaviour.
 *
 * @author Chris de Vreeze
 */
public class QuotesResourceUnitTest {

    @Test
    public void testFindAllQuotes() {
        var quotesResource = new QuotesResource(new DummyQuoteServiceImpl());

        QuoteList.JsonbProxy quotesJson = quotesResource.findAllQuotes();

        QuoteList quoteList = QuoteList.fromJsonbProxy(quotesJson);
        ImmutableList<Quote> quotes = quoteList.quotes();

        assertFalse(quotes.isEmpty());

        Quote anExpectedQuote = new Quote(
                1L,
                "If you can learn how to use your mind, anything is possible.",
                "Wim Hof",
                ImmutableSet.of("inner strength")
        );

        assertTrue(quotes.stream().anyMatch(quote -> quote.equals(anExpectedQuote)));
    }

    @Test
    public void testFindQuoteById() {
        var quotesResource = new QuotesResource(new DummyQuoteServiceImpl());

        QuoteList.JsonbProxy quotesJson = quotesResource.findQuoteById(19L);

        QuoteList quoteList = QuoteList.fromJsonbProxy(quotesJson);
        ImmutableList<Quote> quotes = quoteList.quotes();

        assertFalse(quotes.isEmpty());

        assertEquals(
                Set.of("Isaac Newton"),
                quotes.stream().map(Quote::attributedTo).collect(Collectors.toSet())
        );

        Quote anExpectedQuote = new Quote(
                19L,
                "Genius is patience",
                "Isaac Newton",
                ImmutableSet.of("genius")
        );

        assertTrue(quotes.stream().anyMatch(quote -> quote.equals(anExpectedQuote)));
    }

    @Test
    public void testFindQuotesByAuthor() {
        var quotesResource = new QuotesResource(new DummyQuoteServiceImpl());

        QuoteList.JsonbProxy quotesJson = quotesResource.findQuotesByAuthor("Wim Hof");

        QuoteList quoteList = QuoteList.fromJsonbProxy(quotesJson);
        ImmutableList<Quote> quotes = quoteList.quotes();

        assertFalse(quotes.isEmpty());

        assertEquals(
                Set.of("Wim Hof"),
                quotes.stream().map(Quote::attributedTo).collect(Collectors.toSet())
        );

        Quote anExpectedQuote = new Quote(
                1L,
                "If you can learn how to use your mind, anything is possible.",
                "Wim Hof",
                ImmutableSet.of("inner strength")
        );

        assertTrue(quotes.stream().anyMatch(quote -> quote.equals(anExpectedQuote)));
    }

    @Test
    public void testFindQuotesBySubject() {
        var quotesResource = new QuotesResource(new DummyQuoteServiceImpl());

        QuoteList.JsonbProxy quotesJson = quotesResource.findQuotesBySubject("hidden knowledge");

        QuoteList quoteList = QuoteList.fromJsonbProxy(quotesJson);
        ImmutableList<Quote> quotes = quoteList.quotes();

        assertFalse(quotes.isEmpty());

        assertTrue(
                quotes.stream().allMatch(q -> q.subjects().contains("hidden knowledge"))
        );

        Quote anExpectedQuote = new Quote(
                28L,
                "We now have the technology to bring ET home.",
                "Ben Rich",
                ImmutableSet.of("hidden knowledge")
        );

        assertTrue(quotes.stream().anyMatch(quote -> quote.equals(anExpectedQuote)));
    }

    @Test
    public void testInsertQuote() {
        var quoteService = new DummyQuoteServiceImpl();
        var quotesResource = new QuotesResource(quoteService);

        int numberOfQuotes = quoteService.findAllQuotes().size();

        QuoteData quoteData = new QuoteData(
                "Interfaces are great",
                "Chris",
                ImmutableSet.of("Java truth")
        );

        quotesResource.insertQuote(quoteData.toJsonbProxy());

        assertEquals(1 + numberOfQuotes, quoteService.findAllQuotes().size());

        assertTrue(quoteService.findAllQuotes().stream().anyMatch(quote -> quote.toQuoteData().equals(quoteData)));
    }

    @Test
    public void testDeleteQuote() {
        var quoteService = new DummyQuoteServiceImpl();
        var quotesResource = new QuotesResource(quoteService);

        long quoteId = 19;

        int numberOfQuotes = quoteService.findAllQuotes().size();

        Quote anExpectedQuote = new Quote(
                quoteId,
                "Genius is patience",
                "Isaac Newton",
                ImmutableSet.of("genius")
        );

        assertTrue(quoteService.findAllQuotes().stream().anyMatch(quote -> quote.equals(anExpectedQuote)));

        quotesResource.deleteQuote(quoteId);

        assertEquals(numberOfQuotes - 1, quoteService.findAllQuotes().size());

        assertFalse(quoteService.findAllQuotes().stream().anyMatch(quote -> quote.equals(anExpectedQuote)));
    }
}
