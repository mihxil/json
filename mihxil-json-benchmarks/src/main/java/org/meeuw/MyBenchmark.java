package org.meeuw;

import java.io.IOException;

import org.meeuw.json.Util;
import org.meeuw.json.grep.Grep;
import org.meeuw.json.grep.GrepEvent;
import org.meeuw.json.grep.GrepMain;
import org.meeuw.json.grep.parsing.Parser;
import org.openjdk.jmh.annotations.Benchmark;

import com.fasterxml.jackson.core.JsonParser;

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
