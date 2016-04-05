package combowork.load4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

/**
 * Test for {@link TestCaseParser}
 *
 * @author marouenj
 */
public class TestCaseParserTest {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    @BeforeClass
    public void init() {
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
     * Test {@link TestCaseParser#isValid(JsonNode)}
     *
     * @param json     Json input structure
     * @param expected Expected outcome about the validation
     */
    @Test(dataProvider = "isValid")
    public void isValid(JsonNode json, Boolean expected) {
        Assert.assertEquals(TestCaseParser.isValid(json), (boolean) expected);
    }

    @DataProvider(name = "convertToObjectMatrix")
    public Object[][] convertToObjectMatrix() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource("convertToObjectMatrix.json");
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
            Object[][] expected = new Object[testCase.get("out").size()][];
            int j = -1;
            for (JsonNode expect : testCase.get("out")) {
                j++;
                expected[j] = new Object[expect.size()];
                int k = -1;
                for (JsonNode val : expect) {
                    k++;
                    expected[j][k] = jsonToJavaType(val);
                }
            }

            data[i] = new Object[]{testCase.get("in"), expected};
        }

        return data;
    }

    /**
     * Test {@link TestCaseParser#convertToObjectMatrix(JsonNode)}
     *
     * @param json     Json input structure
     * @param expected Expected outcome about the validation
     */
    @Test(dataProvider = "convertToObjectMatrix")
    public void convertToObjectMatrix(JsonNode json, Object[][] expected) {
        Object[][] actual = TestCaseParser.convertToObjectMatrix(json);

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

    /**
     * Utility used to map Json types to Java types
     *
     * @param node Json-typed value
     * @return Java-typed value
     */
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
