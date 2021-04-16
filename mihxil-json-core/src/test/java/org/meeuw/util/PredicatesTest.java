package org.meeuw.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PredicatesTest {

    @Test
    public void constructor() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Class<?> cls = Predicates.class;
        final Constructor<?> c = cls.getDeclaredConstructors()[0];
        assertFalse(c.isAccessible());
        c.setAccessible(true);
        c.newInstance((Object[]) null);
    }

}
