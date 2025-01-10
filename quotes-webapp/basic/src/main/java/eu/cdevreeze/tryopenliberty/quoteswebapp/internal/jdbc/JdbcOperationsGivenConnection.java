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
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JDBC "operations" given a Connection. Somewhat inspired by Spring.
 * <p>
 * Typically, instances are very short-lived and not injected by dependency injection.
 *
 * @author Chris de Vreeze
 */
public interface JdbcOperationsGivenConnection {

    Connection getConnection();

    <R> R execute(
            Function<Connection, PreparedStatement> preparedStatementCreator,
            Function<PreparedStatement, R> statementFunction
    );

    <R> R query(
            String sql,
            Consumer<PreparedStatement> preparedStatementSetter,
            Function<ResultSet, R> resultSetExtractor
    );
}
