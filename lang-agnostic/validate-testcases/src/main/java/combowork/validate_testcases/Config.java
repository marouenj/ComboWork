package combowork.validate_testcases;

/**
 * Wrapper for config data
 * Sets default values
 * Overrides default values if specified
 *
 * @author marouenj
 */
public class Config {

    public static final String BASE_DIR_KEY = "base_dir";

    private String baseDir = "./combined";

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
