package combowork.load4j.util;

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

    @DataProvider(name = "sizes")
    public Object[][] sizes() throws InvocationTargetException, IllegalAccessException {
        String[] json = new String[]{
                "[{\"Case\":[\"string\"]}]",
                "[{\"Case\":[\"string\", 5]}]",
                "[{\"Case\":[\"string\"]}, {\"Case\":[\"string2\"]}]]",
                "[{\"Case\":[\"string\", 5]}, {\"Case\":[\"string2\", 7]}]]",
        };

        Integer[][] expected = new Integer[][]{
                new Integer[]{1, 1},
                new Integer[]{1, 2},
                new Integer[]{2, 1},
                new Integer[]{2, 2},
        };

        Object[][] data = new Object[json.length][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Object[]{fromText.invoke(null, json[i], null), expected[i]};
        }

        return data;
    }

    /**
     * Test {@link JsonUtil#sizes(JsonNode)}
     *
     * @param json     Json input structure
     * @param expected Expected outcome about the validation
     */
    @Test(dataProvider = "sizes")
    public void sizes(JsonNode json, Integer[] expected) {
        int[] dim = JsonUtil.sizes(json);
        Assert.assertEquals(dim[0], (int) expected[0]);
        Assert.assertEquals(dim[1], (int) expected[1]);
    }

    @DataProvider(name = "convertToObjectMatrix")
    public Object[][] convertToObjectMatrix() throws InvocationTargetException, IllegalAccessException {
        String[] json = new String[]{
                "[{\"Case\":[\"string\"]}]",
                "[{\"Case\":[\"string\", 5]}]",
                "[{\"Case\":[\"string\"]}, {\"Case\":[\"string2\"]}]]",
                "[{\"Case\":[\"string\", 5]}, {\"Case\":[\"string2\", 7]}]]",
                "[{\"Case\":[true]}]",
                "[{\"Case\":[null]}]",
        };

        Object[][][] expected = new Object[][][]{
                new Object[][]{
                        {"string"},
                },
                new Object[][]{
                        {"string", 5},
                },
                new Object[][]{
                        {"string"},
                        {"string2"},
                },
                new Object[][]{
                        {"string", 5},
                        {"string2", 7},
                },
                new Object[][]{
                        {true},
                },
                new Object[][]{
                        {null},
                },
        };

        Object[][] data = new Object[json.length][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Object[]{fromText.invoke(null, json[i], null), expected[i]};
        }

        return data;
    }

    /**
     * Test {@link JsonUtil#convertToObjectMatrix(JsonNode)}
     *
     * @param json     Json input structure
     * @param expected Expected outcome about the validation
     */
    @Test(dataProvider = "convertToObjectMatrix")
    public void convertToObjectMatrix(JsonNode json, Object[][] expected) {
        Object[][] actual = JsonUtil.convertToObjectMatrix(json);

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
