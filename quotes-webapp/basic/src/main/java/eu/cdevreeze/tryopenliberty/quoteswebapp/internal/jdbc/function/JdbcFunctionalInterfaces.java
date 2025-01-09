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

package eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.function;

import java.sql.SQLException;

/**
 * "Namespace" for JDBC variants of known functional interfaces.
 * <p>
 * It offers equivalents for some standard functional interfaces, except that they can throw a SQLException.
 * <p>
 * Use these functional interfaces by qualifying them with the class name, to avoid confusion with the
 * standard Java functional interfaces having the same name. Or better still, use the more specific
 * functional interface subtypes such ConnectionFunction, PreparedStatementConsumer etc.
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
    public interface Function<T, R> {

        R apply(T t) throws SQLException;
    }

    /**
     * Consumer that can throw a SQLException.
     *
     * @author Chris de Vreeze
     */
    @FunctionalInterface
    public interface Consumer<T> {

        void accept(T t) throws SQLException;
    }

    /**
     * Supplier that can throw a SQLException.
     *
     * @author Chris de Vreeze
     */
    @FunctionalInterface
    public interface Supplier<T> {

        T get() throws SQLException;
    }

    /**
     * Predicate that can throw a SQLException.
     *
     * @author Chris de Vreeze
     */
    @FunctionalInterface
    public interface Predicate<T> {

        boolean test(T t) throws SQLException;

        // TODO Other methods (analogous to standard Predicate type)
    }

    /**
     * UnaryOperator that can throw a SQLException.
     *
     * @author Chris de Vreeze
     */
    @FunctionalInterface
    public interface UnaryOperator<T> extends Function<T, T> {
    }
}
