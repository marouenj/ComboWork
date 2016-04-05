package combowork.load4j;

import com.fasterxml.jackson.databind.JsonNode;
import combowork.load4j.util.TestCaseParser;

import java.io.File;

/**
 * Load ComboWork test cases
 * Test cases are of the form
 * [
 * {
 * "Num : 1,
 * "Case" : ["John", "Doe", 18],
 * },
 * {
 * "Num : 2,
 * "Case" : [..., ..., ...],
 * },
 * ...
 * {
 * "Num : ...,
 * "Case" : [..., ..., ...],
 * },
 * ]
 *
 * @author marouenj
 */
public final class TestCaseAdapter {

    /**
     * Load the test cases then return them as {@link JsonNode}
     * This output is used when piping to expect4j
     *
     * @param file contains the test cases
     * @return test cases as {@link JsonNode}
     */
    public static JsonNode asIs(File file) {
        try {
            JsonNode testCases = TestCaseParser.fromFile(file);
            if (!TestCaseParser.isValid(testCases)) {
                throw new RuntimeException();
            }

            return testCases;
        } catch (Exception e) {
            handleExceptions(e);
        }

        // unreachable
        // this is merely to satisfy the compiler
        return null;
    }

    /**
     * Load the test cases then return them as {@code Object[][]}
     * This output is used when piping to TestNG
     *
     * @param file contains the test cases
     * @return test cases in {@code Object[][]}
     */
    public static Object[][] testNgAdapter(File file) {
        try {
            JsonNode testCases = TestCaseParser.fromFile(file);
            if (!TestCaseParser.isValid(testCases)) {
                throw new RuntimeException();
            }

            return TestCaseParser.convertToObjectMatrix(testCases);
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
