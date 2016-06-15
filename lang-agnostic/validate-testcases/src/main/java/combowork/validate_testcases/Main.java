package combowork.validate_testcases;

import com.fasterxml.jackson.databind.JsonNode;
import combowork.common.JsonUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Entry point to validate-testcases
 *
 * @author marouenj
 */
public class Main {

    private final static Logger LOGGER = LoggerFactory.getLogger(Validate.class);

    private static final int EXIT_UNKNOWN_ERROR = 1;
    private static final int EXIT_PARSE_ERROR = 10;
    private static final int EXIT_IO_ERROR = 11;
    private static final int EXIT_UNVALID_WARN = 100;

    public static void main(String[] args) {
        try {
            Options options = new Options();
            options.addOption(Config.BASE_DIR_KEY, true, "path to base dir"); // set options keys

            // populate options
            CommandLine optionValues = null;
            try {
                CommandLineParser commandLineParser = new PosixParser();
                optionValues = commandLineParser.parse(options, args);
            } catch (ParseException e) {
                LOGGER.error("Invalid option(s)");
                System.exit(EXIT_PARSE_ERROR);
            }

            // override default values
            Config config = new Config();
            if (optionValues.hasOption(Config.BASE_DIR_KEY)) {
                config.setBaseDir(optionValues.getOptionValue(Config.BASE_DIR_KEY));
            }

            File baseDir = new File(config.getBaseDir());

            File[] files = baseDir.listFiles(); // list files
            if (files == null) {
                LOGGER.error("IO error");
                System.exit(EXIT_IO_ERROR);
            }

            for (File file : files) { // validate each file
                LOGGER.info("File: " + file.getName());
                JsonNode testCases = JsonUtil.fromFile(file);
                if (!Validate.isValid(testCases)) {
                    System.exit(EXIT_UNVALID_WARN);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unknown error");
            System.exit(EXIT_UNKNOWN_ERROR);
        }

        System.exit(0);
    }
}
