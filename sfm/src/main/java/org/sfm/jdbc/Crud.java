package org.sfm.jdbc;

import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Crud<T, K> {

    private final QueryPreparer<T> insertQueryPreparer;
    private final QueryPreparer<T> updateQueryPreparer;
    private final QueryPreparer<K> selectQueryPreparer;
    private final QueryPreparer<K> deleteQueryPreparer;
    private final JdbcMapper<T> selectQueryMapper;
    private final JdbcMapper<K> keyMapper;

    public Crud(QueryPreparer<T> insertQueryPreparer,
                QueryPreparer<T> updateQueryPreparer,
                QueryPreparer<K> selectQueryPreparer,
                JdbcMapper<T> selectQueryMapper,
                QueryPreparer<K> deleteQueryPreparer, JdbcMapper<K> keyMapper) {
        this.insertQueryPreparer = insertQueryPreparer;
        this.updateQueryPreparer = updateQueryPreparer;
        this.selectQueryPreparer = selectQueryPreparer;
        this.deleteQueryPreparer = deleteQueryPreparer;
        this.selectQueryMapper = selectQueryMapper;
        this.keyMapper = keyMapper;
    }

    public void create(Connection connection, T value) throws SQLException {
        create(connection, value, null);
    }

    public <RH extends RowHandler<? super K>> RH create(Connection connection, T value, RH keyConsumer) throws SQLException {
        PreparedStatement preparedStatement = insertQueryPreparer.prepare(connection).bind(value);
        try {
            preparedStatement.executeUpdate();
            if (keyConsumer != null) {
                handeGeneratedKeys(keyConsumer, preparedStatement);
            }
            return  keyConsumer;
        } finally {
            try { preparedStatement.close(); }
            catch (SQLException e) {
                // IGNORE
            }
        }
    }

    private void handeGeneratedKeys(RowHandler<? super K> keyConsumer, PreparedStatement preparedStatement) throws SQLException {
        ResultSet keys = preparedStatement.getGeneratedKeys();
        try {
            if (keys.next()) {
                try {
                    keyConsumer.handle(keyMapper.map(keys));
                } catch (Exception e) {
                    ErrorHelper.rethrow(e);
                }
            }
        } finally {
            keys.close();
        }
    }


    public T read(Connection connection, K key) throws SQLException {
        PreparedStatement preparedStatement = selectQueryPreparer.prepare(connection).bind(key);
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return selectQueryMapper.map(resultSet);
            }
            return null;
        } finally {
            try { preparedStatement.close(); }
            catch (SQLException e) {
                // IGNORE
            }
        }
    }

    public void update(Connection connection, T value) throws SQLException {
        PreparedStatement preparedStatement = updateQueryPreparer.prepare(connection).bind(value);
        try {
            preparedStatement.executeUpdate();
        } finally {
            try { preparedStatement.close(); }
            catch (SQLException e) {
                // IGNORE
            }
        }
    }

    public void delete(Connection connection, K key) throws SQLException {
        PreparedStatement preparedStatement = deleteQueryPreparer.prepare(connection).bind(key);
        try {
            preparedStatement.executeUpdate();
        } finally {
            try { preparedStatement.close(); }
            catch (SQLException e) {
                // IGNORE
            }
        }
    }
}
