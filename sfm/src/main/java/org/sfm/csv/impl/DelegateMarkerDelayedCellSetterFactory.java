package org.sfm.csv.impl;

import org.sfm.csv.CsvMapper;
import org.sfm.csv.mapper.BreakDetector;
import org.sfm.csv.mapper.CsvMapperCellConsumer;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.csv.mapper.DelayedCellSetterFactory;
import org.sfm.reflect.Setter;

public class DelegateMarkerDelayedCellSetterFactory<T, P> implements DelayedCellSetterFactory<T, P> {

	private final CsvMapper<P> mapper;
	private final Setter<T, P> setter;
    private final int index;
	private final int parent;

	public DelegateMarkerDelayedCellSetterFactory(CsvMapper<P> mapper, Setter<T, P> setter, int index, int parent) {
		this.mapper = mapper;
		this.setter = setter;
        this.index = index;
        this.parent = parent;
    }

	public CsvMapper<P> getMapper() {
		return mapper;
	}

	public Setter<T, P> getSetter() {
		return setter;
	}

	@SuppressWarnings("unchecked")
    @Override
	public DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {

        if (parent == index) {
            final DelegateDelayedCellSetter<T, P> delayedCellSetter = new DelegateDelayedCellSetter<T, P>(this, index, breakDetector);
            cellHandlers[index] = delayedCellSetter.getCellHandler();
            return delayedCellSetter;
        } else {
            return new DelegateDelayedCellSetter<T, P>((CsvMapperCellConsumer<P>) cellHandlers[parent], index);
        }
	}

    @Override
    public boolean hasSetter() {
        return setter != null;
    }

    @Override
    public String toString() {
        return "DelegateMarkerDelayedCellSetter{" +
                "jdbcMapper=" + mapper +
                ", setter=" + setter +
                '}';
    }
}
