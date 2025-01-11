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

import java.sql.Connection;

/**
 * Transactional isolation level.
 *
 * @author Chris de Vreeze
 */
public enum IsolationLevel {

    TRANSACTION_NONE(Connection.TRANSACTION_NONE),
    TRANSACTION_READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    TRANSACTION_READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    TRANSACTION_REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    TRANSACTION_SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int isolationLevelConstant;

    IsolationLevel(int isolationLevelConstant) {
        this.isolationLevelConstant = isolationLevelConstant;
    }

    public int getIsolationLevelConstant() {
        return isolationLevelConstant;
    }

    /**
     * Returns the isolation level corresponding to the given integer constant of interface Connection.
     */
    public static IsolationLevel fromIsolationLevelConstant(int intIsolationLevel) {
        return switch (intIsolationLevel) {
            case Connection.TRANSACTION_NONE -> TRANSACTION_NONE;
            case Connection.TRANSACTION_READ_UNCOMMITTED -> TRANSACTION_READ_UNCOMMITTED;
            case Connection.TRANSACTION_READ_COMMITTED -> TRANSACTION_READ_COMMITTED;
            case Connection.TRANSACTION_REPEATABLE_READ -> TRANSACTION_REPEATABLE_READ;
            case Connection.TRANSACTION_SERIALIZABLE -> TRANSACTION_SERIALIZABLE;
            default -> throw new RuntimeException("Unknown isolation level constant: " + intIsolationLevel);
        };
    }
}
