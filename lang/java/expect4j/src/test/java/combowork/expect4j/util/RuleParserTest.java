package combowork.expect4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Test for {@link RuleParser}
 *
 * @author marouenj
 */
public class RuleParserTest {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private static Method fromText;
    private static Method fromTextLoad4j;

    @BeforeClass
    public void init() throws NoSuchMethodException {
        fromText = RuleParser.class.getDeclaredMethod("fromText", String.class, String.class);
        fromText.setAccessible(true);

        fromTextLoad4j = combowork.load4j.util.TestCaseParser.class.getDeclaredMethod("fromText", String.class, String.class);
        fromTextLoad4j.setAccessible(true);
    }

    @DataProvider(name = "isValid")
    public Object[][] isValid() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource("isValid.json");
        if (resource == null) {
            throw new Exception();
        }

        JsonNode testCases = MAPPER.readTree(
                IOUtils.toString(
                        new FileReader(
                                new File(resource.getFile()))));

        Object[][] data = new Object[testCases.size()][];

        int i = -1;
        for (JsonNode testCase : testCases) {
            i++;
            data[i] = new Object[]{testCase.get("in"), testCase.get("out").booleanValue()};
        }

        return data;
    }

    /**
     * Test {@link RuleParser#isValid(JsonNode)}
     *
     * @param json     Json input structure
     * @param expected Expected outcome about the validation
     */
    @Test(dataProvider = "isValid")
    public void isValid(JsonNode json, Boolean expected) {
        Assert.assertEquals(RuleParser.isValid(json), (boolean) expected);
    }

    @DataProvider(name = "lookUpRuleForTestcase")
    public Object[][] lookUpRuleForTestcase() throws InvocationTargetException, IllegalAccessException {
        String[] expect = new String[]{
                "{\"default\":[1]}",
                "{\"default\":[1],\"override\":[{\"rule\":[{}, {}, {}, 2],\"action\":[2]}]}",
                "{\"default\":[1],\"override\":[{\"rule\":[{}, {}, {}, 2],\"action\":[2]},{\"rule\":[{}, {}, {}, null],\"action\":[3]}]}",
                "{\"default\":[1],\"override\":[{\"rule\":[{}, {}, {}, 2],\"action\":[2]},{\"rule\":[{}, {}, {}, null],\"action\":[3]},{\"rule\":[{}, \"str\", {}, null],\"action\":[4]}]}",
        };

        String[] testcase = new String[]{
                "[1, \"str\", true, null]",
                "[1, \"str\", true, null]",
                "[1, \"str\", true, null]",
                "[1, \"str\", true, null]",
        };

        String[] expected = new String[]{
                "[1]",
                "[1]",
                "[3]",
                "[4]",
        };

        Object[][] data = new Object[expect.length][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Object[]{
                    fromText.invoke(null, expect[i], null),
                    fromText.invoke(null, testcase[i], null),
                    expected[i],
            };
        }

        return data;
    }

    @Test(dataProvider = "lookUpRuleForTestcase")
    public void lookUpRuleForTestcase(JsonNode expect, JsonNode testcase, String expected) {
        Assert.assertEquals(RuleParser.lookUpRuleForTestcase(testcase, expect).toString(), expected);
    }

    @DataProvider(name = "convertToObjectMatrix")
    public Object[][] convertToObjectMatrix() throws InvocationTargetException, IllegalAccessException {
        String[] testCases = new String[]{
                "[{\"Case\":[\"string\"]}]",
                "[{\"Case\":[\"string\", 5]}]",
                "[{\"Case\":[\"string\"]}, {\"Case\":[\"string2\"]}]]",
                "[{\"Case\":[\"string\", 5]}, {\"Case\":[\"string2\", 7]}]]",
        };

        String[] expect = new String[]{
                "{\"default\":[1]}",
                "{\"default\":[1]}",
                "{\"default\":[1],\"override\":[{\"rule\":[\"string2\"],\"action\":[2]}]}",
                "{\"default\":[1],\"override\":[{\"rule\":[\"string2\", 7],\"action\":[3]}]}",
        };


        Object[][][] expected = new Object[][][]{
                new Object[][]{
                        {"string", 1},
                },
                new Object[][]{
                        {"string", 5, 1},
                },
                new Object[][]{
                        {"string", 1},
                        {"string2", 2},
                },
                new Object[][]{
                        {"string", 5, 1},
                        {"string2", 7, 3},
                },
        };

        Object[][] data = new Object[testCases.length][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Object[]{fromTextLoad4j.invoke(null, testCases[i], null), fromText.invoke(null, expect[i], null), expected[i]};
        }

        return data;
    }

    @Test(dataProvider = "convertToObjectMatrix")
    public void convertToObjectMatrix(JsonNode testCases, JsonNode expect, Object[][] expected) {
        Assert.assertNotNull(testCases);
        Object[][] actual = RuleParser.convertToObjectMatrix(testCases, expect);

        Assert.assertEquals(actual.length, expected.length);

        int i = -1;
        for (Object[] entry : actual) {
            i++;
            Assert.assertEquals(entry.length, expected[i].length);
        }

        i = -1;
        for (Object[] vals : actual) {
            i++;

            int j = -1;
            for (Object val : vals) {
                j++;
                Assert.assertEquals(val, expected[i][j]);
            }
        }
    }

}
