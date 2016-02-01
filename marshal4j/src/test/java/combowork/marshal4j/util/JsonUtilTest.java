package combowork.marshal4j.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

public class JsonUtilTest {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private JsonNode fromString(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DataProvider(name = "validateVars")
    public Object[][] validateVars() {
        return new Object[][]{
                {"[]", true}, // root should be an array
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[1]}]", true}, // TODO this should fail: vals should be array of objects (not primitives) when defining prefix and suffix
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[1, 2]}]", true}, // TODO ditto
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[{\"name\":\"\", \"setter\":\"\", \"vals\":[1, 2]}]}]", true},
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[{\"name\":\"\", \"setter\":\"\", \"vals\":[1, 2]}, {\"name\":\"\", \"setter\":\"\", \"vals\":[1, 2]}]}]", true},
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[{\"name\":\"\", \"type\":\"\", \"setter\":\"\", \"vals\":[1, 2]}]}]", true},
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[{\"name\":\"\", \"type\":\"\", \"setter\":\"\", \"vals\":[1, 2]}, {\"name\":\"\", \"type\":\"\", \"setter\":\"\", \"vals\":[1, 2]}]}]", true},
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[{\"prefix\":\"\", \"suffix\":\"\", \"name\":\"\", \"setter\":\"\", \"vals\":[1]}]}]", true}, // TODO this should fail: vals should be array of objects (not primitives) when defining prefix and suffix
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[{\"prefix\":\"\", \"suffix\":\"\", \"name\":\"\", \"setter\":\"\", \"vals\":[1, 2]}]}]", true}, // TODO ditto
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[{\"prefix\":\"\", \"suffix\":\"\", \"name\":\"\", \"setter\":\"\", \"vals\":[{\"name\":\"\", \"setter\":\"\", \"vals\":[1, 2]}]}]}]", true},

                {"{}", false}, // root should be an array
                {"[1]", false}, // array elements should be objects
                {"[{}]", false}, // object should contain required keys
                {"[{\"prefix\":\"\", \"vals\":[1]}]}]", false}, // object should contain required keys
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[]}]", false}, // vals should not be empty
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[1, 2, \"3\"]}]", false}, // vals should have same type
                {"[{\"prefix\":\"\", \"suffix\":\"\", \"vals\":[1, 2, {}]}]", false}, // vals should have same type
        };
    }

    @Test(dataProvider = "validateVars")
    public void validateVars(String json, Boolean expected) {
        JsonNode root = fromString(json);
        Assert.assertEquals(JsonUtil.varsIsValid(root), expected.booleanValue());
    }

    @DataProvider(name = "validateVals")
    public Object[][] validateVals() {
        return new Object[][]{
                {"[]", true},
                {"[{\"Num\":\"1\", \"Case\":\"\"}]", true},
                {"[{\"Num\":\"1\", \"Case\":\"\"}, {\"Num\":\"2\", \"Case\":\"\"}]", true},

                {"{}", false},
                {"[1]", false},
                {"[{\"Num\":\"\"}]", false}, // test case object should contain two objects
                {"[{\"Num\":\"\", \"Case\":\"\", \"extra\":\"\"}]", false}, // test case object should contain two objects
                {"[{\"Num\":\"\", \"extra\":\"\"}]", false}, // test case object should contain Num and Case
        };
    }

    @Test(dataProvider = "validateVals")
    public void validateVals(String json, Boolean expected) {
        JsonNode root = fromString(json);
        Assert.assertEquals(JsonUtil.valsIsValid(root), expected.booleanValue());
    }
}
