package combowork.marshal4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

public class JsonUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final static HashSet<String> KEY_SET;
    private final static HashSet<String> REQUIRED_KEYS_LEVEL_1;

    static {
        KEY_SET = new HashSet<String>() {{
            add("prefix");
            add("suffix");
            add("name");
            add("type");
            add("setter");
            add("vals");
        }};

        REQUIRED_KEYS_LEVEL_1 = new HashSet<String>() {{
            add("prefix");
            add("suffix");
            add("vals");
        }};
    }

    public static JsonNode read(String path) {
        String text;
        try {
            text = IOUtils.toString(new FileReader(path));
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                LOGGER.error(LogMessages.UNABLE_TO_READ_FILE.getText(path));
                throw new RuntimeException(LogMessages.UNABLE_TO_READ_FILE.getText(path));
            } else {
                LOGGER.error(LogMessages.UNABLE_TO_CONVERT_STREAM_TO_STRING.getText(path));
                throw new RuntimeException(LogMessages.UNABLE_TO_CONVERT_STREAM_TO_STRING.getText(path));
            }
        }

        // use MAPPER to read the string json into a json node
        JsonNode tree;
        try {
            tree = MAPPER.readTree(text);
        } catch (IOException e) {
            LOGGER.error(LogMessages.UNABLE_TO_READ_TREE.getText(path));
            throw new RuntimeException(LogMessages.UNABLE_TO_READ_TREE.getText(path));
        }

        return tree;
    }

    public static boolean varsIsValid(JsonNode vars) {
        if (vars == null) {
            return false;
        }

        if (vars.getNodeType() != JsonNodeType.ARRAY) { // root should start with an array of objects
            return false;
        }

        for (JsonNode var : vars) {
            if (var.getNodeType() != JsonNodeType.OBJECT) { // it should be an object encompassing, among other things, the var of the vals
                return false;
            }

            // check all required keys are passed
            // check no other key is passed
            Iterator<String> keys = var.fieldNames();
            int n = 0;
            for (; keys.hasNext(); ) {
                if (!REQUIRED_KEYS_LEVEL_1.contains(keys.next())) {
                    return false;
                }
                n++;
            }
            if (n < REQUIRED_KEYS_LEVEL_1.size()) {
                return false;
            }

            if (!varsIsValid0(var.get("vals"))) {
                return false;
            }
        }

        return true;
    }

    private static boolean varsIsValid0(JsonNode vals) {
        if (vals.size() == 0) {
            return false;
        }

        final JsonNodeType type = vals.get(0).getNodeType();

        for (JsonNode val : vals) {
            if (val.getNodeType() != type) {
                return false;
            }

            if (val.getNodeType() != JsonNodeType.OBJECT) {
                continue;
            }

            // check all required keys are passed
            // check no other key is passed
            HashSet<String> keySet = KEY_SET.stream().collect(Collectors.toCollection(HashSet::new));
            Iterator<String> keys = val.fieldNames();
            for (; keys.hasNext(); ) {
                String key = keys.next();
                if (!keySet.contains(key)) {
                    return false;
                }
                keySet.remove(key);
            }

            if ((keySet.size() > 3) ||
                    (keySet.size() == 1 && !keySet.contains("type")) || // for complex types, type shouldn't be used
                    (keySet.size() == 2 && (!keySet.contains("prefix") || !keySet.contains("suffix"))) || // for primitive types, these shouldn't be used
                    (keySet.size() == 3 && (!keySet.contains("prefix") || !keySet.contains("suffix") || !keySet.contains("type")))) { // for primitive types, type defaults to String
                return false;
            }

            if (!varsIsValid0(val.get("vals"))) {
                return false;
            }
        }

        return true;
    }

    public static boolean valsIsValid(JsonNode vals) {
        if (vals == null) {
            return false;
        }

        if (vals.getNodeType() != JsonNodeType.ARRAY) {
            return false;
        }

        int i = 0;
        for (JsonNode val : vals) {
            if (val.getNodeType() != JsonNodeType.OBJECT) {
                return false;
            }

            if (val.size() != 2) {
                return false;
            }

            if (val.get("Num") == null || val.get("Case") == null) {
                return false;
            }

            if (val.get("Num").asInt() != ++i) {
                return false;
            }
        }

        return true;
    }

    public static String asText(JsonNode node, String key, String defaultVal) {
        JsonNode val = node.get(key);

        if (val == null) {
            return defaultVal;
        }

        return val.asText();
    }

    /**
     * Log messages for this class logger
     */
    private enum LogMessages {
        UNABLE_TO_READ_FILE("Unable to read from file, "),
        UNABLE_TO_CONVERT_STREAM_TO_STRING("Unable to convert Reader to String for, "),
        UNABLE_TO_READ_TREE("Unable to load tree from file, ");

        private final String msg;

        LogMessages(String msg) {
            this.msg = msg;
        }

        public String getText() {
            return msg;
        }

        public String getText(String path) {
            return msg + path;
        }
    }
}
