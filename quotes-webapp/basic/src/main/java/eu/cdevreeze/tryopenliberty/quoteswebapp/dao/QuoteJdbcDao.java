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

package eu.cdevreeze.tryopenliberty.quoteswebapp.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;

import java.sql.Connection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Quotes DAO interface.
 * <p>
 * Unlike much example code for Open Liberty (or Jakarta EE) projects, the DAO layer is an abstract
 * Java interface. Also, the data passed and returned in the DAO methods is immutable, to the extent
 * feasible.
 * <p>
 * Note that this DAO interface is bound to the use of JDBC.
 *
 * @author Chris de Vreeze
 */
public interface QuoteJdbcDao {

    Function<Connection, ImmutableList<Quote>> findAllQuotes();

    Function<Connection, Optional<Quote>> findQuoteById(long quoteId);

    Function<Connection, ImmutableList<Quote>> findQuotesByAuthor(String attributedTo);

    Function<Connection, ImmutableList<Quote>> findQuotesBySubject(String subject);

    Function<Connection, Quote> insertQuote(
            String quoteText,
            String attributedTo,
            ImmutableSet<String> subjects
    );

    Consumer<Connection> deleteQuoteById(long quoteId);
}
