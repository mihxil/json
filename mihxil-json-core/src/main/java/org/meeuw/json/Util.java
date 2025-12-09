package org.meeuw.json;

import lombok.extern.java.Log;
import tools.jackson.core.*;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.util.DefaultPrettyPrinter;

import java.io.*;
import java.net.URL;
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
        JsonParser jp = JSONFACTORY
            .createParser(ObjectReadContext.empty(), in);
        return jp;
    }

    public static JsonParser getJsonParser(Reader in) {
        JsonParser jp = JSONFACTORY.createParser(ObjectReadContext.empty(), in);
        return jp;
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
    private static void write(Object o, JsonGenerator gen)  {
        if (o == null) {
            gen.writeNull();
        } else if (o instanceof Long) {
            gen.writeNumber((Long) o);
        } else if (o instanceof Double) {
            gen.writeNumber((Double) o);
        } else if (o instanceof Boolean) {
            gen.writeBoolean((Boolean) o);
        } else if (o instanceof String) {
            gen.writeString((String) o);
        } else if (o instanceof Float) {
            gen.writeNumber((Float) o);
        } else if (o instanceof Integer) {
            gen.writeNumber((Integer) o);
        } else if (o instanceof Map) {
            write((Map) o, gen);
        } else if (o instanceof List) {
            write((List) o, gen);
        } else {
            gen.writePOJO(o);
        }
    }


    private static File getFile(String string) {
        if ("-".equals(string) || string == null) return null;
        return new File(string);
    }

    public static InputStream getInput(String[] argv, int pos) throws IOException {
        String arg = argv.length > pos ? argv[pos] : null;
        File file = getFile(arg);
        if (file == null) {
            return System.in;
        } else if (!file.exists()) {
            return new URL(arg).openStream();
        } else {
            return new FileInputStream(file);
        }
    }

    public static OutputStream getOutput(String[] argv, int pos) throws IOException {
        if (argv.length > pos) {
            File file = getFile(argv[pos]);
            return file == null ? System.out : new FileOutputStream(file);
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
