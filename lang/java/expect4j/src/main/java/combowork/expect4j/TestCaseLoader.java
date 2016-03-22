package combowork.expect4j;

import com.fasterxml.jackson.databind.JsonNode;
import combowork.expect4j.util.JsonUtil;

import java.io.File;

public final class TestCaseLoader {

    public static Object[][] testNgAdapter(File load4j, File expect4j) {
        JsonNode testCases = combowork.load4j.TestCaseLoader.asIs(load4j);
        try {
            JsonNode expected = JsonUtil.fromFile(expect4j);
            if (!JsonUtil.isValid(testCases)) {
                throw new RuntimeException();
            }

            return JsonUtil.convertToObjectMatrix(testCases, expected);
        } catch (Exception e) {
            handleExceptions(e);
        }

        // unreachable
        // this is merely to satisfy the compiler
        return null;
    }

    private static void handleExceptions(Exception e) {
        System.exit(0);
    }
}
