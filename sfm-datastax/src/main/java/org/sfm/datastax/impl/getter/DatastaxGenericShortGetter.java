package org.sfm.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.sfm.datastax.DataHelper;
import org.sfm.datastax.DataTypeHelper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ShortGetter;

public class DatastaxGenericShortGetter implements ShortGetter<GettableByIndexData>, Getter<GettableByIndexData, Short> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericShortGetter(int index, DataType dataType) {
        this.index = index;
        this.dataTypeName = validateName(dataType);
    }

    private DataType.Name validateName(DataType dataType) {

        final DataType.Name name = dataType.getName();

        if (DataTypeHelper.isNumber(name)) {
            return name;
        }

        throw new IllegalArgumentException("Datatype " + dataType + " not a number");
    }

    @Override
    public Short get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
          return getShort(target);
    }

    @Override
    public short getShort(GettableByIndexData target) throws Exception {
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return (short)target.getLong(index);
            case VARINT:
                return target.getVarint(index).shortValue();
            case INT:
                return (short)target.getInt(index);
            case DECIMAL:
                return target.getDecimal(index).shortValue();
            case FLOAT:
                return (short)target.getFloat(index);
            case DOUBLE:
                return (short)target.getDouble(index);
        }

        if (DataTypeHelper.isSmallInt(dataTypeName)) return DataHelper.getShort(index, target);
        if (DataTypeHelper.isTinyInt(dataTypeName)) return (short)DataHelper.getByte(index, target);
        if (DataTypeHelper.isTime(dataTypeName)) return (short)DataHelper.getTime(index, target);

        return 0;
    }
}
