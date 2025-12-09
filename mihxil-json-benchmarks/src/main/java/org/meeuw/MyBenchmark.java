package org.meeuw;

import tools.jackson.core.JsonParser;

import java.io.IOException;

import org.meeuw.json.Util;
import org.meeuw.json.grep.*;
import org.meeuw.json.grep.parsing.Parser;
import org.openjdk.jmh.annotations.Benchmark;

public class MyBenchmark {

    @Benchmark
    public void baseline() {
        // do nothing, this is a baseline
    }


    @Benchmark
    public void grep() throws IOException {
        JsonParser parser = Util.getJsonParser(getClass().getResourceAsStream("/test.json"));
        Grep grep = new Grep(Parser.parsePathMatcherChain("a,b"), parser);
        for(GrepEvent e : grep) {

        }

    }
    @Benchmark
    public void grepMain() throws IOException {
        JsonParser parser = Util.getJsonParser(getClass().getResourceAsStream("/test.json"));
        GrepMain main = new GrepMain(Parser.parsePathMatcherChain("a,b"));
        main.read(parser, System.out);
    }

}
