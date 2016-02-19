package combowork.load4j;

import com.fasterxml.jackson.databind.JsonNode;
import combowork.load4j.util.JsonUtil;

public final class TestCaseLoader {

    public static Object[][] testNgAdapter(String path) {
        try {
            JsonNode testCases = JsonUtil.fromFile(path);
            if (!JsonUtil.isValid(testCases)) {
                throw new RuntimeException();
            }

            return JsonUtil.convertToObjectMatrix(testCases);
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
