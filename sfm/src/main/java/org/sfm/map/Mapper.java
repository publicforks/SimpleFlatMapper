package org.sfm.map;


public interface Mapper<S, T> extends FieldMapper<S, T> {
	/**
	 * map source object to a new instance of T
	 * @param source object to map from
	 * @return a new mapped instance of T
	 * @throws MappingException if an exception occurs
	 */
    T map(S source) throws MappingException;

    T map(S source, MappingContext<? super S> context) throws MappingException;
}