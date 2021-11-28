package org.meeuw.json.grep;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import org.meeuw.main.AbstractMainTest;
import org.meeuw.main.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

class SedMainTest {

    public static class Main extends AbstractMainTest {

        @Test
        public void test() {
            System.setIn(new ByteArrayInputStream("{ \"items\" : [ { \"a\" : 'abc def'},  { \"a\" : 'xyz qwv'}]}".getBytes(StandardCharsets.UTF_8)));
            Assertions.assertExitCode(() -> SedMain.main(new String[] {"-ignoreArrays", "items.a~abc\\s*(.*)~def", "-", "-"})).isNormal();

            assertThat(outContent.toString()).isEqualTo("{\"items\":[{\"a\":\"def\"},{\"a\":\"xyz qwv\"}]}");

        }

    }

}
