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

package eu.cdevreeze.tryopenliberty.quoteswebapp.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import eu.cdevreeze.tryopenliberty.quoteswebapp.cdi.annotation.QuoteDataSource;
import eu.cdevreeze.tryopenliberty.quoteswebapp.dao.QuoteDao;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcOperations;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcTemplate;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.transaction.TransactionConfig;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;
import eu.cdevreeze.tryopenliberty.quoteswebapp.service.QuoteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Optional;
import java.util.function.Function;

import static eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.transaction.TransactionalInterceptors.inTransaction;

/**
 * Quotes service implementation, adding transaction management on top of the DAO methods.
 *
 * @author Chris de Vreeze
 */
@Typed({QuoteService.class})
@ApplicationScoped
public class QuoteServiceImpl implements QuoteService {

    private final QuoteDao quoteDao;
    private final DataSource dataSource;

    @Inject
    public QuoteServiceImpl(QuoteDao quoteDao, @QuoteDataSource DataSource dataSource) {
        this.quoteDao = quoteDao;
        this.dataSource = dataSource;
    }

    @Override
    public ImmutableList<Quote> findAllQuotes() {
        JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.execute(
                inTransaction(
                        TransactionConfig.TRANSACTION_READ_COMMITTED.makeReadOnly(),
                        quoteDao.findAllQuotes()
                )
        );
    }

    @Override
    public Optional<Quote> findQuoteById(long quoteId) {
        JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.execute(
                inTransaction(
                        TransactionConfig.TRANSACTION_READ_COMMITTED.makeReadOnly(),
                        quoteDao.findQuoteById(quoteId)
                )
        );
    }

    @Override
    public ImmutableList<Quote> findQuotesByAuthor(String attributedTo) {
        JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.execute(
                inTransaction(
                        TransactionConfig.TRANSACTION_READ_COMMITTED.makeReadOnly(),
                        quoteDao.findQuotesByAuthor(attributedTo)
                )
        );
    }

    @Override
    public ImmutableList<Quote> findQuotesBySubject(String subject) {
        JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.execute(
                inTransaction(
                        TransactionConfig.TRANSACTION_READ_COMMITTED.makeReadOnly(),
                        quoteDao.findQuotesBySubject(subject)
                )
        );
    }

    @Override
    public Quote insertQuote(String quoteText, String attributedTo, ImmutableSet<String> subjects) {
        JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.execute(
                inTransaction(
                        TransactionConfig.TRANSACTION_READ_COMMITTED,
                        quoteDao.insertQuote(quoteText, attributedTo, subjects)
                )
        );
    }

    @Override
    public void deleteQuoteById(long quoteId) {
        JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        Function<Connection, Object> action = con -> {
            quoteDao.deleteQuoteById(quoteId).accept(con);
            return null;
        };
        jdbcTemplate.execute(
                inTransaction(TransactionConfig.TRANSACTION_READ_COMMITTED, action)
        );
    }
}
