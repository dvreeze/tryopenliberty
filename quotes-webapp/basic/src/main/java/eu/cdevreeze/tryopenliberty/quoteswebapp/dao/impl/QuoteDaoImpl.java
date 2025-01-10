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

package eu.cdevreeze.tryopenliberty.quoteswebapp.dao.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import eu.cdevreeze.tryopenliberty.quoteswebapp.dao.QuoteDao;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcOperationsGivenConnection;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcTemplateGivenConnection;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.UncheckedSQLException;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Quotes DAO implementation.
 *
 * @author Chris de Vreeze
 */
@Typed({QuoteDao.class})
@ApplicationScoped
public class QuoteDaoImpl implements QuoteDao {

    @Override
    public Function<Connection, ImmutableList<Quote>> findAllQuotes() {
        return this::findAllQuotes;
    }

    @Override
    public Function<Connection, ImmutableList<Quote>> findQuotesByAuthor(String attributedTo) {
        return con -> findQuotesByAuthor(attributedTo, con);
    }

    @Override
    public Function<Connection, ImmutableList<Quote>> findQuotesBySubject(String subject) {
        return con -> findQuotesBySubject(subject, con);
    }

    private ImmutableList<Quote> findAllQuotes(Connection con) {
        Consumer<PreparedStatement> initPs = ps -> {
        };
        return findQuotes(FIND_ALL_QUOTES_SQL, initPs, con);
    }

    private ImmutableList<Quote> findQuotesByAuthor(String attributedTo, Connection con) {
        Consumer<PreparedStatement> initPs = ps -> {
            try {
                ps.setString(1, attributedTo);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
        return findQuotes(FIND_QUOTES_BY_AUTHOR_SQL, initPs, con);
    }

    private ImmutableList<Quote> findQuotesBySubject(String subject, Connection con) {
        Consumer<PreparedStatement> initPs = ps -> {
            try {
                ps.setString(1, subject);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
        return findQuotes(FIND_QUOTES_BY_SUBJECT_SQL, initPs, con);
    }

    private ImmutableList<Quote> findQuotes(String sql, Consumer<PreparedStatement> initPs, Connection con) {
        Function<ResultSet, ImmutableList<Quote>> rsExtractor = rs -> {
            try {
                final List<QuoteRow> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(
                            new QuoteRow(
                                    rs.getLong("quote_id"),
                                    rs.getString("quote_text"),
                                    rs.getString("attributed_to"),
                                    rs.getString("subject_text")
                            )
                    );
                }
                return extractQuotes(rows);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
        JdbcOperationsGivenConnection jdbcTemplateGivenConnection = new JdbcTemplateGivenConnection(con);
        return jdbcTemplateGivenConnection.query(sql, initPs, rsExtractor);
    }

    /**
     * Row for a quote and one of its subjects
     */
    private record QuoteRow(
            long quoteId,
            String quoteText,
            String attributedTo,
            String subject
    ) {
    }

    private ImmutableList<Quote> extractQuotes(List<QuoteRow> quoteRows) {
        return quoteRows
                .stream()
                .collect(Collectors.groupingBy(QuoteRow::quoteId))
                .values()
                .stream()
                .map(rowGroup ->
                        new Quote(
                                rowGroup.get(0).quoteText(),
                                rowGroup.get(0).attributedTo(),
                                rowGroup.stream().map(QuoteRow::subject).collect(ImmutableSet.toImmutableSet())
                        )
                )
                .collect(ImmutableList.toImmutableList());
    }

    private static final String FIND_ALL_QUOTES_SQL =
            """
                    SELECT q.id AS quote_id, s.id as subject_id, q.quote_text, q.attributed_to, s.subject_text
                      FROM quote_schema.quote AS q
                      LEFT OUTER JOIN quote_schema.quote_subject AS qs
                        ON q.id = qs.quote_id
                      LEFT OUTER JOIN quote_schema.subject AS s
                        ON qs.subject_id = s.id""";

    private static final String FIND_QUOTES_BY_AUTHOR_SQL =
            """
                    SELECT q.id AS quote_id, s.id as subject_id, q.quote_text, q.attributed_to, s.subject_text
                      FROM quote_schema.quote AS q
                      LEFT OUTER JOIN quote_schema.quote_subject AS qs
                        ON q.id = qs.quote_id
                      LEFT OUTER JOIN quote_schema.subject AS s
                        ON qs.subject_id = s.id
                     WHERE q.attributed_to = ?""";

    private static final String FIND_QUOTES_BY_SUBJECT_SQL =
            """
                    SELECT q.id AS quote_id, s.id as subject_id, q.quote_text, q.attributed_to, s.subject_text
                      FROM quote_schema.quote AS q
                      LEFT OUTER JOIN quote_schema.quote_subject AS qs
                        ON q.id = qs.quote_id
                      LEFT OUTER JOIN quote_schema.subject AS s
                        ON qs.subject_id = s.id
                     WHERE s.subject_text = ?""";
}
