package org.example.features;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Java 14+ Record 特性示例
 * Record 是 Java 14 引入的预览特性，在 Java 16 中正式发布
 * 它提供了一种简洁的方式来创建不可变的数据类
 */
public class RecordExample {

    // 基本 Record 示例
    public record Person(String name, int age, String email) {
        // Record 可以包含构造函数、方法和静态方法
        public Person {
            // 紧凑构造函数 - 用于验证和规范化
            if (age < 0) {
                throw new IllegalArgumentException("年龄不能为负数");
            }
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
        }
        
        // 可以添加自定义方法
        public boolean isAdult() {
            return age >= 18;
        }
        
        // 可以重写方法
        @Override
        public String toString() {
            return String.format("Person[name=%s, age=%d, email=%s]", name, age, email);
        }
    }

    // 嵌套 Record 示例
    public record Address(String street, String city, String zipCode) {}

    public record Employee(String name, int age, String email, Address address, LocalDate hireDate) {
        public Employee {
            Objects.requireNonNull(name, "姓名不能为空");
            Objects.requireNonNull(address, "地址不能为空");
            Objects.requireNonNull(hireDate, "入职日期不能为空");
        }
        
        public int getYearsOfService() {
            return LocalDate.now().getYear() - hireDate.getYear();
        }
    }

    // 泛型 Record 示例
    public record Pair<T, U>(T first, U second) {
        public Pair<U, T> swap() {
            return new Pair<U, T>(second, first);
        }
    }

    // 使用示例方法
    public static void demonstrateRecords() {
        System.out.println("=== Record 特性演示 ===");
        
        // 创建 Person Record
        Person person = new Person("张三", 25, "zhangsan@example.com");
        System.out.println("Person: " + person);
        System.out.println("是否成年: " + person.isAdult());
        System.out.println("姓名: " + person.name());
        System.out.println("年龄: " + person.age());
        System.out.println("邮箱: " + person.email());
        
        // 创建嵌套 Record
        Address address = new Address("中关村大街1号", "北京", "100000");
        Employee employee = new Employee("李四", 30, "lisi@example.com", address, LocalDate.of(2020, 1, 1));
        System.out.println("\nEmployee: " + employee);
        System.out.println("工作年限: " + employee.getYearsOfService());
        
        // 泛型 Record
        Pair<String, Integer> pair = new Pair<String, Integer>("Hello", 42);
        System.out.println("\nPair: " + pair);
        System.out.println("交换后: " + pair.swap());
        
        // Record 的 equals 和 hashCode 自动实现
        Person person1 = new Person("张三", 25, "zhangsan@example.com");
        Person person2 = new Person("张三", 25, "zhangsan@example.com");
        System.out.println("\nRecord 相等性比较: " + person1.equals(person2));
        System.out.println("HashCode 相同: " + (person1.hashCode() == person2.hashCode()));
    }
}
