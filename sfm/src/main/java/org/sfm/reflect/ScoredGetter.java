package org.sfm.reflect;


import org.sfm.reflect.impl.NullGetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ScoredGetter<T, P>  {

    @SuppressWarnings("unchecked")
    public static final ScoredGetter NULL = new ScoredGetter(Integer.MIN_VALUE, NullGetter.NULL_GETTER);
    private final int score;
    private final Getter<T, P> getter;

    public ScoredGetter(int score, Getter<T, P> getter) {
        this.score = score;
        this.getter = getter;
    }

    public int getScore() {
        return score;
    }

    public Getter<T, P> getGetter() {
        return getter;
    }

    @Override
    public String toString() {
        return "ScoredGetter{" +
                "score=" + score +
                ", getter=" + getter +
                '}';
    }

    @SuppressWarnings("unchecked")
    public static <T, P> ScoredGetter<T, P> nullGetter() {
        return NULL;
    }

    public boolean isBetterThan(ScoredGetter<T, P> scoredGetter) {
        return score > scoredGetter.score;
    }

    public ScoredGetter<T, P> best(ScoredGetter<T, P> getter) {
        return isBetterThan(getter) ? this : getter;
    }

    public static <T, P> ScoredGetter<T, P> of(Getter<T, P> getter, int score) {
        return new ScoredGetter<T, P>(score, getter);
    }

    public static <T, P> ScoredGetter<T, P> ofMethod(Method method, Getter<T, P> methodGetter) {
        int score = 2;
        if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
            score *= 2;
        }
        return of(methodGetter, score);
    }

    public static <T, P> ScoredGetter<T, P> ofField(Field field, Getter<T, P> fieldGetter) {
        return of(fieldGetter, 1);
    }
}
