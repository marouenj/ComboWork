package combowork.marshal4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Utility class for the different operations on files and folders
 */
public class FileUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static Iterator<String> getListOfVars(CliUtil.Config config) {
        if (config == null) {
            LOGGER.error(LogMessages.CONFIG_IS_NULL.getText());
            throw new RuntimeException(LogMessages.CONFIG_IS_NULL.getText());
        }

        File baseDir = new File(config.getBaseDir());
        if (!baseDir.isDirectory()) {
            LOGGER.error(LogMessages.BASE_DIR_IS_A_FILE.getText());
            throw new RuntimeException(LogMessages.BASE_DIR_IS_A_FILE.getText());
        }

        ArrayList<String> listOfVars = new ArrayList<>();
        for (File var : baseDir.listFiles()) {
            // skip nested dir
            if (var.isDirectory()) {
                continue;
            }

            // skip non-json files
            if (!var.getName().endsWith(".json")) {
                continue;
            }

            String pathToVal = config.getPathToCombine() + "/" + var.getName();
            File val = new File(pathToVal);
            if (val.exists() && val.isFile()) {
                listOfVars.add(var.getName());
            }
        }

        return listOfVars.iterator();
    }

    private enum LogMessages {
        CONFIG_IS_NULL ("Config is null"),
        BASE_DIR_IS_A_FILE ("Base dir should be a dir, not a file");

        private final String msg;

        LogMessages(String msg) {
            this.msg = msg;
        }

        public String getText() {
            return msg;
        }
    }
}
