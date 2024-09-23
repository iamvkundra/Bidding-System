package com.intuit.auction.service.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommonUtilsTest {

    @Test
    void toJson_shouldConvertObjectToJson() {
        TestObject testObject = new TestObject("John", 30);
        String jsonResult = CommonUtils.toJson(testObject, false);
        assertThat(jsonResult).isEqualTo("{\"name\":\"John\",\"age\":30}");
    }

    @Test
    void toJson_shouldConvertObjectToPrettyPrintedJson() {
        TestObject testObject = new TestObject("John", 30);
        String jsonResult = CommonUtils.toJson(testObject, true);
        assertThat(jsonResult).isEqualTo("{\n" +
                "  \"name\" : \"John\",\n" +
                "  \"age\" : 30\n" +
                "}");
    }

    @Test
    void toJson_shouldReturnStringIfInputIsString() {
        String inputString = "{\"name\":\"John\",\"age\":30}";
        String jsonResult = CommonUtils.toJson(inputString, false);
        assertThat(jsonResult).isEqualTo(inputString);
    }

    @Test
    void toJson_shouldThrowRuntimeExceptionOnInvalidObject() {
        Object invalidObject = new Object() {
            @Override
            public String toString() {
                throw new IllegalStateException("Invalid object");
            }
        };
        assertThrows(RuntimeException.class, () -> CommonUtils.toJson(invalidObject, false));
    }

    private static class TestObject {
        private String name;
        private int age;

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }
        public String getName() {
            return name;
        }
        public int getAge() {
            return age;
        }
    }
}
