package combowork.expect4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import combowork.common.Config;
import combowork.common.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for parsing rules and expected results
 *
 * @author marouenj
 */
public class RuleParser {

    /**
     * Validate rule file
     *
     * @param root rule file in a {@link JsonNode} format
     * @return Whether is valid or not
     */
    public static boolean isValid(JsonNode root) {
        if (root == null || root.getNodeType() != JsonNodeType.OBJECT) {
            return false;
        }

        JsonNode defaultKey = root.get("default");
        if (defaultKey == null || defaultKey.getNodeType() != JsonNodeType.ARRAY || defaultKey.size() == 0) {
            return false;
        }

        List<JsonNodeType> pattern;
        try {
            pattern = pattern(defaultKey);
        } catch (RuntimeException e) {
            return false;
        }

        JsonNode overrideKey = root.get("override");
        if (overrideKey == null) {
            return true;
        }
        if (overrideKey.getNodeType() != JsonNodeType.ARRAY || overrideKey.size() == 0) {
            return false;
        }

        for (JsonNode rule : overrideKey) {
            if (rule.getNodeType() != JsonNodeType.OBJECT) {
                return false;
            }

            JsonNode ruleKey = rule.get("rule");
            if (ruleKey == null || ruleKey.getNodeType() != JsonNodeType.ARRAY || ruleKey.size() == 0) {
                return false;
            }

            JsonNode actionKey = rule.get("action");
            if (actionKey == null || actionKey.getNodeType() != JsonNodeType.ARRAY || actionKey.size() != pattern.size()) {
                return false;
            }

            int i = -1;
            for (JsonNode action : actionKey) {
                i++;

                if (pattern.get(i) == JsonNodeType.NULL) { // hasn't encountered a deterministic value yet
                    pattern.set(i, action.getNodeType());
                    continue;
                }

                if (action.getNodeType() != pattern.get(i)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Infer the pattern of the expected vals from the default instance
     * Subsequent expected vals should be conform to the pattern, otherwise the structure is considered invalid
     *
     * @return Pattern of the expected vals
     */
    private static ArrayList<JsonNodeType> pattern(JsonNode whatToExpect) {
        ArrayList<JsonNodeType> pattern = new ArrayList<>(whatToExpect.size());

        for (JsonNode val : whatToExpect) {
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

        JsonNode rules = expected.get("override");
        if (rules == null) {
            return match;
        }

        for (JsonNode rule : rules) {
            JsonNode pattern = rule.get("rule");
            JsonNode action = rule.get("action");

            JsonNode matchCandidate = null;
            int closenessCandidate = 0;

            int i = -1;
            for (JsonNode val : pattern) {
                i++;
                if (val.getNodeType() == JsonNodeType.OBJECT) {
                    continue;
                }
                if (val.getNodeType() == testCase.get(i).getNodeType() && val.equals(testCase.get(i))) {
                    matchCandidate = action;
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
