package combowork.load4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import combowork.common.Config;
import combowork.common.JsonUtil;

/**
 * Utilities for parsing test cases file
 *
 * @author marouenj
 */
public class TestCaseParser {

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
