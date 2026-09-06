package org.meeuw.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.cli.help.OptionFormatter;
import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextHelpAppendable;
import org.apache.commons.cli.help.TextStyle;
import org.meeuw.util.Manifests;

public class MainUtil {

    private MainUtil() {

    }

    public static String version() {
        try {
            return Manifests.read("ProjectVersion");
        } catch (IOException e) {
            return "<unknown>";
        }
    }

    public static void ignoreArrays(Options options){
        options.addOption(new Option("ia", "ignoreArrays", false, "Ignore arrays (no need to match those)"));
    }
    public static void ignoreCase(Options options){
        options.addOption(new Option("ic", "ignoreCase", false, "Ignore case"));
    }

    public static void ignore(Options options){
        ignoreArrays(options);
        ignoreCase(options);
        options.addOption(new Option("i", "ignore", false, "Ignore case and arrays"));

    }
    public static void debug(Options options) {
        options.addOption(new Option("d", "debug", false, "Debug"));
    }


    public static CommandLine commandLine(
        String name,
        String header,
        String argsDescription,
        Consumer<Options> addOptions,
        int expectedNumberOfArguments,
        String[] argv) throws ParseException, IOException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(new Option("?", "help", false, "print this message"));
        options.addOption(new Option("v", "version", false, "Print version"));
        addOptions.accept(options);
        CommandLine cl = parser.parse(options, argv, true);

        if (cl.hasOption("version")) {
            System.out.println(version());
            System.exit(0);
        }
        if (cl.hasOption("help") || (cl.getArgList().size() < expectedNumberOfArguments)) {
            TextHelpAppendable helpOutput = isTerminal() ? new BoldOptionHelpAppendable(System.out) : new TextHelpAppendable(System.out);
            helpOutput.setMaxWidth(120);
            HelpFormatter formatter = new UnixHelpFormatter(HelpFormatter.builder()
                .setHelpAppendable(helpOutput)
                .setOptionFormatBuilder(OptionFormatter.builder()
                    .setArgumentNameDelimiters("", "")
                    .setOptArgSeparator("="))
                .setShowSince(false)
            );

            formatter.printHelp(
                name + " [OPTIONS] " + argsDescription,
                header,
                options,
                "See https://github.com/mihxil/json",
                true
            );
            System.exit(0);
        }


        return cl;
     }

    private static boolean isTerminal() {
        return System.console() != null && System.getenv("NO_COLOR") == null;
    }

    private static final class UnixHelpFormatter extends HelpFormatter {

        private UnixHelpFormatter(Builder builder) {
            super(builder);
        }

        @Override
        public TableDefinition getTableDefinition(Iterable<Option> options) {
            TextStyle style = TextStyle.builder()
                .setAlignment(TextStyle.Alignment.LEFT)
                .setIndent(4)
                .setScalable(true)
                .get();
            List<List<String>> rows = new ArrayList<>();
            options.forEach(option -> {
                OptionFormatter formatter = getOptionFormatter(option);
                String optionHelp = formatter.getBothOpt();
                if (option.hasArg()) {
                    optionHelp += "=" + formatter.getArgName();
                }
                if (!formatter.getDescription().isEmpty()) {
                    optionHelp += "\n" + formatter.getDescription();
                }
                rows.add(Collections.singletonList(optionHelp));
            });
            return TableDefinition.from("", Collections.singletonList(style), Collections.singletonList(""), rows);
        }
    }

    private static final class BoldOptionHelpAppendable extends TextHelpAppendable {

        private static final String BOLD = "\u001B[1m";
        private static final String RESET = "\u001B[0m";

        private boolean formattingOptions;

        private BoldOptionHelpAppendable(Appendable output) {
            super(output);
        }

        @Override
        public void appendTable(TableDefinition table) throws IOException {
            formattingOptions = true;
            try {
                super.appendTable(table);
            } finally {
                formattingOptions = false;
            }
        }

        @Override
        protected Queue<String> makeColumnQueue(CharSequence columnData, TextStyle style) {
            Queue<String> lines = super.makeColumnQueue(columnData, style);
            if (!formattingOptions || columnData.isEmpty()) {
                return lines;
            }

            int descriptionStart = columnData.toString().indexOf('\n');
            int optionLineCount = descriptionStart < 0
                ? lines.size()
                : super.makeColumnQueue(columnData.subSequence(0, descriptionStart), style).size();
            Queue<String> formatted = new LinkedList<>();
            for (int line = 0; !lines.isEmpty(); line++) {
                String text = lines.remove();
                formatted.add(line < optionLineCount ? BOLD + text + RESET : text);
            }
            return formatted;
        }
    }
}
