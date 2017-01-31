package org.meeuw.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Inspired by import com.jcabi.manifests.Manifests, but that has all kind dependencies we don't want.

 * @author Michiel Meeuwissen
 * @since 0.6
 */
public class Manifests {


    public static String read(String value) throws IOException {
        Map<String, String> map = new HashMap<>();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
        while (urls.hasMoreElements()) {
            map.putAll(load(urls.nextElement().openStream()));
        }
        return map.get(value);
    }

    private static Map<String, String> load(final InputStream stream) throws IOException {
        final Map<String, String> props =
                new HashMap<>();
        final Manifest manifest = new Manifest(stream);
        final Attributes attrs = manifest.getMainAttributes();
        for (final Object key : attrs.keySet()) {
            final String value = attrs.getValue(
                    Attributes.Name.class.cast(key)
            );
            props.put(key.toString(), value);
        }
        return props;
    }

}
