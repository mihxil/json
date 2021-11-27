package org.meeuw.main;

import lombok.SneakyThrows;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ThrowableAssert;

import com.ginsberg.junit.exit.SystemExitPreventedException;

import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {

    public static SystemAssert assertExitCode(ThrowableAssert.ThrowingCallable  runnable) {
        SystemAssert a = new SystemAssert(runnable);
        return a;
    }
    public static class SystemAssert extends AbstractAssert<SystemAssert, ThrowableAssert.ThrowingCallable> {

        SystemExitPreventedException systemExitPreventedException = null;
        boolean ran = false;
        protected SystemAssert(ThrowableAssert.ThrowingCallable  runnable) {
            super(runnable, SystemAssert.class);
        }

        protected void runIfNeeded() throws Throwable {
            if (! ran) {
                try {
                    actual.call();
                } catch (SystemExitPreventedException spe) {
                    this.systemExitPreventedException = spe;
                }
            }
        }
        @SneakyThrows
        public SystemAssert isEqualTo(int system)  {
            runIfNeeded();
            int statusCode = systemExitPreventedException == null ? 0 : systemExitPreventedException.getStatusCode();
            assertThat(statusCode).isEqualTo(system);
            return this;
        }

        public SystemAssert isNormal() {
            return isEqualTo(0);
        }
    }
}

