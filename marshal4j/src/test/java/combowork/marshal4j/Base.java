package combowork.marshal4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Base {
    private final static ObjectMapper MAPPER = new ObjectMapper();

    protected JsonNode fromString(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
