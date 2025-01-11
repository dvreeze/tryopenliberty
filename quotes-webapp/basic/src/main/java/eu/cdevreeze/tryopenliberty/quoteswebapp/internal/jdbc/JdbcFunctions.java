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

import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.function.*;

import java.sql.SQLException;
import java.util.function.*;

/**
 * Functions turning SQLException into UncheckedSQLException.
 *
 * @author Chris de Vreeze
 */
public class JdbcFunctions {

    private JdbcFunctions() {
    }

    public static <T, R> Function<T, R> throwingUncheckedSQLException(FunctionThrowingSQLException<T, R> f) {
        return (T t) -> {
            try {
                return f.apply(t);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }

    public static <T> Consumer<T> throwingUncheckedSQLException(ConsumerThrowingSQLException<T> f) {
        return (T t) -> {
            try {
                f.accept(t);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }

    public static <T> Supplier<T> throwingUncheckedSQLException(SupplierThrowingSQLException<T> f) {
        return () -> {
            try {
                return f.get();
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }

    public static <T> Predicate<T> throwingUncheckedSQLException(PredicateThrowingSQLException<T> f) {
        return (T t) -> {
            try {
                return f.test(t);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }

    public static <T> UnaryOperator<T> throwingUncheckedSQLException(UnaryOperatorThrowingSQLException<T> f) {
        return (T t) -> {
            try {
                return f.apply(t);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        };
    }
}
