package combowork.expect4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import combowork.common.JsonUtil;
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

    private Method lookUpRuleForTestCase;

    private final static ObjectMapper MAPPER = new ObjectMapper();

    @BeforeClass
    public void init() throws NoSuchMethodException {
        this.lookUpRuleForTestCase = RuleParser.class.getDeclaredMethod("lookUpRuleForTestcase", JsonNode.class, JsonNode.class);
        this.lookUpRuleForTestCase.setAccessible(true);
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
                    expected[j][k] = JsonUtil.jsonToJavaType(val);
                }
            }

            data[i] = new Object[]{testCase.get("load4j"), testCase.get("expect4j"), expected};
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

    @DataProvider(name = "lookUpRuleForTestcase")
    public Object[][] lookUpRuleForTestcase() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource("lookUpRuleForTestcase.json");
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
            data[i] = new Object[]{testCase.get("load4j"), testCase.get("expect4j"), testCase.get("out").asText()};
        }

        return data;
    }

    @Test(dataProvider = "lookUpRuleForTestcase")
    public void lookUpRuleForTestcase(JsonNode testcase, JsonNode expect, String expected) throws InvocationTargetException, IllegalAccessException {
        JsonNode actual = (JsonNode) (this.lookUpRuleForTestCase.invoke(null, testcase, expect));
        Assert.assertEquals(actual.toString(), expected);
    }
}
