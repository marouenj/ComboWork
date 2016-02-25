package marouenj.combowork.tutorials.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Person {

    @NotNull
    private String name;

    @NotNull
    private String familyName;

    @Min(18)
    private byte age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }
}
