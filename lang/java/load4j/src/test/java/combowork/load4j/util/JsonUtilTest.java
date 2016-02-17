package combowork.load4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test for {@link JsonUtil}
 *
 * @author marouenj
 */
public class JsonUtilTest {

    @DataProvider(name = "isValid")
    public Object[][] isValid() {
        String[] json = new String[]{
                "{}",
                "[]",
                "[{}]",
                "[{}, {}]",

                "[{\"Case\":[\"string\"]}]",
                "[{\"Case\":[\"string\", 5]}]",
                "[{\"Case\":[\"string\"]}, {\"Case\":[\"string2\"]}]]",
                "[{\"Case\":[\"string\", 5]}, {\"Case\":[\"string2\", 7]}]]",

                "[{\"Case\":[\"string\", 5]}, {\"Case\":[\"string2\"]}]]",
                "[{\"Case\":[\"string\", 5]}, {\"Case\":[\"string2\", true]}]]",
        };

        Boolean[] expected = new Boolean[]{
                false,
                false,
                false,
                false,

                true,
                true,
                true,
                true,

                false,
                false,
        };

        Object[][] data = new Object[json.length][];

        for (int i = 0; i < data.length; i++) {
            data[i] = new Object[]{JsonUtil.fromText(json[i], null), expected[i]};
        }

        return data;
    }

    /**
     * Test {@link JsonUtil#isValid(JsonNode)}
     *
     * @param json     Json input structure
     * @param expected Expected outcome about the validation
     */
    @Test(dataProvider = "isValid")
    public void isValid(JsonNode json, Boolean expected) {
        Assert.assertEquals(JsonUtil.isValid(json), (boolean) expected);
    }
}
