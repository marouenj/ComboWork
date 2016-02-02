package combowork.marshal4j.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(CliUtil.class);

    private final static String SWITCH_BASE_DIR = "base";
    private final static String SWITCH_COMBINE_DIR = "combine";
    private final static String SWITCH_MARSHAL4J_DIR = "marshal4j";

    public static Config parseCli(String[] args) {
        Options options = new Options();
        options.addOption(SWITCH_BASE_DIR, true, "base dir");
        options.addOption(SWITCH_COMBINE_DIR, true, "combine dir");
        options.addOption(SWITCH_MARSHAL4J_DIR, true, "marshal4j dir");

        CommandLineParser commandLineParser = new PosixParser();

        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.error(LogMessages.UNABLE_TO_PARSE.getText());
            throw new RuntimeException(LogMessages.UNABLE_TO_PARSE.getText());
        }

        Config config = new Config();
        if (commandLine.hasOption(SWITCH_BASE_DIR)) {
            config.setBaseDir(commandLine.getOptionValue(SWITCH_BASE_DIR));
        }
        if (commandLine.hasOption(SWITCH_COMBINE_DIR)) {
            config.setCombineDir(commandLine.getOptionValue(SWITCH_COMBINE_DIR));
        }
        if (commandLine.hasOption(SWITCH_MARSHAL4J_DIR)) {
            config.setMarshal4jDir(commandLine.getOptionValue(SWITCH_MARSHAL4J_DIR));
        }

        return config;
    }

    public static class Config {

        private String baseDir = ".";
        private String combineDir = "combine";
        private String marshal4jDir = "marshal4j";

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

        public String getPathToCombine() {
            return baseDir + "/" + combineDir;
        }

        public String getPathToMarshal4j() {
            return baseDir + "/" + marshal4jDir;
        }
    }

    /**
     * Log messages for this class logger
     */
    private enum LogMessages {
        UNABLE_TO_PARSE("Unable to parse commands");

        private final String msg;

        LogMessages(String msg) {
            this.msg = msg;
        }

        public String getText() {
            return msg;
        }

    }
}
