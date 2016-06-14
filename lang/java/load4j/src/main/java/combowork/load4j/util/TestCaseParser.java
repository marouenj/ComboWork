package combowork.load4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import combowork.common.Config;
import combowork.common.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utilities for parsing test cases file
 *
 * @author marouenj
 */
public class TestCaseParser {

    /**
     * Validate a ComboWork test suite
     *
     * @param testCases Test cases to be validated
     * @return Validation result
     */
    public static boolean isValid(JsonNode testCases) {
        if (testCases == null) {
            return false;
        }

        if (testCases.getNodeType() != JsonNodeType.ARRAY) { // root should start with an array of objects
            return false;
        }

        if (testCases.size() == 0) {
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

            JsonNode vals = testcase.get(Config.KEY_TEST_CASE_VALS);
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

        // TODO validate complex vals

        return true;
    }

    /**
     * Infer the pattern of the test cases from the first entry
     * All subsequent entries will be checked against the pattern in {@link TestCaseParser#isValid(JsonNode)}
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

    /**
     * Validate a complex value (array)
     *
     * @param testCases Root for test cases
     * @param valIdxes  Set of index to the complex vals
     * @return True if all complex vals are valid
     */
    private static boolean isValidArr(JsonNodeType testCases, Set<Integer> valIdxes) {
        return true;
    }

    /**
     * Infer the pattern of the complex val from the first entry
     * All subsequent entries will be checked against the pattern in {@link TestCaseParser#isValidArr(JsonNodeType, Set)}
     *
     * @param first First entry of the complex val to infer the pattern from
     * @return Pattern
     */
    private static ArrayList<JsonNodeType> patternArr(JsonNode first) {
        return null;
    }

    /**
     * Map test cases to a {@link Object}[][]
     *
     * @param testCases test cases
     * @return view of the test cases
     */
    public static Object[][] convertToObjectMatrix(JsonNode testCases) {
        int nbreTestCases = testCases.size();
        int nbreVals = testCases.get(0).get(Config.KEY_TEST_CASE_VALS).size();

        Object[][] view = new Object[nbreTestCases][nbreVals];

        int i = -1;
        for (JsonNode testCase : testCases) {
            i++;
            JsonNode vals = testCase.get(Config.KEY_TEST_CASE_VALS);
            int j = -1;
            for (JsonNode val : vals) {
                j++;
                view[i][j] = JsonUtil.jsonToJavaType(val);
            }
        }

        return view;
    }
}
