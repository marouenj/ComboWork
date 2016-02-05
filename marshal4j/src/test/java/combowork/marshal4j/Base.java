package combowork.marshal4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

public class Base {
    private final static ObjectMapper MAPPER = new ObjectMapper();

    protected JsonNode fromString(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static class Top implements Serializable {

        private Boolean b;
        private Middle m;

        public Boolean getB() {
            return b;
        }

        public void setB(Boolean b) {
            this.b = b;
        }

        public Middle getM() {
            return m;
        }

        public void setM(Middle m) {
            this.m = m;
        }
    }

    public static class Middle implements Serializable {

        private Integer i;
        private Bottom b;

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public Bottom getB() {
            return b;
        }

        public void setB(Bottom b) {
            this.b = b;
        }
    }

    public static class Bottom implements Serializable {

        private int i;
        private char c;
        private String s;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public char getC() {
            return c;
        }

        public void setC(char c) {
            this.c = c;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }
}
