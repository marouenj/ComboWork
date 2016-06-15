package combowork.validate_testcases;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import combowork.common.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Validate {

    private final static Logger LOGGER = LoggerFactory.getLogger(Validate.class);

    /**
     * Validate a test cases file
     *
     * @param testCases Test cases
     * @return True if valid
     */
    public static boolean isValid(JsonNode testCases) {
        if (testCases == null) {
            LOGGER.warn("Invalid: null");
            return false;
        }

        if (testCases.getNodeType() != JsonNodeType.ARRAY) { // root should start with an array of objects
            LOGGER.warn("Invalid: root is not array");
            return false;
        }

        if (testCases.size() == 0) {
            LOGGER.warn("Invalid: no test cases");
            return false;
        }

        List<JsonNodeType> pattern;
        try {
            pattern = pattern(testCases.get(0));
        } catch (RuntimeException e) {
            LOGGER.warn("Invalid: unable to infer pattern");
            return false;
        }

        int i = -1;
        for (JsonNode testcase : testCases) {
            i++;

            if (testcase.getNodeType() != JsonNodeType.OBJECT) {
                LOGGER.warn("Invalid: test case '" + i + "' not an object");
                return false;
            }

            JsonNode vals = testcase.get(Config.KEY_TEST_CASE_VALS);
            if (vals == null) {
                LOGGER.warn("Invalid: test case '" + i + "' is null");
                return false;
            }

            if (vals.size() != pattern.size()) {
                LOGGER.warn("Invalid: test case '" + i + "' not matches pattern in size");
                return false;
            }

            int j = -1;
            for (JsonNode val : vals) {
                j++;

                if (pattern.get(j) == JsonNodeType.NULL) { // hasn't encounter a deterministic value yet
                    pattern.set(j, val.getNodeType());
                    continue;
                }

                if (val.getNodeType() != pattern.get(j)) {
                    LOGGER.warn("Invalid: test case '" + i + "' not matches pattern in types");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Infer the pattern of the test cases from the first entry
     * All subsequent entries will be checked against the pattern in {@link Validate#isValid(JsonNode)}
     *
     * @param first First entry in the test case file to infer the pattern from
     * @return Pattern
     */
    private static ArrayList<JsonNodeType> pattern(JsonNode first) {
        if (first.getNodeType() != JsonNodeType.OBJECT) {
            throw new RuntimeException();
        }

        JsonNode vals = first.get(Config.KEY_TEST_CASE_VALS);
        if (vals == null || vals.size() == 0) {
            throw new RuntimeException();
        }

        ArrayList<JsonNodeType> pattern = new ArrayList<>(vals.size());

        for (JsonNode val : vals) {
            if (val.getNodeType() == JsonNodeType.BOOLEAN
                    || val.getNodeType() == JsonNodeType.NUMBER
                    || val.getNodeType() == JsonNodeType.STRING
                    || val.getNodeType() == JsonNodeType.ARRAY
                    || val.getNodeType() == JsonNodeType.NULL) {
                pattern.add(val.getNodeType());
            } else {
                throw new RuntimeException();
            }
        }

        return pattern;
    }
}
