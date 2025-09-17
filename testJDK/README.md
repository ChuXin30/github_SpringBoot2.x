# Java 21 特性演示项目

这个项目展示了 Java 21 中的主要新特性，包括代码示例和完整的单元测试。

## 🚀 Java 21 新特性

### 1. Record 类 (Java 14+)
- **特性**: 简洁的不可变数据类
- **示例**: `RecordExample.java`
- **测试**: `RecordExampleTest.java`

### 2. Pattern Matching (Java 16+)
- **特性**: instanceof 模式匹配、switch 模式匹配
- **示例**: `PatternMatchingExample.java`
- **测试**: `PatternMatchingExampleTest.java`

### 3. Text Blocks (Java 15+)
- **特性**: 多行字符串字面量
- **示例**: `TextBlocksExample.java`
- **测试**: `TextBlocksExampleTest.java`

### 4. Switch Expression (Java 14+)
- **特性**: 表达式式的 switch 语句
- **示例**: `SwitchExpressionExample.java`
- **测试**: `SwitchExpressionExampleTest.java`

### 5. Virtual Threads (Java 21)
- **特性**: 轻量级虚拟线程
- **示例**: `VirtualThreadsExample.java`
- **测试**: `VirtualThreadsExampleTest.java`

## 📋 项目结构

```
testJDK/
├── src/
│   ├── main/java/org/example/
│   │   ├── Java21FeaturesDemo.java          # 主演示类
│   │   └── features/
│   │       ├── RecordExample.java           # Record 特性示例
│   │       ├── PatternMatchingExample.java  # Pattern Matching 示例
│   │       ├── TextBlocksExample.java       # Text Blocks 示例
│   │       ├── SwitchExpressionExample.java # Switch Expression 示例
│   │       └── VirtualThreadsExample.java   # Virtual Threads 示例
│   └── test/java/org/example/
│       ├── Java21FeaturesTestSuite.java     # 测试套件
│       └── features/
│           ├── RecordExampleTest.java
│           ├── PatternMatchingExampleTest.java
│           ├── TextBlocksExampleTest.java
│           ├── SwitchExpressionExampleTest.java
│           └── VirtualThreadsExampleTest.java
├── pom.xml                                  # Maven 配置
└── README.md                               # 项目说明
```

## 🛠️ 环境要求

- **Java**: JDK 21 或更高版本
- **Maven**: 3.6.0 或更高版本
- **IDE**: 支持 Java 21 的 IDE（推荐 IntelliJ IDEA 或 Eclipse）

## 🏃‍♂️ 运行方式

### 1. 运行演示程序

```bash
# 编译项目
mvn clean compile

# 运行主演示程序
mvn exec:java -Dexec.mainClass="org.example.Java21FeaturesDemo"
```

### 2. 运行单元测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=RecordExampleTest

# 运行测试套件
mvn test -Dtest=Java21FeaturesTestSuite
```

### 3. 生成测试报告

```bash
# 生成测试报告
mvn surefire-report:report

# 查看报告
open target/site/surefire-report.html
```

## 📚 特性详解

### Record 类
```java
public record Person(String name, int age, String email) {
    public Person {
        if (age < 0) throw new IllegalArgumentException("年龄不能为负数");
    }
    
    public boolean isAdult() {
        return age >= 18;
    }
}
```

### Pattern Matching
```java
// instanceof 模式匹配
if (shape instanceof Circle circle) {
    return "圆形，半径: " + circle.radius();
}

// switch 模式匹配
return switch (shape) {
    case Circle c -> "圆形";
    case Rectangle r -> "矩形";
    case Triangle t -> "三角形";
};
```

### Text Blocks
```java
String json = """
    {
        "name": "张三",
        "age": 25,
        "email": "zhangsan@example.com"
    }
    """;
```

### Switch Expression
```java
String dayType = switch (day) {
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
    case SATURDAY, SUNDAY -> "周末";
};
```

### Virtual Threads
```java
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> future = executor.submit(() -> {
        // 虚拟线程中的任务
        return "任务完成";
    });
    String result = future.get();
}
```

## 🧪 测试覆盖

项目包含完整的单元测试，覆盖以下方面：

- ✅ Record 类的创建、验证、相等性
- ✅ Pattern Matching 的各种模式
- ✅ Text Blocks 的格式化和内容验证
- ✅ Switch Expression 的各种表达式
- ✅ Virtual Threads 的创建、执行、异常处理

## 🔧 配置说明

### Maven 配置
项目使用 Maven 构建，配置了：
- Java 21 编译目标
- JUnit 5 测试框架
- AssertJ 断言库
- 预览特性支持

### 编译器参数
```xml
<compilerArgs>
    <arg>--enable-preview</arg>
</compilerArgs>
```

## 📖 学习资源

- [Oracle Java 21 官方文档](https://docs.oracle.com/en/java/javase/21/)
- [Java 21 新特性指南](https://openjdk.org/projects/jdk/21/)
- [JEP 文档](https://openjdk.org/jeps/)

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目！

## 📄 许可证

本项目采用 MIT 许可证。
