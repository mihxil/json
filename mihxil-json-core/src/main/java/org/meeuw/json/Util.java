package org.meeuw.json;

import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Michiel Meeuwissen
 */
public class Util {

    private Util() {}

    @SneakyThrows({IOException.class})
    public static JsonParser getJsonParser(InputStream in)  {
        JsonParser jp = getJsonFactory().createParser(in);
        setJsonParserOptions(jp);
        return jp;
    }

    @SneakyThrows({IOException.class})
    public static JsonParser getJsonParser(Reader in) {
        JsonParser jp = getJsonFactory().createParser(in);
        setJsonParserOptions(jp);
        return jp;
    }

    public static JsonParser getJsonParser(String string) throws IOException {
        return getJsonParser(new StringReader(string));
    }

    @SneakyThrows({IOException.class})
    public static void write(Object map, Writer writer) {
        JsonGenerator gen = getJsonFactory().createGenerator(writer);
        write(map, gen);
        gen.close();
    }

    public static void write(Object map, OutputStream writer) {
        try {
            JsonGenerator gen = getJsonFactory().createGenerator(writer);
            write(map, gen);
            gen.close();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }
    public static void write(Map<String, Object> map, JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            gen.writeFieldName(e.getKey());
            write(e.getValue(), gen);
        }
        gen.writeEndObject();
    }

    private static  void write(List<Object> os, JsonGenerator gen) throws IOException {
        gen.writeStartArray();
        for (Object o : os) {
            write(o, gen);
        }
        gen.writeEndArray();
    }
    private static void write(Object o, JsonGenerator gen) throws IOException {
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
            gen.writeObject(o);
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

    protected static void setJsonParserOptions(JsonParser jp) {
        jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        jp.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        jp.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public static JsonFactory getJsonFactory() {
        return new JsonFactory(new ObjectMapper());
    }
}
