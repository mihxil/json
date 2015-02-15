package org.meeuw.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilTest {

    @Test
    public void constructor() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Class<?> cls = Util.class;
        final Constructor<?> c = cls.getDeclaredConstructors()[0];
        assertFalse(c.isAccessible());
        c.setAccessible(true);
        c.newInstance((Object[]) null);
    }

}
