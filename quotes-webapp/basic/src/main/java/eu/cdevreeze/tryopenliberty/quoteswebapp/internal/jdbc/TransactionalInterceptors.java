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

import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.function.ConnectionFunction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transactional interceptors, for JDBC local database transactions only.
 *
 * @author Chris de Vreeze
 */
public class TransactionalInterceptors {

    private TransactionalInterceptors() {
    }

    public static <R> ConnectionFunction<R> inTransaction(
            ConnectionFunction<R> connectionFunction,
            int isolationLevel
    ) {
        return inTransaction(connectionFunction, isolationLevel, false);
    }

    public static <R> ConnectionFunction<R> inReadOnlyTransaction(
            ConnectionFunction<R> connectionFunction,
            int isolationLevel
    ) {
        return inTransaction(connectionFunction, isolationLevel, true);
    }

    public static <R> ConnectionFunction<R> inReadCommittedTransaction(
            ConnectionFunction<R> connectionFunction
    ) {
        return inTransaction(connectionFunction, Connection.TRANSACTION_READ_COMMITTED);
    }

    public static <R> ConnectionFunction<R> inReadOnlyReadCommittedTransaction(
            ConnectionFunction<R> connectionFunction
    ) {
        return inReadOnlyTransaction(connectionFunction, Connection.TRANSACTION_READ_COMMITTED);
    }

    private static <R> ConnectionFunction<R> inTransaction(
            ConnectionFunction<R> connectionFunction,
            int isolationLevel,
            boolean readOnly
    ) {
        return con -> {
            try {
                // Setting readOnly does not work (consistently) if the connection is a
                // com.ibm.ws.rsadapter.jdbc.v43.WSJdbc43Connection.
                // So leaving this property alone for the moment
                con.setTransactionIsolation(isolationLevel);
                con.setAutoCommit(false);
                R result = connectionFunction.apply(con);
                con.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        };
    }
}
