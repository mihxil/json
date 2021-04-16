package org.meeuw.json.grep.matching;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.meeuw.json.PathEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


public class AbstractKeyPatternTest {
    AbstractKeyPattern no = new AbstractKeyPattern() {
        @Override
        public boolean matches(PathEntry key) {
            return false;
        }
    };
    AbstractKeyPattern yes = new AbstractKeyPattern() {
        @Override
        public boolean matches(PathEntry key) {
            return true;
        }
    };

    @Test
    public void no() {
        assertEquals(-1, no.matchCounts(Arrays.asList(mock(PathEntry.class))));
        assertEquals(-1, no.matchCounts(Arrays.asList(mock(PathEntry.class))));

    }
    @Test
    public void yes() {
        assertEquals(1, yes.matchCounts(Arrays.asList(mock(PathEntry.class))));
        assertEquals(1, yes.matchCounts(Arrays.asList(mock(PathEntry.class))));

    }

}
