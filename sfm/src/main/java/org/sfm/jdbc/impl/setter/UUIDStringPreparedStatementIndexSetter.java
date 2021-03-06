package org.sfm.jdbc.impl.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class UUIDStringPreparedStatementIndexSetter implements PreparedStatementIndexSetter<UUID> {
    @Override
    public void set(PreparedStatement ps, UUID value, int columnIndex) throws SQLException {
        if (value == null) {
            ps.setNull(columnIndex, Types.VARCHAR);
        } else {
            ps.setString(columnIndex, value.toString());
        }
    }
}
