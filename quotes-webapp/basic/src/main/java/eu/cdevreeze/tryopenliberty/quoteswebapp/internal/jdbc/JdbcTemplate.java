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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JDBC "template", making the use of JDBC a bit easier. Somewhat inspired by Spring, but also by the JDK
 * functional interfaces.
 *
 * @author Chris de Vreeze
 */
public class JdbcTemplate implements JdbcOperations {

    private final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public <R> R execute(Function<Connection, R> connectionFunction) {
        try (Connection con = dataSource.getConnection()) {
            return connectionFunction.apply(con);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public void execute(Consumer<Connection> connectionConsumer) {
        try (Connection con = dataSource.getConnection()) {
            connectionConsumer.accept(con);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }
}
