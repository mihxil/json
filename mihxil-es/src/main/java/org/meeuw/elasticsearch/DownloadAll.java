package org.meeuw.elasticsearch;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
@Data
@Slf4j
public class DownloadAll {

    public static final String ID = "_id";
    public static final String TYPE = "_type";
    public static final String SOURCE = "_source";
    public static final String SCORE = "_score";

    private final String elasticSearchServer;
    private final String elasticSearchDatabase;

    private String sort = null;
    private Long max= null;
    private Long offset = null;
    private List<String> types = null;


    public DownloadAll(String elasticSearchServer, String elasticSearchDatabase) {
        this.elasticSearchServer = elasticSearchServer;
        this.elasticSearchDatabase = elasticSearchDatabase;
    }

    @lombok.Builder(builderClassName = "Builder")
    private  DownloadAll(
        String elasticSearchServer,
        String elasticSearchDatabase,
        String sort,
        Long max,
        Long offset,
        List<String> types
    ) {
        this(elasticSearchServer, elasticSearchDatabase);
        this.sort = sort;
        this.max = max;
        this. offset = offset;
        this.types = types;
    }

    private String getTypesString() {
        String typesString = "";
        if (types != null && types.size() > 0) {
            typesString = String.join(",", types) + "/";
        }
        return typesString;
    }



    private void download(Status status, InputStream is, final OutputStream out) {
        iterate(status, is, (node) -> {
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            Util.write(node.getSource(), writer);
            byte[] bytes = writer.toByteArray();
            status.byteCount += bytes.length;
            try {
                out.write(bytes);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }

        }, (stat) -> {
            if (stat.count > 0) {
                try {
                    out.write(",\n".getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                status.byteCount += 2;
            }
            if (stat.count % 1000 == 0) {
                System.err.print(".");
                if (status.count > 0 && status.count % 50000 == 0) {
                    System.err.println("\n");
                }
            }});
    }

    private void iterate(Status status, InputStream is, Consumer<ESObject> consumer, Consumer<Status> separate) {
        JsonParser parser = Util.getJsonParser(is);
        Grep grep = new Grep(new PathMatcherOrChain(
            new SinglePathMatcher(new PreciseMatch("_scroll_id")),
            new SinglePathMatcher(new PreciseMatch("hits"), new PreciseMatch("hits"), new ArrayEntryMatch())), parser);
        status.scroll_id = null;
        long subCount = 0;
        for (GrepEvent event : grep) {
            if (event.getPath().toString().equals("_scroll_id")) {
                status.scroll_id = event.getValue();
            } else {
                separate.accept(status);
                status.count++;
                if (offset != null && status.count < offset) {
                    continue;
                }

                subCount++;
                Map<String, Object> node = (Map<String, Object>) event.getEvent().getNode();
                ESObject esObject = ESObject.builder()
                    .id((String) node.get(ID))
                    .type((String) node.get(TYPE))
                    .score((Double) node.get(SCORE))
                    .source((Map<String, Object>) node.get(SOURCE))
                    .build();

                consumer.accept(esObject);
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

    protected InputStream openStream(Status status) throws IOException {
        if (status.scroll_id != null) {
            URL url = new URL(elasticSearchServer + "_search/scroll?scroll=1m");
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.getOutputStream().write(status.scroll_id.getBytes());
            status.calls++;
            return con.getInputStream();
        } else {
            String u = elasticSearchServer + elasticSearchDatabase + "/" + getTypesString() + "_search?search_type=scan&scroll=10&size=50";
            if (sort != null) {
                u += "&sort=" + sort;
            }
            log.info("Using " + u);
            URL url = new URL(u);
            return url.openStream();
        }
    }


    public Status download(OutputStream out) throws IOException {
        out.write("[".getBytes());
        Status status = new Status();
        status.byteCount++;
        download(status, openStream(status), out);
        while (! status.ready) {
            download(status, openStream(status), out);
        }
        status.byteCount++;
        out.write("]".getBytes());
        return status;
    }

    public Status iterate(Consumer<ESObject> consumer) throws IOException {
        return iterate(consumer, (status) -> {});
    }

    public  Status iterate(Consumer<ESObject> consumer, Consumer<Status> separate) throws IOException {
        Status status = new Status();
        iterate(status, openStream(status), consumer, separate);
        while (!status.ready) {
            iterate(status, openStream(status), consumer, separate);
        }
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

    public static void main(String[] args) throws IOException {
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

        OutputStream output = cmd.getArgs().length == 2 || cmd.getArgs()[2].equals("-") ? System.out : new FileOutputStream(cmd.getArgs()[2]);
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
