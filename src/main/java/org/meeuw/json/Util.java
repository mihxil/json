package org.meeuw.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.*;
import java.net.URL;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class Util {

    public static JsonParser getJsonParser(InputStream in) throws IOException {
        JsonParser jp = getJsonFactory().createParser(in);
        setJsonParserOptions(jp);
        return jp;
    }

    public static JsonParser getJsonParser(Reader in) throws IOException {
        JsonParser jp = getJsonFactory().createParser(in);
        setJsonParserOptions(jp);
        return jp;
    }

    public static JsonParser getJsonParser(String string) throws IOException {
        return getJsonParser(new StringReader(string));
    }


    private static File getFile(String string) {
        if ("-".equals(string) || string == null) return null;
        return new File(string);
    }

    public static InputStream getInput(String[] argv, int pos) throws IOException {
        final InputStream in;
        String arg = argv.length > pos ? argv[pos] : null;
        File file = getFile(arg);
        if (file == null) {
            in = System.in;
        } else if (!file.exists()) {
            in = new URL(arg).openStream();
        } else {
            in = new FileInputStream(file);
        }
        return in;
    }

    public static OutputStream getOutput(String[] argv, int pos) throws IOException {
        final OutputStream out;
        String arg = argv.length > pos ? argv[pos] : null;
        if (arg != null) {
            File file = getFile(arg);
            out = file == null ? System.out : new FileOutputStream(file);
        } else {
            out = System.out;
        }
        return out;
    }

    protected static void setJsonParserOptions(JsonParser jp) {
        jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        jp.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        jp.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    protected static JsonFactory getJsonFactory() {
        return new JsonFactory();
    }
}
