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
 * Predicate throwing a SQLException. Do not use this interface directly in user code,
 * but use it under the hood to wrap SQLException into UncheckedSQLException.
 *
 * @author Chris de Vreeze
 */
@FunctionalInterface
public interface PredicateThrowingSQLException<T> {

    boolean test(T t) throws SQLException;
}