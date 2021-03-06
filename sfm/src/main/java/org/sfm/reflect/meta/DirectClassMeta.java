package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.*;
import org.sfm.reflect.impl.NullSetter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public final class DirectClassMeta<T> implements ClassMeta<T> {


    public static final String[] HEADERS = new String[]{""};
    public static final IdentityGetter IDENTITY_GETTER = new IdentityGetter();
    private final ReflectionService reflectService;
	private final Type target;

	public DirectClassMeta(Type target, ReflectionService reflectService) throws MapperBuildingException {
		this.target = target;
		this.reflectService = reflectService;
	}


    @Override
    public ReflectionService getReflectionService() {
        return reflectService;
    }

    @Override
    public PropertyFinder<T> newPropertyFinder() {
        return new DirectPropertyFinder();
    }

    @Override
    public Type getType() {
        return target;
    }

    @Override
    public String[] generateHeaders() {
        return HEADERS;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public List<InstantiatorDefinition> getInstantiatorDefinitions() {
        return Collections.emptyList();
    }

    public class DirectPropertyFinder implements PropertyFinder<T> {
        @Override
        public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
            return new DirectPropertyMeta<E>("direct", reflectService, target);
        }

        @Override
        public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
            return null;
        }
    }

    public class DirectPropertyMeta<E> extends PropertyMeta<T, E> {
        private final Type type;

        public DirectPropertyMeta(String name, ReflectionService reflectService, Type type) {
            super(name, reflectService);
            this.type = type;
        }


        @Override
        public Setter<T, E> getSetter() {
            return NullSetter.setter();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Getter<T, E> getGetter() {
            return (Getter<T, E>) IDENTITY_GETTER;

        }

        @Override
        public Type getPropertyType() {
            return type;
        }

        @Override
        public String getPath() {
            return ".";
        }

        @Override
        public String toString() {
            return "DirectPropertyMeta{" +
                    "type=" + type +
                    ",name=" + getName() +
                    '}';
        }

        @Override
        public boolean isDirect() {
            return true;
        }

    }

    @Override
    public String toString() {
        return "DirectClassMeta{" +
                "target=" + target +
                '}';
    }
}
