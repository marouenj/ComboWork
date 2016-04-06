package combowork.expect4j;

import com.fasterxml.jackson.databind.JsonNode;
import combowork.expect4j.util.RuleParser;

import java.io.File;

/**
 * Load ComboWork test cases and rule-based expectations
 * <p>
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
 * <p>
 * Rule-based expectations are of the form
 * {
 * "default" : [
 * {
 * // expectation 1
 * },
 * {
 * // expectation 2
 * },
 * ...
 * {
 * // expectation n
 * }
 * ],
 * "override" : [
 * {
 * "rule" : [ {}, {}, {}, "val", null ],
 * "action" : [
 * {
 * // expectation 1
 * },
 * {
 * // expectation 2
 * },
 * ...
 * {
 * // expectation n
 * }
 * ]
 * },
 * {
 * "rule" : [ {}, 1, {}, {}, {} ],
 * "action" : [
 * {
 * // expectation 1
 * },
 * {
 * // expectation 2
 * },
 * ...
 * {
 * // expectation n
 * }
 * ]
 * }
 * ]
 * }
 *
 * @author marouenj
 */
public final class TestCaseLoader {

    /**
     * Load the test cases and the expectation rules.
     * Map each test case with an expectation result having the closest match
     * Return the lot as a TestNG data provider-compatible test data
     *
     * @param load4j   Test cases
     * @param expect4j Rule-based expectations
     * @return Test data as a TestNG data provider
     */
    public static Object[][] testNgAdapter(File load4j, File expect4j) {
        JsonNode testCases = combowork.load4j.TestCaseAdapter.asIs(load4j);
        try {
            JsonNode expected = RuleParser.fromFile(expect4j);
            if (!RuleParser.isValid(testCases)) {
                throw new RuntimeException();
            }

            return RuleParser.convertToObjectMatrix(testCases, expected);
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
