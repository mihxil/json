package org.meeuw.main;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.ginsberg.junit.exit.DisallowExitSecurityManager;

public class AbstractMainTest {

    protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private final SecurityManager original = System.getSecurityManager();


    @BeforeEach
    public void setUpStreamsAndSecurityManager() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        System.setSecurityManager(new DisallowExitSecurityManager(original));
    }

    @AfterEach
    public void restoreStreamsAndSecurityManager() {
        System.setOut(originalOut);
        System.out.println(outContent);
        System.setErr(originalErr);
        System.err.println(errContent);
        System.setSecurityManager(original);
    }

}
