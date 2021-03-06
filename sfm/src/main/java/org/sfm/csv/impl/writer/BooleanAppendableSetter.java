package org.sfm.csv.impl.writer;

import org.sfm.csv.CellWriter;
import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanAppendableSetter implements BooleanSetter<Appendable> {

    private final CellWriter cellWriter;

    public BooleanAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setBoolean(Appendable target, boolean value) throws Exception {
        cellWriter.writeValue(Boolean.toString(value), target);
    }
}
