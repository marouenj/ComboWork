package combowork.validate_rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Validate {

    private final static Logger LOGGER = LoggerFactory.getLogger(Validate.class);


    /**
     * Validate rule file
     *
     * @param root rule file in a {@link JsonNode} format
     * @return Whether is valid or not
     */
    public static boolean isValid(JsonNode root) {
        if (root == null) {
            LOGGER.warn("Invalid: null");
            return false;
        }

        if (root.getNodeType() != JsonNodeType.OBJECT) {
            LOGGER.warn("Invalid: root is not object");
            return false;
        }

        JsonNode defaultKey = root.get("default");
        if (defaultKey == null) {
            LOGGER.warn("Invalid: default key is null");
            return false;
        }

        if (defaultKey.getNodeType() != JsonNodeType.ARRAY) {
            LOGGER.warn("Invalid: default key is not array");
            return false;
        }

        if (defaultKey.size() == 0) {
            LOGGER.warn("Invalid: default key is empty");
            return false;
        }

        List<JsonNodeType> pattern;
        try {
            pattern = pattern(defaultKey);
        } catch (RuntimeException e) {
            LOGGER.warn("Invalid: unable to infer pattern");
            return false;
        }

        JsonNode overrideKey = root.get("override");
        if (overrideKey == null) {
            LOGGER.warn("Invalid: override key is null");
            return true;
        }

        if (overrideKey.getNodeType() != JsonNodeType.ARRAY) {
            LOGGER.warn("Invalid: override key is not array");
            return false;
        }

        if (overrideKey.size() == 0) {
            LOGGER.warn("Invalid: override key is empty");
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
