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
            LOGGER.error("Config file descriptor is null");
            throw new RuntimeException("Config file descriptor is null");
        }

        ArrayList<String> listOfVars = new ArrayList<>();

        File baseDir = new File(config.getBaseDir());
        if (!baseDir.isDirectory()) {
            LOGGER.error("Base dir should be a dir, not a file");
            throw new RuntimeException("Base dir should be a dir, not a file");
        }

        for (File vars : baseDir.listFiles()) {
            // skip nested dir
            if (vars.isDirectory()) {
                continue;
            }

            // skip non-json files
            if (!vars.getName().endsWith(".json")) {
                continue;
            }

            String valsPath = config.getCombineDir() + "/" + vars.getName() + ".combine";
            File vals = new File(valsPath);
            if (vals.exists() && vals.isFile()) {
                listOfVars.add(vars.getName());
            }
        }

        return listOfVars.iterator();
    }
}
