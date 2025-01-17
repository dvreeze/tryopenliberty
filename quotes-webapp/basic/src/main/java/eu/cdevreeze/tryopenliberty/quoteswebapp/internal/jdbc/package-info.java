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

/**
 * Mini-library making the use of JDBC easier. This support mainly concerns JDBC-resource management,
 * much like the Spring JdbcTemplate does. This mini-library uses standard functional interfaces
 * taking/returning JDBC resources (such as Connection, PreparedStatement, ResultSet). It also offers
 * a utility wrapping checked SQLException instances in unchecked exceptions, in order to make the use
 * of those standard functional interfaces practical.
 * <p>
 * This library also makes (programmatic) transaction management easier. Local database transactions
 * require open connections. Therefore, the JDBC "templating" functionality partly depends on a
 * Connection as context (within an existing transactional context, if applicable), and partly depends
 * on a DataSource as context (obtaining Connection instances, setting transactional boundaries, and
 * calling a "Connection function" within those transactional boundaries).
 *
 * @author Chris de Vreeze
 */
package eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc;
