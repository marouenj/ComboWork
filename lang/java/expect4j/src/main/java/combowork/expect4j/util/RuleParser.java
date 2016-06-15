package combowork.expect4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import combowork.common.Config;
import combowork.common.JsonUtil;

/**
 * Utilities for parsing rules and expected results
 *
 * @author marouenj
 */
public class RuleParser {

    /**
     * Combine test cases and expected values in a {@link Object}[][]
     *
     * @return A view of the test cases and their respective expected vals
     */
    public static Object[][] convertToObjectMatrix(JsonNode testCases, JsonNode expected) {
        int nbreTestCases = testCases.size();
        int nbreVals = testCases.get(0).get(Config.KEY_TEST_CASE_VALS).size();

        int nbreExpected = expected.get("default").size();


        Object[][] view = new Object[nbreTestCases][nbreVals + nbreExpected];

        int i = -1;
        for (JsonNode testCase : testCases) {
            i++;
            JsonNode vals = testCase.get("Case");

            int j = -1;
            for (JsonNode val : vals) {
                j++;
                view[i][j] = JsonUtil.jsonToJavaType(val);
            }

            JsonNode rule = lookUpRuleForTestcase(vals, expected);
            for (JsonNode val : rule) {
                j++;
                view[i][j] = JsonUtil.jsonToJavaType(val);
            }
        }

        return view;
    }

    /**
     * Given a testcase, lookup the most matching rule and return the list of expected vals
     *
     * @param expected expect4j config file
     * @param testCase Array of vals representing a testcase
     * @return Array of vals containing the list of expected vals
     */
    private static JsonNode lookUpRuleForTestcase(JsonNode testCase, JsonNode expected) {
        JsonNode match = expected.get("default");
        int closeness = 0;

        JsonNode overrideVal = expected.get("override");
        if (overrideVal == null) {
            return match;
        }

        for (JsonNode rule : overrideVal) {
            JsonNode ruleVal = rule.get("rule");
            JsonNode actionVal = rule.get("action");

            JsonNode matchCandidate = null;
            int closenessCandidate = 0;

            int i = -1;
            for (JsonNode val : ruleVal) {
                i++;
                if (val.getNodeType() == JsonNodeType.OBJECT) {
                    if (val.size() == 0) {
                        continue;
                    } else {
                        Boolean found = false;
                        JsonNode inVal = val.get("in");
                        for (JsonNode val2 : inVal) {
                            if (val2.getNodeType() == testCase.get(i).getNodeType() && val2.equals(testCase.get(i))) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            matchCandidate = actionVal;
                            closenessCandidate++;
                        } else {
                            closenessCandidate = 0;
                            break;
                        }
                    }
                } else if (val.getNodeType() == testCase.get(i).getNodeType() && val.equals(testCase.get(i))) {
                    matchCandidate = actionVal;
                    closenessCandidate++;
                } else {
                    closenessCandidate = 0;
                    break;
                }
            }

            if (closenessCandidate > closeness) {
                match = matchCandidate;
                closeness = closenessCandidate;
            }
        }

        return match;
    }
}
