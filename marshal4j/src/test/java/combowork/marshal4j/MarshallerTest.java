package combowork.marshal4j;

import com.fasterxml.jackson.databind.JsonNode;
import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MarshallerTest extends Base {

    @DataProvider(name = "levelOnePrimitives")
    public Object[][] levelOnePrimitives() {
        String[] vars = new String[]{
                "[]",
                "[{\"type\":\"String\"}]",
                "[{\"type\":\"Boolean\"}]",
                "[{\"type\":\"Character\"}]",
                "[{\"type\":\"Byte\"}]", // TODO add byte ranges
                "[{\"type\":\"Short\"}]", // TODO add short ranges
                "[{\"type\":\"Integer\"}]", // TODO add int ranges
                "[{\"type\":\"Long\"}]", // TODO add long ranges
                "[{\"type\":\"Float\"}]", // TODO add float ranges
                "[{\"type\":\"Double\"}]", // TODO add double ranges
                "[{\"type\":\"boolean\"}]",
                "[{\"type\":\"char\"}]",
                "[{\"type\":\"byte\"}]",
                "[{\"type\":\"short\"}]",
                "[{\"type\":\"int\"}]",
                "[{\"type\":\"long\"}]",
                "[{\"type\":\"float\"}]",
                "[{\"type\":\"double\"}]",
                "[{\"type\":\"String\"}, {\"type\":\"boolean\"}, {\"type\":\"double\"}]",
        };

        String[] vals = new String[]{
                "[]", // any value
                "[\"sample\"]",
                "[true]",
                "[\"a\"]",
                "[1]",
                "[2]",
                "[3]",
                "[4]",
                "[5.0]",
                "[6.0]",
                "[true]",
                "[\"a\"]",
                "[1]",
                "[2]",
                "[3]",
                "[4]",
                "[5.0]",
                "[6.0]",
                "[\"sample\", true, 1.0]",
        };

        // writeObject, writeBoolean, writeChar, writeByte, writeShort, writeInt, writeLong, writeFloat, writeDouble
        Integer[][] expected = new Integer[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // String
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Boolean
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Character
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Byte
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Short
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Integer
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Long
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Float
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Double
                {0, 1, 0, 0, 0, 0, 0, 0, 0}, // boolean
                {0, 0, 1, 0, 0, 0, 0, 0, 0}, // char
                {0, 0, 0, 1, 0, 0, 0, 0, 0}, // byte
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // short
                {0, 0, 0, 0, 0, 1, 0, 0, 0}, // int
                {0, 0, 0, 0, 0, 0, 1, 0, 0}, // long
                {0, 0, 0, 0, 0, 0, 0, 1, 0}, // float
                {0, 0, 0, 0, 0, 0, 0, 0, 1}, // double
                {1, 1, 0, 0, 0, 0, 0, 0, 1}, // String, boolean, double
        };

        Object[][] data = new Object[vars.length][];

        for (int i = 0; i < vars.length; i++) {
            Object[] json = {vars[i], vals[i]};
            data[i] = new Object[]{json, expected[i]};
        }

        return data;
    }

    @Test(dataProvider = "levelOnePrimitives")
    public void levelOnePrimitives(Object[] json, Integer[] expected) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        JsonNode vars = fromString((String) json[0]);
        JsonNode vals = fromString((String) json[1]);

        ObjectOutput out = Mockito.mock(ObjectOutput.class);

        Method method = Marshaller.class.getDeclaredMethod("forEachVar", JsonNode.class, JsonNode.class, ObjectOutput.class);
        method.setAccessible(true);
        method.invoke(null, vars, vals, out);

        verify(out, times(expected[0])).writeObject(any(Object.class));
        verify(out, times(expected[1])).writeBoolean(any(Boolean.class));
        verify(out, times(expected[2])).writeChar(any(Character.class));
        verify(out, times(expected[3])).writeByte(any(Byte.class));
        verify(out, times(expected[4])).writeShort(any(Short.class));
        verify(out, times(expected[5])).writeInt(any(Integer.class));
        verify(out, times(expected[6])).writeLong(any(Long.class));
        verify(out, times(expected[7])).writeFloat(any(Float.class));
        verify(out, times(expected[8])).writeDouble(any(Double.class));
    }

    @DataProvider(name = "nestedPrimitives")
    public Object[][] nestedPrimitives() {
        String[] vars = new String[]{
                "[{\"type\":\"String\"},{\"prefix\":\"java.util\", \"suffix\":\"Date\", \"vals\":[{\"type\":\"int\", \"setter\": \"setYear\"}]}]",
                "[{\"type\":\"String\"},{\"prefix\":\"java.util\", \"suffix\":\"Date\", \"vals\":[{\"type\":\"int\", \"setter\": \"setYear\"}, {\"type\":\"int\", \"setter\": \"setMonth\"}, {\"type\":\"int\", \"setter\": \"setDate\"}]}]",
        };

        String[] vals = new String[]{
                "[\"sample\",2015]",
                "[\"sample\",2015,10,5]",
        };

        // writeObject, writeBoolean, writeChar, writeByte, writeShort, writeInt, writeLong, writeFloat, writeDouble
        Integer[][] expected = new Integer[][]{
                {2, 0, 0, 0, 0, 0, 0, 0, 0}, // String, Date->year
                {2, 0, 0, 0, 0, 0, 0, 0, 0}, // String, Date->(year,month,day)
        };

        Object[][] data = new Object[vars.length][];

        for (int i = 0; i < vars.length; i++) {
            Object[] json = {vars[i], vals[i]};
            data[i] = new Object[]{json, expected[i]};
        }

        return data;
    }

    @Test(dataProvider = "nestedPrimitives")
    public void nestedPrimitives(Object[] json, Integer[] expected) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        this.levelOnePrimitives(json, expected);
    }
}
