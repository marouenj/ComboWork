package combowork.marshal4j.util;

import mockit.MockUp;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtilTest {

    private final static String DUMMY_STRING = "";

    @BeforeClass
    public void init() {
//        MockitoAnnotations.initMocks(this);
    }

    /**
     * Should throw an exception when config is null
     */
    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Config is null")
    public void shouldThrowExceptionWhenConfigIsNull() {
        FileUtil.getListOfVars(null);
    }

    /**
     * Should throw an exception when base dir is not a dir
     */
    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Base dir should be a dir, not a file")
    public void shouldThrowExceptionWhenBaseDirIsNotADir() {
        new MockUp<CliUtil.Config>() {
            @mockit.Mock
            public String getBaseDir() {
                return DUMMY_STRING;
            }
        };

        new MockUp<File>() {
            @mockit.Mock
            public boolean isDirectory() {
                return false;
            }
        };

        FileUtil.getListOfVars(new CliUtil.Config());
    }

    /**
     * Should return an empty list when base dir contains nested dirs only
     */
    @Test
    public void shouldReturnEmptyListWhenBaseDirContainsNestedDirsOnly() {
        new MockUp<CliUtil.Config>() {
            @mockit.Mock
            public String getBaseDir() {
                return DUMMY_STRING;
            }
        };

        new MockUp<File>() {
            final int numberOfFiles = 10;

            int isDirectoryCallNumber = 0;

            @mockit.Mock
            public boolean isDirectory() {
                isDirectoryCallNumber++;
                if (isDirectoryCallNumber < 1 + numberOfFiles + 1) {
                    return true;
                }
                throw new RuntimeException("isDirectory called more than the expected number of times");
            }

            @mockit.Mock
            public File[] listFiles() {
                File[] files = new File[numberOfFiles];
                for (int i = 0; i < files.length; i++) {
                    files[i] = new File(DUMMY_STRING);
                }
                return files;
            }
        };

        List<String> listOfVars = FileUtil.getListOfVars(new CliUtil.Config());

        int i = 0;
        for (String ignored : listOfVars) {
            i++;
        }

        Assert.assertEquals(i, 0);
    }

    /**
     * Should return empty list when base dir contains no json files
     */
    @Test
    public void shouldReturnEmptyListWhenBaseDirContainsNoJsonFiles() {
        new MockUp<CliUtil.Config>() {
            @mockit.Mock
            public String getBaseDir() {
                return DUMMY_STRING;
            }
        };

        new MockUp<File>() {
            final int numberOfFiles = 10;
            final String fileName = "no_json.txt";

            int isDirectoryCallNumber = 0;

            @mockit.Mock
            public boolean isDirectory() {
                isDirectoryCallNumber++;
                if (isDirectoryCallNumber == 1) {
                    return true;
                } else if (isDirectoryCallNumber < 1 + numberOfFiles + 1) {
                    return false;
                }
                throw new RuntimeException("isDirectory called more than the expected number of times");
            }

            @mockit.Mock
            public File[] listFiles() {
                File[] files = new File[numberOfFiles];
                for (int i = 0; i < files.length; i++) {
                    files[i] = new File(DUMMY_STRING);
                }
                return files;
            }

            @mockit.Mock
            public String getName() {
                return fileName;
            }
        };

        List<String> listOfVars = FileUtil.getListOfVars(new CliUtil.Config());

        int i = 0;
        for (String ignored : listOfVars) {
            i++;
        }

        Assert.assertEquals(i, 0);
    }

    @DataProvider(name = "shouldWorkErrorFree")
    public Object[][] shouldWorkErrorFreeP() {
        return new Object[][]{
                {"#1", new ArrayList<String>() {{
                    add("1.json");
                }}},
                {"#2", new ArrayList<String>() {{
                    add("1.json");
                    add("2.json");
                }}},
                {"#3", new ArrayList<String>() {{
                    add("1.json");
                    add("2.json");
                    add("3.json");
                }}},
                {"#4", new ArrayList<String>() {{
                    add("1.json");
                    add("2.json");
                    add("3.json");
                    add("4.xml");
                }}},
        };
    }

    /**
     * Should work error free
     */
    @Test(dataProvider = "shouldWorkErrorFree")
    public void shouldWorkErrorFree(String testCase, List<String> fileNames) {
        new MockUp<CliUtil.Config>() {
            @mockit.Mock
            public String getBaseDir() {
                return DUMMY_STRING;
            }

            @mockit.Mock
            public String getCombineDir() {
                return DUMMY_STRING;
            }
        };

        new MockUp<File>() {
            final int numberOfFiles = fileNames.size();

            int isDirectoryCallNumber = 0;
            int getNameCallNumber = 0;

            @mockit.Mock
            public boolean isDirectory() {
                isDirectoryCallNumber++;
                if (isDirectoryCallNumber == 1) {
                    return true;
                } else if (isDirectoryCallNumber < 1 + numberOfFiles + 1) {
                    return false;
                }
                throw new RuntimeException("isDirectory called more than the expected number of times");
            }

            @mockit.Mock
            public File[] listFiles() {
                File[] files = new File[numberOfFiles];
                for (int i = 0; i < files.length; i++) {
                    files[i] = new File(DUMMY_STRING);
                }
                return files;
            }

            @mockit.Mock
            public String getName() {
                getNameCallNumber++;
                if (getNameCallNumber > 3 * fileNames.size()) {
                    throw new RuntimeException("getName called more than the expected number of times");
                }
                return fileNames.get((getNameCallNumber - 1) / 3);
            }

            @mockit.Mock
            public boolean exists() {
                return true;
            }

            @mockit.Mock
            public boolean isFile() {
                return true;
            }
        };

        List<String> listOfVars = FileUtil.getListOfVars(new CliUtil.Config());

        int actual = 0;
        for (String ignored : listOfVars) {
            actual++;
        }

        int expected = fileNames.size();
        for (String fileName : fileNames) {
            if (!fileName.endsWith(".json")) {
                expected--;
            }
        }

        Assert.assertEquals(actual, expected);
    }
}
