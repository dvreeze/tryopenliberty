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
import eu.cdevreeze.tryopenliberty.quoteswebapp.cdi.annotation.QuoteDataSource;
import eu.cdevreeze.tryopenliberty.quoteswebapp.dao.QuoteDao;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcFunctionalInterfaces;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcOperations;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcTemplate;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Quotes DAO implementation.
 *
 * @author Chris de Vreeze
 */
@Typed({QuoteDao.class})
@ApplicationScoped
public class QuoteDaoImpl implements QuoteDao {

    private final JdbcOperations jdbcTemplate;

    @Inject
    public QuoteDaoImpl(@QuoteDataSource DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ImmutableList<Quote> findAllQuotes() {
        JdbcFunctionalInterfaces.PreparedStatementCreator makePs = con -> con.prepareStatement(FIND_ALL_QUOTES_SQL);
        return findQuotes(makePs);
    }

    @Override
    public ImmutableList<Quote> findQuotesByAuthor(String attributedTo) {
        JdbcFunctionalInterfaces.PreparedStatementCreator makePs = con -> {
            PreparedStatement ps = con.prepareStatement(FIND_QUOTES_BY_AUTHOR_SQL);
            ps.setString(1, attributedTo);
            return ps;
        };
        return findQuotes(makePs);
    }

    @Override
    public ImmutableList<Quote> findQuotesBySubject(String subject) {
        JdbcFunctionalInterfaces.PreparedStatementCreator makePs = con -> {
            PreparedStatement ps = con.prepareStatement(FIND_QUOTES_BY_SUBJECT_SQL);
            ps.setString(1, subject);
            return ps;
        };
        return findQuotes(makePs);
    }

    private ImmutableList<Quote> findQuotes(JdbcFunctionalInterfaces.PreparedStatementCreator makePs) {
        JdbcFunctionalInterfaces.PreparedStatementFunction<ImmutableList<Quote>> psFunction = ps -> {
            final List<QuoteRow> rows = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
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
            }
            return extractQuotes(rows);
        };
        try {
            return jdbcTemplate.execute(makePs, psFunction);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
