package org.meeuw.json;

import lombok.extern.java.Log;
import tools.jackson.core.*;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.util.DefaultPrettyPrinter;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author Michiel Meeuwissen
 */
@Log
public class Util {

    static final JsonFactory JSONFACTORY = JsonFactory.builder()
        .configure(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES, true)
        .configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true)
        .configure(JsonReadFeature.ALLOW_JAVA_COMMENTS, true)
        .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
        .build();

    private Util() {}

    public static JsonParser getJsonParser(InputStream in)  {
        return JSONFACTORY
            .createParser(ObjectReadContext.empty(), in);
    }

    public static JsonParser getJsonParser(Reader in) {
        return JSONFACTORY.createParser(ObjectReadContext.empty(), in);
    }

    public static JsonParser getJsonParser(String string) throws IOException {
        return getJsonParser(new StringReader(string));
    }

    public static void write(Object map, Writer writer) {
        try (JsonGenerator gen = JSONFACTORY.createGenerator(ObjectWriteContext.empty(), writer)) {
            write(map, gen);
        }
    }

    public static void write(Object map, OutputStream writer) {
        try (JsonGenerator gen = JSONFACTORY.createGenerator(ObjectWriteContext.empty(), writer)) {
            write(map, gen);
        }
    }

    public static void write(Map<String, Object> map, JsonGenerator gen) {
        gen.writeStartObject();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            gen.writeName(e.getKey());
            write(e.getValue(), gen);
        }
        gen.writeEndObject();
    }

    private static  void write(List<Object> os, JsonGenerator gen)  {
        gen.writeStartArray();
        for (Object o : os) {
            write(o, gen);
        }
        gen.writeEndArray();
    }
    @SuppressWarnings("rawtypes")
    private static void write(Object o, JsonGenerator gen)  {
        if (o == null) {
            gen.writeNull();
        } else if (o instanceof Long l) {
            gen.writeNumber(l);
        } else if (o instanceof Double d) {
            gen.writeNumber(d);
        } else if (o instanceof Boolean b) {
            gen.writeBoolean(b);
        } else if (o instanceof String s) {
            gen.writeString(s);
        } else if (o instanceof Float f) {
            gen.writeNumber(f);
        } else if (o instanceof Integer i) {
            gen.writeNumber(i);
        } else if (o instanceof Map map) {
            write(map, gen);
        } else if (o instanceof List list) {
            write(list, gen);
        } else {
            gen.writePOJO(o);
        }
    }



    public static InputStream getInput(String[] argv, int pos) throws IOException {
        String arg = argv.length > pos ? argv[pos] : null;
        if (arg == null || "-".equals(arg)) {
            return System.in;
        }
        Path file = FileSystems.getDefault().getPath(arg);
        if (! Files.exists(file)) {
            return new URL(arg).openStream();
        } else {
            return Files.newInputStream(file);
        }
    }

    public static OutputStream getOutput(String[] argv, int pos) throws IOException {
        if (argv.length > pos) {
            if ("-".equals(argv[pos])) {
                return System.out;
            }
            Path file = FileSystems.getDefault().getPath(argv[pos]);
            return Files.newOutputStream(file);
        } else {
            return System.out;
        }
    }

    public static JsonFactory getJsonFactory() {
        return JSONFACTORY;
    }

    public static ObjectWriteContext prettyWriteContext() {
        return new ObjectWriteContext.Base() {
            @Override
            public PrettyPrinter getPrettyPrinter() {
                return new DefaultPrettyPrinter();
            }
        };
    }
}
