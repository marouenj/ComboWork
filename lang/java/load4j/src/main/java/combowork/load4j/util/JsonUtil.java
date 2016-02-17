package combowork.load4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for {@link JsonNode}
 *
 * @author marouenj
 */
public class JsonUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private final static String VALS_KEY = "Case";

    /**
     * List of log messages intrinsic to {@link JsonUtil}
     */
    private enum LogMessages {
        UNABLE_TO_OPEN_FILE("Unable to open file, %s"),
        UNABLE_TO_CONVERT_STREAM_TO_STRING("Unable to convert Stream to String in, %s"),
        UNABLE_TO_PARSE_JSON("Unable to parse json tree. Json file is, %s"),
        INVALID_TEST_CASES("Test cases are invalid");

        private final String text;

        LogMessages(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public String getText(String... vals) {
            return String.format(getText(), vals);
        }
    }

    private final static ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode fromFile(String path) {
        String text;
        try {
            text = IOUtils.toString(new FileReader(path));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                String msg = LogMessages.UNABLE_TO_OPEN_FILE.getText(path);
                LOGGER.error(msg);
                throw new RuntimeException(msg);
            } else {
                String msg = LogMessages.UNABLE_TO_CONVERT_STREAM_TO_STRING.getText(path);
                LOGGER.error(msg);
                throw new RuntimeException(msg);
            }
        }

        return fromText(text, path);
    }

    public static JsonNode fromText(String text, String path) {
        JsonNode tree;
        try {
            tree = MAPPER.readTree(text);
        } catch (IOException e) {
            String msg = LogMessages.UNABLE_TO_PARSE_JSON.getText(path);
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        return tree;
    }

    public static boolean isValid(JsonNode testcases) {
        if (testcases == null || testcases.size() == 0) {
            return false;
        }

        if (testcases.getNodeType() != JsonNodeType.ARRAY) { // root should start with an array of objects
            return false;
        }

        List<JsonNodeType> pattern;
        try {
            pattern = pattern(testcases.get(0).get(VALS_KEY));
        } catch (RuntimeException e) {
            return false;
        }

        for (JsonNode testcase : testcases) {
            if (testcase.getNodeType() != JsonNodeType.OBJECT) {
                return false;
            }

            JsonNode vals = testcase.get(VALS_KEY);

            if (vals.size() != pattern.size()) {
                return false;
            }

            int i = -1;
            for (JsonNode val : vals) {
                i++;
                if (val.getNodeType() != pattern.get(i)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static ArrayList<JsonNodeType> pattern(JsonNode testcase) {
        ArrayList<JsonNodeType> pattern = new ArrayList<>(testcase.size());

        if (testcase.size() == 0) {
            throw new RuntimeException(LogMessages.INVALID_TEST_CASES.getText());
        }

        for (JsonNode val : testcase) {
            if (val.getNodeType() == JsonNodeType.BOOLEAN
                    || val.getNodeType() == JsonNodeType.NUMBER
                    || val.getNodeType() == JsonNodeType.STRING) {
                pattern.add(val.getNodeType());
            } else {
                throw new RuntimeException(LogMessages.INVALID_TEST_CASES.getText());
            }
        }

        return pattern;
    }
}
