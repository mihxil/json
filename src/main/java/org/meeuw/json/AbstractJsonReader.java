package org.meeuw.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.*;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;


public abstract class AbstractJsonReader {

    public void read(InputStream in) throws IOException {
        JsonParser jp = getJsonFactory().createParser(in);
        setJsonParserOptions(jp);
        read(jp);
    }

    public void read(Reader in) throws IOException {
        JsonParser jp = getJsonFactory().createParser(in);
        setJsonParserOptions(jp);
        read(jp);
    }

    protected void setJsonParserOptions(JsonParser jp) {
        jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        jp.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        jp.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public void read(JsonParser jp) throws IOException {


        int depth = 0;
        Deque<PathEntry> path = new Path();
        while (true) {
            JsonToken token = jp.nextToken();
            if (token == null) {
                break;
            }
            switch(token) {
                case START_OBJECT:
                    depth++;
                    break;
                case START_ARRAY:
                    depth++;
                    path.add(new ArrayEntry());
                    break;
                case END_OBJECT: {
                    depth--;
                    PathEntry prev = path.peekLast();
                    if (prev != null) {
                        if (prev instanceof ArrayEntry) {
                            ((ArrayEntry) prev).inc();
                        } else {
                            path.removeLast();
                        }
                    }
                    break;
                }
                case END_ARRAY: {
                    PathEntry prev = path.pollLast();
                    depth--;
                    break;
                }
                case FIELD_NAME:
                    String fieldName = jp.getText();
                    path.add(new KeyEntry(fieldName));

                    break;
            }

            handleToken(jp, token, path);

            switch(token) {
                case VALUE_STRING:
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL:
                case END_ARRAY:
                    PathEntry prev = path.peekLast();
                    if (prev instanceof ArrayEntry) {
                        ((ArrayEntry) prev).inc();
                    } else {
                        path.removeLast();
                    }
                    break;
            }

        }
        ready();
    }


    protected abstract void handleToken(JsonParser jp, JsonToken token, Deque<PathEntry> path) throws IOException;

    protected void ready() throws IOException {

    }


    protected JsonFactory getJsonFactory() {
        return new JsonFactory();
    }

    private static File getFile(String string) {
        if ("-".equals(string) || string == null) return null;
        return new File(string);
    }

    protected static InputStream getInput(String[] argv, int pos) throws IOException {
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

    protected static OutputStream getOutput(String[] argv, int pos) throws IOException {
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

    public static class Path extends ArrayDeque<PathEntry> {
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (PathEntry pe : this) {
                pe.append(builder);
            }
            return builder.toString();
        }
    }

    /**
     * Representation of one entry in the current 'path'.
     */
    public static interface PathEntry {

        void append(StringBuilder builder);

    }
    public static class KeyEntry implements PathEntry {
        final String key;

        public KeyEntry(String key) {
            this.key = key;
        }
        @Override
        public String toString() {
            return key;
        }

        @Override
        public void append(StringBuilder builder) {
            if (builder.length() > 0) builder.append('.');
            builder.append(key);
        }
    }
    public static class ArrayEntry implements PathEntry {
        Integer index = 0;

        public ArrayEntry() {
        }

        protected void inc() {
            index++;
        }

        @Override
        public String toString() {
            return "[" + index + "]";
        }

        @Override
        public void append(StringBuilder builder) {
            builder.append('[').append(index).append(']');
        }
    }
}
