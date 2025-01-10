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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JDBC "template" given a Connection. It makes the use of JDBC a bit easier. Somewhat inspired by Spring, but also
 * by the JDK functional interfaces.
 * <p>
 * Typically, instances are very short-lived and not injected by dependency injection.
 *
 * @author Chris de Vreeze
 */
public class JdbcTemplateGivenConnection implements JdbcOperationsGivenConnection {

    private final Connection currentConnection;

    public JdbcTemplateGivenConnection(Connection currentConnection) {
        this.currentConnection = currentConnection;
    }

    @Override
    public Connection getConnection() {
        return currentConnection;
    }

    @Override
    public <R> R execute(
            Function<Connection, PreparedStatement> preparedStatementCreator,
            Function<PreparedStatement, R> statementFunction
    ) {
        try (PreparedStatement ps = preparedStatementCreator.apply(currentConnection)) {
            return statementFunction.apply(ps);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public <R> R query(
            String sql,
            Consumer<PreparedStatement> preparedStatementSetter,
            Function<ResultSet, R> resultSetExtractor
    ) {
        Function<Connection, PreparedStatement> preparedStatementCreator = con -> {
            try {
                PreparedStatement ps = con.prepareStatement(sql);
                preparedStatementSetter.accept(ps);
                return ps;
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
        Function<PreparedStatement, R> statementFunction = ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return resultSetExtractor.apply(rs);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
        return execute(preparedStatementCreator, statementFunction);
    }
}
