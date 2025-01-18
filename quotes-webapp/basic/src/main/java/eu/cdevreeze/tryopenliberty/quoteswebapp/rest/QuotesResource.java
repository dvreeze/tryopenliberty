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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.QuoteData;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.QuoteList;
import eu.cdevreeze.tryopenliberty.quoteswebapp.service.QuoteService;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Quotes resource.
 * <p>
 * Note the use of constructor injection, and the use of a service interface rather than implementation.
 *
 * @author Chris de Vreeze
 */
@Path("quotes")
public class QuotesResource {

    private final QuoteService quoteService;

    @Inject
    public QuotesResource(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public QuoteList.JsonbProxy findAllQuotes() {
        checkQuoteServiceDependency();
        QuoteList quoteList = new QuoteList(quoteService.findAllQuotes());
        return quoteList.toJsonbProxy();
    }

    @GET
    @Path("/quoteId/{quoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public QuoteList.JsonbProxy findQuoteById(@PathParam("quoteId") long quoteId) {
        checkQuoteServiceDependency();
        QuoteList quoteList =
                new QuoteList(
                        quoteService.findQuoteById(quoteId).stream().collect(ImmutableList.toImmutableList())
                );
        return quoteList.toJsonbProxy();
    }

    @GET
    @Path("/attributedTo/{attributedTo}")
    @Produces(MediaType.APPLICATION_JSON)
    public QuoteList.JsonbProxy findQuotesByAuthor(@PathParam("attributedTo") String author) {
        checkQuoteServiceDependency();
        QuoteList quoteList = new QuoteList(quoteService.findQuotesByAuthor(author));
        return quoteList.toJsonbProxy();
    }

    @GET
    @Path("/subject/{subject}")
    @Produces(MediaType.APPLICATION_JSON)
    public QuoteList.JsonbProxy findQuotesBySubject(@PathParam("subject") String subject) {
        checkQuoteServiceDependency();
        QuoteList quoteList = new QuoteList(quoteService.findQuotesBySubject(subject));
        return quoteList.toJsonbProxy();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void insertQuote(QuoteData.JsonbProxy quote) {
        checkQuoteServiceDependency();
        QuoteData qt = QuoteData.fromJsonbProxy(quote);
        quoteService.insertQuote(qt.quoteText(), qt.attributedTo(), qt.subjects());
    }

    @DELETE
    @Path("/{quoteId}")
    public void deleteQuote(@PathParam("quoteId") long quoteId) {
        checkQuoteServiceDependency();
        quoteService.deleteQuoteById(quoteId);
    }

    private void checkQuoteServiceDependency() {
        // Checking my understanding of (parts of) CDI (without passivation)
        Instance<QuoteService> serviceInstance =
                CDI.current().select(QuoteService.class, Default.Literal.INSTANCE);
        Instance.Handle<QuoteService> serviceInstanceHandle = serviceInstance.getHandle();

        QuoteService serviceObject = serviceInstanceHandle.get();

        Preconditions.checkArgument(
                serviceObject.equals(
                        CDI.current().select(QuoteService.class, Default.Literal.INSTANCE).get()
                )
        );
        // The check I hope to be successful
        Preconditions.checkArgument(serviceObject.equals(this.quoteService));
    }
}
