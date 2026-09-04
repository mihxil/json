package org.meeuw.json;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UtilTest {

    @Test
    public void constructor() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Class<?> cls = Util.class;
        final Constructor<?> c = cls.getDeclaredConstructors()[0];
        assertFalse(c.isAccessible());
        c.setAccessible(true);
        c.newInstance((Object[]) null);
    }

    @Test
    public void httpInput() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/input", exchange -> {
            byte[] response = "content".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.createContext("/redirect", exchange -> {
            exchange.getResponseHeaders().add("Location", "/input");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });
        server.createContext("/missing", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.start();
        String baseUrl = "http://localhost:" + server.getAddress().getPort();
        try {
            try (var in = Util.getInput(new String[] {baseUrl + "/redirect"}, 0)) {
                assertThat(new String(in.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("content");
            }
            assertThatThrownBy(() -> Util.getInput(new String[] {baseUrl + "/missing"}, 0))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("HTTP 404");
        } finally {
            server.stop(0);
        }
    }

}
