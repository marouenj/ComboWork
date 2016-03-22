package marouenj.combowork.tutorials.model;

import combowork.expect4j.TestCaseLoader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.net.URL;
import java.util.Set;

public class PersonTest {

    private static Validator validator;

    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DataProvider(name = "isValid")
    public Object[][] isValid() {
        ClassLoader classLoader = getClass().getClassLoader();

        URL load4j = classLoader.getResource("combined/all.json");
        if (load4j == null) {
            throw new NullPointerException();
        }
        File load4jFile = new File(load4j.getFile());

        URL expect4j = classLoader.getResource("rules/all.json");
        if (expect4j == null) {
            throw new NullPointerException();
        }
        File expect4jFile = new File(expect4j.getFile());

        return TestCaseLoader.testNgAdapter(load4jFile, expect4jFile);
    }

    @Test(dataProvider = "isValid")
    public void isValid(String name, String familyName, Number age, Number expected) {
        Person person = new Person();
        person.setName(name);
        person.setFamilyName(familyName);
        person.setAge(age.byteValue());

        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);
        Assert.assertEquals(expected, constraintViolations.size());
    }
}
