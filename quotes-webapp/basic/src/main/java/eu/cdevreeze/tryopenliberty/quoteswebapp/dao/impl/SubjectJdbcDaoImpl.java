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

import com.google.common.collect.ImmutableSet;
import eu.cdevreeze.tryopenliberty.quoteswebapp.dao.SubjectJdbcDao;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcConnectionOperations;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.JdbcConnectionTemplate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.SQLExceptionUtil.throwingUncheckedSQLException;

/**
 * Subject DAO implementation.
 *
 * @author Chris de Vreeze
 */
@Typed({SubjectJdbcDao.class})
@ApplicationScoped
public final class SubjectJdbcDaoImpl implements SubjectJdbcDao {

    // Final works; the proxy uses the interface and composition rather than concrete inheritance from this class

    @Override
    public Function<Connection, ImmutableSet<String>> findAllSubjects() {
        return this::findAllSubjects;
    }

    @Override
    public Consumer<Connection> insertSubjectIfAbsent(String subject) {
        return con -> insertSubjectIfAbsent(subject, con);
    }

    @Override
    public Consumer<Connection> deleteSubjectById(long subjectId) {
        return con -> deleteSubjectById(subjectId, con);
    }

    private ImmutableSet<String> findAllSubjects(Connection con) {
        Consumer<PreparedStatement> initPs = ps -> {
        };
        Function<ResultSet, ImmutableSet<String>> rsExtractor =
                throwingUncheckedSQLException((ResultSet rs) -> {
                    final List<String> rows = new ArrayList<>();
                    while (rs.next()) {
                        rows.add(rs.getString("subject_text"));
                    }
                    return ImmutableSet.copyOf(rows);
                });
        JdbcConnectionOperations jdbcConnectionTemplate = new JdbcConnectionTemplate(con);
        return jdbcConnectionTemplate.query(FIND_ALL_SUBJECTS_SQL, initPs, rsExtractor);
    }

    private void insertSubjectIfAbsent(String subject, Connection con) {
        Consumer<PreparedStatement> preparedStatementSetter =
                throwingUncheckedSQLException((PreparedStatement ps) -> ps.setString(1, subject));
        JdbcConnectionOperations jdbcConnectionTemplate = new JdbcConnectionTemplate(con);
        jdbcConnectionTemplate.update(INSERT_SUBJECT_SQL, preparedStatementSetter);
    }

    private void deleteSubjectById(long subjectId, Connection con) {
        Consumer<PreparedStatement> preparedStatementSetter =
                throwingUncheckedSQLException((PreparedStatement ps) -> ps.setLong(1, subjectId));
        JdbcConnectionOperations jdbcConnectionTemplate = new JdbcConnectionTemplate(con);
        jdbcConnectionTemplate.update(DELETE_SUBJECT_BY_ID_SQL, preparedStatementSetter);
    }

    private static final String FIND_ALL_SUBJECTS_SQL =
            "SELECT id, subject_text FROM quote_schema.subject";

    private static final String INSERT_SUBJECT_SQL =
            """
                    INSERT INTO quote_schema.subject (subject_text)
                    VALUES (?)
                    ON CONFLICT DO NOTHING""";

    private static final String DELETE_SUBJECT_BY_ID_SQL =
            "DELETE FROM quote_schema.subject WHERE id = ?";
}
