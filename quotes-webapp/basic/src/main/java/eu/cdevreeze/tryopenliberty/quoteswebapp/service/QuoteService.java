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

package eu.cdevreeze.tryopenliberty.quoteswebapp.service;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;

/**
 * Quotes service interface.
 * <p>
 * Unlike much example code for Open Liberty (or Jakarta EE) projects, the service layer is an abstract
 * Java interface. Also, the data passed and returned in the service methods is immutable, including
 * the collections, which are immutable Guava collections.
 *
 * @author Chris de Vreeze
 */
public interface QuoteService {

    ImmutableList<Quote> findAllQuotes();

    ImmutableList<Quote> findQuotesByAuthor(String attributedTo);

    ImmutableList<Quote> findQuotesBySubject(String subject);
}
