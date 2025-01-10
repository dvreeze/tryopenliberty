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

package eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc;

import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.function.PreparedStatementConsumer;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.function.PreparedStatementCreator;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.function.PreparedStatementFunction;
import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.function.ResultSetFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBC "template" given a Connection. It makes the use of JDBC a bit easier. Somewhat inspired by Spring, but also
 * by the JDK functional interfaces.
 *
 * @author Chris de Vreeze
 */
public class JdbcTemplateGivenConnection implements JdbcOperationsGivenConnection {

    private final Connection currentConnection;

    public JdbcTemplateGivenConnection(Connection currentConnection) {
        this.currentConnection = currentConnection;
    }

    @Override
    public <R> R execute(PreparedStatementCreator preparedStatementCreator, PreparedStatementFunction<R> statementFunction) throws SQLException {
        try (PreparedStatement ps = preparedStatementCreator.apply(currentConnection)) {
            return statementFunction.apply(ps);
        }
    }

    @Override
    public <R> R query(String sql, PreparedStatementConsumer preparedStatementSetter, ResultSetFunction<R> resultSetExtractor) throws SQLException {
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            preparedStatementSetter.accept(ps);
            return ps;
        };
        PreparedStatementFunction<R> statementFunction = ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return resultSetExtractor.apply(rs);
            }
        };
        return execute(preparedStatementCreator, statementFunction);
    }
}
