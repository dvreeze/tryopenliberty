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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import eu.cdevreeze.tryopenliberty.quoteswebapp.dao.QuoteJdbcDao;
import eu.cdevreeze.tryopenliberty.quoteswebapp.dao.SubjectJdbcDao;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcConnectionOperations;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcConnectionTemplate;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.SQLExceptionUtil.throwingUncheckedSQLException;

/**
 * Quotes DAO implementation.
 *
 * @author Chris de Vreeze
 */
@Typed({QuoteJdbcDao.class})
@ApplicationScoped
public class QuoteJdbcDaoImpl implements QuoteJdbcDao {

    private final SubjectJdbcDao subjectDao;

    @Inject
    public QuoteJdbcDaoImpl(SubjectJdbcDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    @Override
    public Function<Connection, ImmutableList<Quote>> findAllQuotes() {
        return this::findAllQuotes;
    }

    @Override
    public Function<Connection, Optional<Quote>> findQuoteById(long quoteId) {
        return con -> findQuoteById(quoteId, con);
    }

    @Override
    public Function<Connection, ImmutableList<Quote>> findQuotesByAuthor(String attributedTo) {
        return con -> findQuotesByAuthor(attributedTo, con);
    }

    @Override
    public Function<Connection, ImmutableList<Quote>> findQuotesBySubject(String subject) {
        return con -> findQuotesBySubject(subject, con);
    }

    @Override
    public Function<Connection, Quote> insertQuote(String quoteText, String attributedTo, ImmutableSet<String> subjects) {
        return con -> insertQuote(quoteText, attributedTo, subjects, con);
    }

    @Override
    public Consumer<Connection> deleteQuoteById(long quoteId) {
        return con -> deleteQuoteById(quoteId, con);
    }

    private ImmutableList<Quote> findAllQuotes(Connection con) {
        Consumer<PreparedStatement> initPs = ps -> {
        };
        return findQuotes(FIND_ALL_QUOTES_SQL, initPs, con);
    }

    private Optional<Quote> findQuoteById(long quoteId, Connection con) {
        Consumer<PreparedStatement> initPs =
                throwingUncheckedSQLException((PreparedStatement ps) -> ps.setLong(1, quoteId));
        return findQuotes(FIND_QUOTE_BY_ID_SQL, initPs, con).stream().findFirst();
    }

    private ImmutableList<Quote> findQuotesByAuthor(String attributedTo, Connection con) {
        Consumer<PreparedStatement> initPs =
                throwingUncheckedSQLException((PreparedStatement ps) -> ps.setString(1, attributedTo));
        return findQuotes(FIND_QUOTES_BY_AUTHOR_SQL, initPs, con);
    }

    private ImmutableList<Quote> findQuotesBySubject(String subject, Connection con) {
        Consumer<PreparedStatement> initPs =
                throwingUncheckedSQLException((PreparedStatement ps) -> ps.setString(1, subject));
        return findQuotes(FIND_QUOTES_BY_SUBJECT_SQL, initPs, con);
    }

    private ImmutableList<Quote> findQuotes(String sql, Consumer<PreparedStatement> initPs, Connection con) {
        Function<ResultSet, ImmutableList<Quote>> rsExtractor =
                throwingUncheckedSQLException((ResultSet rs) -> {
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
                });
        JdbcConnectionOperations jdbcConnectionTemplate = new JdbcConnectionTemplate(con);
        return jdbcConnectionTemplate.query(sql, initPs, rsExtractor);
    }

    private Quote insertQuote(String quoteText, String attributedTo, ImmutableSet<String> subjects, Connection con) {
        for (String subject : subjects) {
            subjectDao.insertSubjectIfAbsent(subject).accept(con);
        }

        JdbcConnectionOperations jdbcConnectionTemplate = new JdbcConnectionTemplate(con);

        Consumer<PreparedStatement> psSetter1 =
                throwingUncheckedSQLException((PreparedStatement ps) -> {
                    ps.setString(1, quoteText);
                    ps.setString(2, attributedTo);
                });
        ImmutableList<ImmutableMap<String, Object>> keys =
                jdbcConnectionTemplate.updateReturningKeys(INSERT_QUOTE_SQL, psSetter1);
        Preconditions.checkArgument(keys.size() == 1);
        Preconditions.checkArgument(!keys.get(0).isEmpty());
        Preconditions.checkArgument(keys.get(0).containsKey("id"));

        long quoteId = Long.parseLong(Objects.requireNonNull(keys.get(0).get("id")).toString());

        for (String subject : subjects) {
            Consumer<PreparedStatement> psSetter2 =
                    throwingUncheckedSQLException((PreparedStatement ps) -> {
                        ps.setString(1, quoteText);
                        ps.setString(2, subject);
                    });
            jdbcConnectionTemplate.update(INSERT_QUOTE_SUBJECT_SQL, psSetter2);
        }

        return new Quote(quoteId, quoteText, attributedTo, subjects);
    }

    private void deleteQuoteById(long quoteId, Connection con) {
        JdbcConnectionOperations jdbcConnectionTemplate = new JdbcConnectionTemplate(con);

        Consumer<PreparedStatement> psSetter1 =
                throwingUncheckedSQLException((PreparedStatement ps) -> ps.setLong(1, quoteId));
        jdbcConnectionTemplate.update(DELETE_QUOTE_SUBJECTS_SQL, psSetter1);

        Consumer<PreparedStatement> psSetter2 =
                throwingUncheckedSQLException((PreparedStatement ps) -> ps.setLong(1, quoteId));
        jdbcConnectionTemplate.update(DELETE_QUOTE_BY_ID_SQL, psSetter2);
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
                                rowGroup.get(0).quoteId(),
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

    private static final String FIND_QUOTE_BY_ID_SQL =
            """
                    SELECT q.id AS quote_id, s.id as subject_id, q.quote_text, q.attributed_to, s.subject_text
                      FROM quote_schema.quote AS q
                      LEFT OUTER JOIN quote_schema.quote_subject AS qs
                        ON q.id = qs.quote_id
                      LEFT OUTER JOIN quote_schema.subject AS s
                        ON qs.subject_id = s.id
                     WHERE q.id = ?""";

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

    private static final String INSERT_QUOTE_SQL =
            """
                    INSERT INTO quote_schema.quote (quote_text, attributed_to)
                    VALUES (?, ?)""";

    private static final String INSERT_QUOTE_SUBJECT_SQL =
            """
                    INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
                     SELECT q.id, s.id
                       FROM quote_schema.quote AS q, quote_schema.subject AS s
                      WHERE q.quote_text = ?
                        AND s.subject_text = ?""";

    private static final String DELETE_QUOTE_SUBJECTS_SQL =
            """
                    DELETE FROM quote_schema.quote_subject WHERE quote_id = ?""";

    private static final String DELETE_QUOTE_BY_ID_SQL =
            """
                    DELETE FROM quote_schema.quote WHERE id = ?""";
}
