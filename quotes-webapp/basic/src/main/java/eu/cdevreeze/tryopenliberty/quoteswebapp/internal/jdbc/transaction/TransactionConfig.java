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

/**
 * Transactional configuration.
 *
 * @author Chris de Vreeze
 */
public record TransactionConfig(IsolationLevel isolationLevel, boolean readOnly) {

    public TransactionConfig(IsolationLevel isolationLevel) {
        this(isolationLevel, false);
    }

    public TransactionConfig makeReadOnly() {
        return new TransactionConfig(isolationLevel, true);
    }

    public static TransactionConfig TRANSACTION_NONE =
            new TransactionConfig(IsolationLevel.TRANSACTION_NONE);
    public static TransactionConfig TRANSACTION_READ_UNCOMMITTED =
            new TransactionConfig(IsolationLevel.TRANSACTION_READ_UNCOMMITTED);
    public static TransactionConfig TRANSACTION_READ_COMMITTED =
            new TransactionConfig(IsolationLevel.TRANSACTION_READ_COMMITTED);
    public static TransactionConfig TRANSACTION_REPEATABLE_READ =
            new TransactionConfig(IsolationLevel.TRANSACTION_REPEATABLE_READ);
    public static TransactionConfig TRANSACTION_SERIALIZABLE =
            new TransactionConfig(IsolationLevel.TRANSACTION_SERIALIZABLE);
}
