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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static eu.cdevreeze.tryopenliberty.quoteswebapp.internal.jdbc.SQLExceptionUtil.throwingUncheckedSQLException;

/**
 * JDBC "template" given a Connection. It makes the use of JDBC a bit easier. Somewhat inspired by Spring, but also
 * by the JDK functional interfaces.
 * <p>
 * Typically, instances are very short-lived and not injected by dependency injection.
 *
 * @author Chris de Vreeze
 */
public class JdbcTemplateGivenConnection implements JdbcOperationsGivenConnection {

    private final Connection currentConnection;

    public JdbcTemplateGivenConnection(Connection currentConnection) {
        this.currentConnection = currentConnection;
    }

    @Override
    public Connection getConnection() {
        return currentConnection;
    }

    @Override
    public <R> R execute(
            Function<Connection, PreparedStatement> preparedStatementCreator,
            Function<PreparedStatement, R> preparedStatementFunction
    ) {
        try (PreparedStatement ps = preparedStatementCreator.apply(currentConnection)) {
            return preparedStatementFunction.apply(ps);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public <R> R query(
            String sql,
            Consumer<PreparedStatement> preparedStatementSetter,
            Function<ResultSet, R> resultSetExtractor
    ) {
        Function<Connection, PreparedStatement> preparedStatementCreator =
                throwingUncheckedSQLException((Connection con) -> {
                    PreparedStatement ps = con.prepareStatement(sql);
                    preparedStatementSetter.accept(ps);
                    return ps;
                });
        Function<PreparedStatement, R> preparedStatementFunction =
                throwingUncheckedSQLException((PreparedStatement ps) -> {
                    try (ResultSet rs = ps.executeQuery()) {
                        return resultSetExtractor.apply(rs);
                    }
                });
        return execute(preparedStatementCreator, preparedStatementFunction);
    }

    @Override
    public int update(Function<Connection, PreparedStatement> preparedStatementCreator) {
        try (PreparedStatement ps = preparedStatementCreator.apply(currentConnection)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    @Override
    public int update(String sql, Consumer<PreparedStatement> preparedStatementSetter) {
        Function<Connection, PreparedStatement> preparedStatementCreator =
                throwingUncheckedSQLException((Connection con) -> {
                    PreparedStatement ps = con.prepareStatement(sql);
                    preparedStatementSetter.accept(ps);
                    return ps;
                });
        return update(preparedStatementCreator);
    }

    @Override
    public ImmutableList<ImmutableMap<String, Object>> updateReturningKeys(
            String sql,
            Consumer<PreparedStatement> preparedStatementSetter
    ) {
        Supplier<ImmutableList<ImmutableMap<String, Object>>> resultSupplier =
                throwingUncheckedSQLException(() -> {
                    try (PreparedStatement ps = currentConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        preparedStatementSetter.accept(ps);

                        ps.executeUpdate();

                        ResultSet rs = ps.getGeneratedKeys();

                        ResultSetMetaData rsMetaData = Objects.requireNonNull(rs.getMetaData());
                        int columnCount = rsMetaData.getColumnCount();

                        List<String> columnNames = IntStream.rangeClosed(1, columnCount)
                                .mapToObj(colIndex -> getColumnName(rsMetaData, colIndex))
                                .toList();

                        List<ImmutableMap<String, Object>> rows = new ArrayList<>();

                        while (rs.next()) {
                            Map<String, Object> row = IntStream.rangeClosed(1, columnCount)
                                    .mapToObj(colIndex -> Map.entry(columnNames.get(colIndex - 1), getObject(rs, colIndex)))
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                            rows.add(ImmutableMap.copyOf(row));
                        }

                        return ImmutableList.copyOf(rows);
                    }
                });
        return resultSupplier.get();
    }

    private String getColumnName(ResultSetMetaData rsMetaData, int index) {
        Supplier<String> resultSupplier = throwingUncheckedSQLException(() ->
                rsMetaData.getColumnName(index)
        );
        return resultSupplier.get();
    }

    private Object getObject(ResultSet rs, int columnIndex) {
        Supplier<Object> resultSupplier = throwingUncheckedSQLException(() ->
                rs.getObject(columnIndex)
        );
        return resultSupplier.get();
    }
}
