package combowork.marshal4j.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CliUtil {

    private final static String VARS_DIR_SWITCH = "in";
    private final static String COMBINE_DIR_SWITCH = "combine";
    private final static String MARSHAL4J_DIR_SWITCH = "marshal4j";

    public static Config parseCli(String[] args) {
        Options options = new Options();
        options.addOption(VARS_DIR_SWITCH, true, "base dir");
        options.addOption(COMBINE_DIR_SWITCH, true, "combine output dir");
        options.addOption(MARSHAL4J_DIR_SWITCH, true, "marshal4j output dir");

        CommandLineParser commandLineParser = new PosixParser();

        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Config config = new Config();
        if (commandLine.hasOption(VARS_DIR_SWITCH)) {
            config.setBaseDir(commandLine.getOptionValue(VARS_DIR_SWITCH));
        }
        if (commandLine.hasOption(COMBINE_DIR_SWITCH)) {
            config.setCombineDir(commandLine.getOptionValue(COMBINE_DIR_SWITCH));
        }
        if (commandLine.hasOption(MARSHAL4J_DIR_SWITCH)) {
            config.setMarshal4jDir(commandLine.getOptionValue(MARSHAL4J_DIR_SWITCH));
        }

        return config;
    }

    public static class Config {

        private String baseDir = ".";
        private String combineDir = "./combine";
        private String marshal4jDir = "./marshal4j";

        public String getBaseDir() {
            return baseDir;
        }

        public void setBaseDir(String baseDir) {
            this.baseDir = baseDir;
        }

        public String getCombineDir() {
            return combineDir;
        }

        public void setCombineDir(String combineDir) {
            this.combineDir = combineDir;
        }

        public String getMarshal4jDir() {
            return marshal4jDir;
        }

        public void setMarshal4jDir(String marshal4jDir) {
            this.marshal4jDir = marshal4jDir;
        }
    }
}
