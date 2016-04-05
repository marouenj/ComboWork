package marouenj.combowork.tutorials.model;

import combowork.load4j.TestCaseAdapter;
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
        URL resource = classLoader.getResource("combined/isValid.json");
        if (resource == null) {
            throw new NullPointerException();
        }
        File file = new File(resource.getFile());
        return TestCaseAdapter.testNgAdapter(file);
    }

    @Test(dataProvider = "isValid")
    public void isValid(String name, String familyName, Number age) {
        Person person = new Person();
        person.setName(name);
        person.setFamilyName(familyName);
        person.setAge(age.byteValue());

        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);
        Assert.assertEquals(0, constraintViolations.size());
    }

    @DataProvider(name = "isUnvalid")
    public Object[][] isUnvalid() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("combined/isUnvalid.json");
        if (resource == null) {
            throw new NullPointerException();
        }
        File file = new File(resource.getFile());
        return TestCaseAdapter.testNgAdapter(file);
    }

    @Test(dataProvider = "isUnvalid")
    public void isUnvalid(String name, String familyName, Number age) {
        Person person = new Person();
        person.setName(name);
        person.setFamilyName(familyName);
        person.setAge(age.byteValue());

        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);
        Assert.assertNotEquals(0, constraintViolations.size());
    }
}
