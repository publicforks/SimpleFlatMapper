package org.sfm.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.sfm.datastax.DataHelper;
import org.sfm.reflect.Getter;

import java.util.Date;

public class DatastaxDateGetter implements Getter<GettableByIndexData, Object> {

    private final int index;

    public DatastaxDateGetter(int index) {
        this.index = index;
    }

    @Override
    public Object get(GettableByIndexData target) throws Exception {
        return DataHelper.getDate(index, target);
    }
}
