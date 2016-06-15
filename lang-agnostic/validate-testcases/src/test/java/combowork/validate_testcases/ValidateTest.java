package combowork.validate_testcases;

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
 * Test {@link Validate}
 *
 * @author marouenj
 */
public class ValidateTest {

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
     * Test {@link Validate#isValid(JsonNode)}
     *
     * @param json     Json input structure
     * @param expected Expected outcome about the validation
     */
    @Test(dataProvider = "isValid")
    public void isValid(JsonNode json, Boolean expected) {
        Assert.assertEquals(Validate.isValid(json), (boolean) expected);
    }
}
