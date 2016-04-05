package combowork.expect4j.util;

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
 */
public class JsonUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private final static ObjectMapper MAPPER = new ObjectMapper();

    // TODO make this public in load4j
    private final static String VALS_KEY = "Case";

    /**
     * List of log messages intrinsic to {@link JsonUtil}
     */
    private enum LogMessages {
        UNABLE_TO_OPEN_FILE("Unable to open file, %s"),
        UNABLE_TO_CONVERT_STREAM_TO_STRING("Unable to convert Stream to String in, %s"),
        UNABLE_TO_PARSE_JSON("Unable to parse json tree. Json file is, %s");

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

    public static boolean isValid(JsonNode root) {
        if (root == null || root.getNodeType() != JsonNodeType.OBJECT) {
            return false;
        }

        JsonNode defaultVal = root.get("default");
        if (defaultVal == null || defaultVal.getNodeType() != JsonNodeType.ARRAY || defaultVal.size() == 0) {
            return false;
        }

        List<JsonNodeType> pattern;
        try {
            pattern = pattern(defaultVal);
        } catch (RuntimeException e) {
            return false;
        }

        JsonNode rules = root.get("override");
        if (rules == null) {
            return true;
        }
        if (rules.getNodeType() != JsonNodeType.ARRAY || rules.size() == 0) {
            return false;
        }

        for (JsonNode rule : rules) {
            if (rule.getNodeType() != JsonNodeType.OBJECT) {
                return false;
            }

            JsonNode ruleVal = rule.get("rule");
            if (ruleVal == null || ruleVal.getNodeType() != JsonNodeType.ARRAY || ruleVal.size() == 0) {
                return false;
            }

            JsonNode actions = rule.get("action");
            if (actions == null || actions.getNodeType() != JsonNodeType.ARRAY || actions.size() == 0 || actions.size() != pattern.size()) {
                return false;
            }

            int i = -1;
            for (JsonNode action : actions) {
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

    private static ArrayList<JsonNodeType> pattern(JsonNode vals) {
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
     * Given a testcase, lookup the most matching rule and return the list of expected vals
     *
     * @param expected expect4j config file
     * @param testCase Array of vals representing a testcase
     * @return Array of vals containing the list of expected vals
     */
    public static JsonNode lookUpRuleForTestcase(JsonNode testCase, JsonNode expected) {
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

    public static Object[][] convertToObjectMatrix(JsonNode testCases, JsonNode expected) {
        int nbreTestCases = testCases.size();
        int nbreVals = testCases.get(0).get(VALS_KEY).size();

        int nbreExpected = expected.get("default").size();


        Object[][] view = new Object[nbreTestCases][nbreVals + nbreExpected];

        int i = -1;
        for (JsonNode testCase : testCases) {
            i++;
            JsonNode vals = testCase.get("Case");

            int j = -1;
            for (JsonNode val : vals) {
                j++;
                view[i][j] = jsonToJavaType(val);
            }

            JsonNode rule = lookUpRuleForTestcase(vals, expected);
            for (JsonNode val : rule) {
                j++;
                view[i][j] = jsonToJavaType(val);
            }
        }

        return view;
    }

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
