package org.simpleflatmapper.core.reflect.primitive;

public interface LongSetter<T> {
	void setLong(T target, long value) throws Exception;
}