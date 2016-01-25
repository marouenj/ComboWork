package combowork.marshal4j.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CliUtilTest {

    @Test
    public void shouldUseDefaultValuesWhenArgsIsNull() {
        String[] args = null;
        CliUtil.Config config = CliUtil.parseCli(args);

        Assert.assertEquals(config.getBaseDir(), ".");
        Assert.assertEquals(config.getCombineDir(), "./combine");
        Assert.assertEquals(config.getMarshal4jDir(), "./marshal4j");
    }

    @Test
    public void shouldUseDefaultValuesWhenArgsIsEmpty() {
        String[] args = new String[0];
        CliUtil.Config config = CliUtil.parseCli(args);

        Assert.assertEquals(config.getBaseDir(), ".");
        Assert.assertEquals(config.getCombineDir(), "./combine");
        Assert.assertEquals(config.getMarshal4jDir(), "./marshal4j");
    }

    @Test
    public void shouldOverrideDefaultValues() {
        String[] args = {"--in", "/base", "--combine", "/base/combine", "--marshal4j", "/base/marshal4j"};
        CliUtil.Config config = CliUtil.parseCli(args);

        Assert.assertEquals(config.getBaseDir(), "/base");
        Assert.assertEquals(config.getCombineDir(), "/base/combine");
        Assert.assertEquals(config.getMarshal4jDir(), "/base/marshal4j");
    }
}
