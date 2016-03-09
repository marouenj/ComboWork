package combowork.expect4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Test for {@link JsonUtil}
 *
 * @author marouenj
 */
public class JsonUtilTest {

    private static Method fromText;

    @BeforeClass
    public void init() throws NoSuchMethodException {
        fromText = JsonUtil.class.getDeclaredMethod("fromText", String.class, String.class);
        fromText.setAccessible(true);
    }

    @DataProvider(name = "isValid")
    public Object[][] isValid() throws InvocationTargetException, IllegalAccessException {
        String[] json = new String[]{
                "[]",
                "{}",
                "{\"default\":[]}",
                "{\"default\":[1],\"override\":[]}",
                "{\"default\":[1],\"override\":{}}",
                "{\"default\":[1],\"override\":[{},{},1]}",
                "{\"default\":[1],\"override\":[{}]}",
                "{\"default\":[1],\"override\":[{},{}]}",
                "{\"default\":[1],\"override\":[{\"rule\":[{}],\"action\":[]}]}",
                "{\"default\":[1],\"override\":[{\"rule\":[{}],\"action\":[\"1\"]}]}",
                "{\"default\":[1],\"override\":[{\"rule\":[{}],\"action\":[1,2]}]}",
                "{\"default\":[null],\"override\":[{\"rule\":[{}],\"action\":[1]},{\"rule\":[{}],\"action\":[\"2\"]}]}",

                "{\"default\":[1]}",
                "{\"default\":[1],\"override\":[{\"rule\":[{}],\"action\":[1]}]}",
                "{\"default\":[1],\"override\":[{\"rule\":[{}],\"action\":[1]},{\"rule\":[{}],\"action\":[2]}]}",
                "{\"default\":[null],\"override\":[{\"rule\":[{}],\"action\":[1]}]}",
                "{\"default\":[null],\"override\":[{\"rule\":[{}],\"action\":[1]},{\"rule\":[{}],\"action\":[2]}]}",
        };

        Boolean[] expected = new Boolean[]{
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,

                true,
                true,
                true,
                true,
                true,
        };

        Object[][] data = new Object[json.length][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Object[]{fromText.invoke(null, json[i], null), expected[i]};
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
