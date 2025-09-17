package org.example.features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

/**
 * Record 特性单元测试
 */
public class RecordExampleTest {

    @Test
    @DisplayName("测试基本 Person Record 创建和访问")
    void testBasicPersonRecord() {
        // Given
        String name = "张三";
        int age = 25;
        String email = "zhangsan@example.com";
        
        // When
        RecordExample.Person person = new RecordExample.Person(name, age, email);
        
        // Then
        assertThat(person.name()).isEqualTo(name);
        assertThat(person.age()).isEqualTo(age);
        assertThat(person.email()).isEqualTo(email);
        assertThat(person.isAdult()).isTrue();
    }

    @Test
    @DisplayName("测试 Person Record 的紧凑构造函数验证")
    void testPersonRecordValidation() {
        // 测试负数年龄
        assertThatThrownBy(() -> new RecordExample.Person("张三", -1, "test@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("年龄不能为负数");
        
        // 测试无效邮箱
        assertThatThrownBy(() -> new RecordExample.Person("张三", 25, "invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("邮箱格式不正确");
        
        // 测试空邮箱
        assertThatThrownBy(() -> new RecordExample.Person("张三", 25, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("邮箱格式不正确");
    }

    @Test
    @DisplayName("测试 Person Record 的相等性")
    void testPersonRecordEquality() {
        // Given
        RecordExample.Person person1 = new RecordExample.Person("张三", 25, "zhangsan@example.com");
        RecordExample.Person person2 = new RecordExample.Person("张三", 25, "zhangsan@example.com");
        RecordExample.Person person3 = new RecordExample.Person("李四", 25, "zhangsan@example.com");
        
        // Then
        assertThat(person1).isEqualTo(person2);
        assertThat(person1).isNotEqualTo(person3);
        assertThat(person1.hashCode()).isEqualTo(person2.hashCode());
        assertThat(person1.hashCode()).isNotEqualTo(person3.hashCode());
    }

    @Test
    @DisplayName("测试嵌套 Record - Employee")
    void testEmployeeRecord() {
        // Given
        String name = "李四";
        int age = 30;
        String email = "lisi@example.com";
        RecordExample.Address address = new RecordExample.Address("中关村大街1号", "北京", "100000");
        LocalDate hireDate = LocalDate.of(2020, 1, 1);
        
        // When
        RecordExample.Employee employee = new RecordExample.Employee(name, age, email, address, hireDate);
        
        // Then
        assertThat(employee.name()).isEqualTo(name);
        assertThat(employee.age()).isEqualTo(age);
        assertThat(employee.email()).isEqualTo(email);
        assertThat(employee.address()).isEqualTo(address);
        assertThat(employee.hireDate()).isEqualTo(hireDate);
        assertThat(employee.getYearsOfService()).isGreaterThan(0);
    }

    @Test
    @DisplayName("测试 Employee Record 的空值验证")
    void testEmployeeRecordValidation() {
        RecordExample.Address address = new RecordExample.Address("中关村大街1号", "北京", "100000");
        LocalDate hireDate = LocalDate.of(2020, 1, 1);
        
        // 测试空姓名
        assertThatThrownBy(() -> new RecordExample.Employee(null, 30, "test@example.com", address, hireDate))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("姓名不能为空");
        
        // 测试空地址
        assertThatThrownBy(() -> new RecordExample.Employee("李四", 30, "test@example.com", null, hireDate))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("地址不能为空");
        
        // 测试空入职日期
        assertThatThrownBy(() -> new RecordExample.Employee("李四", 30, "test@example.com", address, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("入职日期不能为空");
    }

    @Test
    @DisplayName("测试泛型 Record - Pair")
    void testGenericPairRecord() {
        // Given
        String first = "Hello";
        Integer second = 42;
        
        // When
        RecordExample.Pair<String, Integer> pair = new RecordExample.Pair<>(first, second);
        RecordExample.Pair<Integer, String> swappedPair = pair.swap();
        
        // Then
        assertThat(pair.first()).isEqualTo(first);
        assertThat(pair.second()).isEqualTo(second);
        assertThat(swappedPair.first()).isEqualTo(second);
        assertThat(swappedPair.second()).isEqualTo(first);
    }

    @Test
    @DisplayName("测试 Record 的 toString 方法")
    void testRecordToString() {
        // Given
        RecordExample.Person person = new RecordExample.Person("张三", 25, "zhangsan@example.com");
        
        // When
        String toString = person.toString();
        
        // Then
        assertThat(toString).contains("Person[name=张三, age=25, email=zhangsan@example.com]");
    }

    @Test
    @DisplayName("测试未成年人的 isAdult 方法")
    void testMinorIsAdult() {
        // Given
        RecordExample.Person minor = new RecordExample.Person("小明", 16, "xiaoming@example.com");
        
        // Then
        assertThat(minor.isAdult()).isFalse();
    }

    @Test
    @DisplayName("测试 Address Record")
    void testAddressRecord() {
        // Given
        String street = "中关村大街1号";
        String city = "北京";
        String zipCode = "100000";
        
        // When
        RecordExample.Address address = new RecordExample.Address(street, city, zipCode);
        
        // Then
        assertThat(address.street()).isEqualTo(street);
        assertThat(address.city()).isEqualTo(city);
        assertThat(address.zipCode()).isEqualTo(zipCode);
    }
}
