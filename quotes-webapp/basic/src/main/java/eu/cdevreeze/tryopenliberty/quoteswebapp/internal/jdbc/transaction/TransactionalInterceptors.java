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

package eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.transaction;

import eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.UncheckedSQLException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Transactional interceptors, for JDBC local database transactions only.
 *
 * @author Chris de Vreeze
 */
public class TransactionalInterceptors {

    private TransactionalInterceptors() {
    }

    public static <R> Function<Connection, R> transactional(
            TransactionConfig transactionConfig,
            Function<Connection, R> connectionFunction
    ) {
        return con -> {
            try {
                // Setting readOnly does not work (consistently) if the connection is a
                // com.ibm.ws.rsadapter.jdbc.v43.WSJdbc43Connection.
                // So leaving this property alone for the moment
                con.setTransactionIsolation(transactionConfig.isolationLevel().getIsolationLevelConstant());
                con.setAutoCommit(false);
                R result = connectionFunction.apply(con);
                con.commit();
                return result;
            } catch (SQLException e) {
                rollback(con);
                throw new UncheckedSQLException(e);
            } catch (RuntimeException e) {
                rollback(con);
                throw e;
            }
        };
    }

    private static void rollback(Connection con) {
        try {
            con.rollback();
        } catch (SQLException ex) {
            throw new UncheckedSQLException(ex);
        }
    }
}
