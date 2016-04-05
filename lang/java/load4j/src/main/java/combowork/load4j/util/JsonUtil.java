package combowork.load4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for {@link JsonNode}
 *
 * @author marouenj
 *
 */
public class JsonUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final static String VALS_KEY = "Case";

    /**
     * List of log messages intrinsic to {@link JsonUtil}
     *
     */
    private enum LogMessages {
        UNABLE_TO_OPEN_FILE("Unable to open file, %s"),
        UNABLE_TO_CONVERT_STREAM_TO_STRING("Unable to convert Stream to String in, %s"),
        UNABLE_TO_PARSE_JSON("Unable to parse json tree for file, %s");

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

    /**
     * Load a ComboWork test suite from a file into a {@link JsonNode}
     *
     * @param file handle to the file containing the test suite
     * @return JsonNode view of the test cases
     *
     */
    public static JsonNode fromFile(File file) {
        String path = file.getPath();
        String text;
        try {
            text = IOUtils.toString(new FileReader(file));
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

    /**
     * Parse a test suite in text into a {@link JsonNode}
     *
     * @param text load of the test suite
     * @param path path of the file containing the test suite
     * @return JsonNode view of the test cases
     *
     */
    private static JsonNode fromText(String text, String path) {
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

    /**
     * Validate a ComboWork test suite
     *
     * @param testCases Test cases to be validated
     * @return Validation result
     *
     */
    public static boolean isValid(JsonNode testCases) {
        if (testCases == null || testCases.size() == 0) {
            return false;
        }

        if (testCases.getNodeType() != JsonNodeType.ARRAY) { // root should start with an array of objects
            return false;
        }

        List<JsonNodeType> pattern;
        try {
            pattern = pattern(testCases.get(0));
        } catch (RuntimeException e) {
            return false;
        }

        for (JsonNode testcase : testCases) {
            if (testcase.getNodeType() != JsonNodeType.OBJECT) {
                return false;
            }

            JsonNode vals = testcase.get(VALS_KEY);
            if (vals == null || vals.size() != pattern.size()) {
                return false;
            }

            int i = -1;
            for (JsonNode val : vals) {
                i++;

                if (pattern.get(i) == JsonNodeType.NULL) { // hasn't encounter a deterministic value yet
                    pattern.set(i, val.getNodeType());
                    continue;
                }

                if (val.getNodeType() != pattern.get(i)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Infer the pattern of the test cases from the first entry
     * All subsequent entries will be checked against the pattern in {@link JsonUtil#isValid(JsonNode)}
     *
     * @param first First entry in the test case file to infer the pattern from
     * @return Pattern
     */
    private static ArrayList<JsonNodeType> pattern(JsonNode first) {
        if (first.getNodeType() != JsonNodeType.OBJECT) {
            throw new RuntimeException();
        }

        JsonNode vals = first.get(VALS_KEY);
        if (vals == null || vals.size() == 0) {
            throw new RuntimeException();
        }

        ArrayList<JsonNodeType> pattern = new ArrayList<>(vals.size());

        for (JsonNode val : vals) {
            if (val.getNodeType() == JsonNodeType.BOOLEAN
                    || val.getNodeType() == JsonNodeType.NUMBER
                    || val.getNodeType() == JsonNodeType.STRING
                    || val.getNodeType() == JsonNodeType.NULL) {
                pattern.add(val.getNodeType());
            } else {
                throw new RuntimeException();
            }
        }

        return pattern;
    }

    /**
     * Map test cases to a {@link Object}[][]
     *
     * @param testCases test cases
     * @return view of the test cases
     */
    public static Object[][] convertToObjectMatrix(JsonNode testCases) {
        int nbreTestCases = testCases.size();
        int nbreVals = testCases.get(0).get(VALS_KEY).size();

        Object[][] view = new Object[nbreTestCases][nbreVals];

        int i = -1;
        for (JsonNode testCase : testCases) {
            i++;
            JsonNode vals = testCase.get(VALS_KEY);
            int j = -1;
            for (JsonNode val : vals) {
                j++;
                view[i][j] = jsonToJavaType(val);
            }
        }

        return view;
    }

    /**
     * Json to Java type mapper
     *
     * @param node Json data
     * @return Java data
     */
    private static Object jsonToJavaType(JsonNode node) {
        if (node.isNumber()) {
            return node.numberValue();
        }

        if (node.isBoolean()) {
            return node.asBoolean();
        }

        if (node.isTextual()) {
            return node.asText();
        }

        if (node.isNull()) {
            return null;
        }

        throw new RuntimeException();
    }
}
