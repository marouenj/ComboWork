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

/**
 * Utility for {@link JsonNode}
 *
 * @author marouenj
 */
public class JsonUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

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
        if (root == null) {
            return false;
        }

        if (root.getNodeType() != JsonNodeType.ARRAY) { // root should start with an array of objects
            return false;
        }

        return true;
    }
}
