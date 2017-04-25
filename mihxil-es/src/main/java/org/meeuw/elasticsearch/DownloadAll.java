package org.meeuw.elasticsearch;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.cli.*;
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

    private String sort = null;
    private Long max= null;
    private Long offset = null;
    private List<String> types = null;


    public DownloadAll(String elastischSearchServer, String elastischSearchDatabase) throws MalformedURLException {
        this.elastischSearchServer = elastischSearchServer;
        this.elastischSearchDatabase = elastischSearchDatabase;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
    private String getTypesString() {
        String typesString = "";
        if (types != null && types.size() > 0) {
            typesString = types.stream().collect(Collectors.joining(",")) + "/";
        }
        return typesString;
    }

    private void download(Status status, OutputStream out) throws IOException {

        URL url = new URL(elastischSearchServer + "_search/scroll?scroll=1m");
        URLConnection con = url.openConnection();
        con.setDoOutput(true);
        con.getOutputStream().write(status.scroll_id.getBytes());
        download(status, con.getInputStream(), out);
        status.calls++;
    }

    private void download(Status status, InputStream is, OutputStream out) throws IOException {
        JsonParser parser = Util.getJsonParser(is);
        Grep grep = new Grep(new PathMatcherOrChain(
            new SinglePathMatcher(new PreciseMatch("_scroll_id")),
            new SinglePathMatcher(new PreciseMatch("hits"), new PreciseMatch("hits"), new ArrayEntryMatch(), new PreciseMatch("_source"))), parser);
        status.scroll_id = null;
        long subCount = 0;
        for (GrepEvent event : grep) {
            if (event.getPath().toString().equals("_scroll_id")) {
                status.scroll_id = event.getValue();
            } else {
                if (status.count > 0) {
                    out.write(",\n".getBytes());
                    status.byteCount += 2;
                }
                if (status.count % 1000 == 0) {
                    System.err.print(".");
                    if (status.count > 0 && status.count % 50000 == 0) {
                        System.err.println("\n");
                    }
                }
                status.count++;
                if (offset != null && status.count < offset) {
                    continue;
                }

                subCount++;
                byte[] bytes = event.getNode().getBytes();
                status.byteCount += bytes.length;
                out.write(bytes);
            }
        }
        if (max != null && status.count > max) {
            status.ready = true;
        }
        if (subCount == 0 && status.calls > 0) {
            status.ready = true;
        }
        status.calls++;
    }

    private Status download(OutputStream out) throws IOException {
        out.write("[".getBytes());
        String u = elastischSearchServer + elastischSearchDatabase + "/" + getTypesString() + "_search?search_type=scan&scroll=10&size=50";
        if (sort != null) {
            u += "&sort=" + sort;
        }
        System.err.println("Using " + u);
        URL url = new URL(u);
        Status status = new Status();
        status.byteCount++;
        download(status, url.openStream(), out);
        while (! status.ready) {
            download(status, out);
        }
        status.byteCount++;
        out.write("]".getBytes());
        return status;
    }

    private static class Status {
        long startTime = System.currentTimeMillis();
        String scroll_id = null;
        long count = 0;
        long calls = 0;
        boolean ready = false;
        long byteCount = 0;
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("downloadall <elastic search server> <elastic database> [<output file>]", options);
    }

    public static void main(String[] args) throws IOException, ParseException {
        Options options =
            new Options()
                .addOption(Option.builder("types").hasArg().build())
                .addOption("sort", true, "sort")
                .addOption("max", true, "max")
                .addOption("offset", true, "offset");


        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            if (cmd.getArgList().size() < 2) {
                printHelp(options);
                System.exit(1);
            }

        } catch (ParseException pe) {
            System.err.println(pe.getMessage());
            printHelp(options);
            System.exit(1);
            return;
        }

        OutputStream output = cmd.getArgs().length == 2 ? System.out : new FileOutputStream(cmd.getArgs()[2]);
        DownloadAll all = new DownloadAll(cmd.getArgs()[0], cmd.getArgs()[1]);

        if (cmd.hasOption("sort")) {
            all.setSort(cmd.getOptionValue("sort"));
        }
        if (cmd.hasOption("max")) {
            all.setMax(Long.parseLong(cmd.getOptionValue("max")));
        }
        if (cmd.hasOption("offset")) {
            all.setOffset(Long.parseLong(cmd.getOptionValue("offset")));
        }
        if (cmd.hasOption("types")) {
            all.setTypes(Arrays.asList(cmd.getOptionValue("types").split(",")));
        }
        Status status = all.download(output);
        output.close();
        System.err.println("\nready "+ status.byteCount + " in "  + TimeUnit.SECONDS.convert(System.currentTimeMillis() - status.startTime, TimeUnit.MILLISECONDS) + " s");
        System.exit(0);
    }
}
