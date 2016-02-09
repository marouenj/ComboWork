package combowork.marshal4j.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CliUtilTest {

    @Test
    public void shouldUseDefaultValuesWhenArgsIsNull() {
        String[] args = null;
        CliUtil.Config config = CliUtil.parseCli(args);

        Assert.assertEquals(config.getBaseDir(), ".");
        Assert.assertEquals(config.getCombineDir(), "combine");
        Assert.assertEquals(config.getMarshal4jDir(), "marshal4j");
        Assert.assertEquals(config.getPathToCombine(), "./combine");
        Assert.assertEquals(config.getPathToMarshal4j(), "./marshal4j");
    }

    @Test
    public void shouldUseDefaultValuesWhenArgsIsEmpty() {
        String[] args = new String[0];
        CliUtil.Config config = CliUtil.parseCli(args);

        Assert.assertEquals(config.getBaseDir(), ".");
        Assert.assertEquals(config.getCombineDir(), "combine");
        Assert.assertEquals(config.getMarshal4jDir(), "marshal4j");
        Assert.assertEquals(config.getPathToCombine(), "./combine");
        Assert.assertEquals(config.getPathToMarshal4j(), "./marshal4j");
    }

    @Test
    public void shouldOverrideDefaultValues() {
        String[] args = {"--base", "/base", "--combine", "combine2", "--marshal4j", "marshal4j2"};
        CliUtil.Config config = CliUtil.parseCli(args);

        Assert.assertEquals(config.getBaseDir(), "/base");
        Assert.assertEquals(config.getCombineDir(), "combine2");
        Assert.assertEquals(config.getMarshal4jDir(), "marshal4j2");
        Assert.assertEquals(config.getPathToCombine(), "/base/combine2");
        Assert.assertEquals(config.getPathToMarshal4j(), "/base/marshal4j2");
    }
}
