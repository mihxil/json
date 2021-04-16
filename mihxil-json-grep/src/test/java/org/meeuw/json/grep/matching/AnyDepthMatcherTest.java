package org.meeuw.json.grep.matching;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.meeuw.json.PathEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class AnyDepthMatcherTest {

    @Test
    public void testMatchCounts() throws Exception {
        AnyDepthMatcher matcher = new AnyDepthMatcher();
        assertEquals(2, matcher.matchCounts(Arrays.asList(mock(PathEntry.class), mock(PathEntry.class))));
    }
}
