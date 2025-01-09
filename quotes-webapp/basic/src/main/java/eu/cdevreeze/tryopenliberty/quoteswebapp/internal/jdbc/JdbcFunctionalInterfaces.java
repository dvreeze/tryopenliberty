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

/**
 * Equivalents for some standard functional interfaces, except that they can throw a SQLException.
 * Also, specific functional sub-interfaces of those functional interfaces.
 *
 * @author Chris de Vreeze
 */
public class JdbcFunctionalInterfaces {

    private JdbcFunctionalInterfaces() {
    }

    /**
     * Function that can throw a SQLException.
     *
     * @author Chris de Vreeze
     */
    @FunctionalInterface
    public interface FunctionInJdbc<T, R> {

        R apply(T t) throws SQLException;
    }

    /**
     * Consumer that can throw a SQLException.
     *
     * @author Chris de Vreeze
     */
    @FunctionalInterface
    public interface ConsumerInJdbc<T> {

        void accept(T t) throws SQLException;
    }

    // Specific JDBC function and consumer functional interfaces

    @FunctionalInterface
    public interface ConnectionFunction<R> extends FunctionInJdbc<Connection, R> {
    }

    @FunctionalInterface
    public interface PreparedStatementFunction<R> extends FunctionInJdbc<PreparedStatement, R> {
    }

    @FunctionalInterface
    public interface ResultSetFunction<R> extends FunctionInJdbc<ResultSet, R> {
    }

    @FunctionalInterface
    public interface ConnectionConsumer extends ConsumerInJdbc<Connection> {
    }

    @FunctionalInterface
    public interface PreparedStatementConsumer extends ConsumerInJdbc<PreparedStatement> {
    }

    @FunctionalInterface
    public interface PreparedStatementCreator extends ConnectionFunction<PreparedStatement> {
    }
}
