package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.sfm.datastax.DataHelper;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;

public class DateSettableDataSetter implements Setter<SettableByIndexData, Object>{
    private final int index;

    public DateSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, Object value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            DataHelper.setDate(index, value, target);
        }
    }
}
