package org.meeuw;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.meeuw.json.Util;
import org.meeuw.json.grep.Grep;
import org.meeuw.json.grep.GrepEvent;
import org.meeuw.json.grep.matching.ArrayEntryMatch;
import org.meeuw.json.grep.matching.PathMatcherOrChain;
import org.meeuw.json.grep.matching.PreciseMatch;
import org.meeuw.json.grep.matching.SinglePathMatcher;

import com.fasterxml.jackson.core.JsonParser;

/**
 * This can download an entire elastic search database.
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class DownloadAll {

    private final String elastischSearchServer;
    private final String elastischSearchDatabase;

    public DownloadAll(String elastischSearchServer, String elastischSearchDatabase) throws MalformedURLException {
        this.elastischSearchServer = elastischSearchServer;
        this.elastischSearchDatabase = elastischSearchDatabase;
    }

    private void download(Status status, OutputStream out) throws IOException {
        URL url = new URL(elastischSearchServer + "_search/scroll?scroll=1m");
        URLConnection con = url.openConnection();
        con.setDoOutput(true);
        con.getOutputStream().write(status.scroll_id.getBytes());
        download(status, con.getInputStream(), out);
    }

    private void download(Status status, InputStream is, OutputStream out) throws IOException {
        JsonParser parser = Util.getJsonParser(is);
        Grep grep = new Grep(new PathMatcherOrChain(
            new SinglePathMatcher(new PreciseMatch("_scroll_id")),
            new SinglePathMatcher(new PreciseMatch("hits"), new PreciseMatch("hits"), new ArrayEntryMatch(), new PreciseMatch("_source"))), parser);
        for (GrepEvent event : grep) {
            if (event.getPath().toString().equals("_scroll_id")) {
                status.scroll_id = event.getValue();
            } else {
                if (status.count > 0) {
                    out.write(",\n".getBytes());
                }
                status.count++;
                out.write(event.getNode().getBytes());
            }
        }
    }

    private void download(OutputStream out) throws IOException {
        out.write("[".getBytes());
        URL url = new URL(elastischSearchServer + elastischSearchDatabase + "/_search?search_type=scan&scroll=10&size=50");
        Status status = new Status();
        download(status, url.openStream(), out);
        while (status.scroll_id != null) {
            download(status, out);
        }
        out.write("[".getBytes());

    }

    private static class Status {
        String scroll_id = null;
        long count = 0;
    }

    public static void main(String[] argv) throws IOException {
        if (argv.length < 2) {
            System.err.println("usage: <elastic search server> <elastic database> [<output file>]");
            System.exit(1);
        }
        OutputStream output = argv.length == 2 ? System.out : new FileOutputStream(argv[2]);

        DownloadAll all = new DownloadAll(argv[0], argv[1]);
        all.download(output);
        output.close();
        System.err.println("ready");
        System.exit(0);
    }
}
