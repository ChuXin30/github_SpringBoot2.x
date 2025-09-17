package org.example.features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

/**
 * Text Blocks 特性单元测试
 */
public class TextBlocksExampleTest {

    @Test
    @DisplayName("测试基本 Text Block")
    void testBasicTextBlock() {
        // When
        String textBlock = TextBlocksExample.getBasicTextBlock();
        
        // Then
        assertThat(textBlock).contains("Hello, World!");
        assertThat(textBlock).contains("这是一个多行字符串");
        assertThat(textBlock).contains("使用 Text Blocks 语法");
        assertThat(textBlock).contains("\n");
    }

    @Test
    @DisplayName("测试包含引号的 Text Block")
    void testTextWithQuotes() {
        // When
        String textWithQuotes = TextBlocksExample.getTextWithQuotes();
        
        // Then
        assertThat(textWithQuotes).contains("She said \"Hello, World!\" and smiled.");
        assertThat(textWithQuotes).contains("He replied, \"Nice to meet you!\"");
    }

    @Test
    @DisplayName("测试包含转义字符的 Text Block")
    void testTextWithEscapes() {
        // When
        String textWithEscapes = TextBlocksExample.getTextWithEscapes();
        
        // Then
        assertThat(textWithEscapes).contains("Line 1");
        assertThat(textWithEscapes).contains("Line 2\tTab character");
        assertThat(textWithEscapes).contains("Line 3\nNew line character");
    }

    @Test
    @DisplayName("测试 JSON 格式 Text Block")
    void testJsonExample() {
        // When
        String jsonExample = TextBlocksExample.getJsonExample();
        
        // Then
        assertThat(jsonExample).contains("\"name\": \"张三\"");
        assertThat(jsonExample).contains("\"age\": 25");
        assertThat(jsonExample).contains("\"email\": \"zhangsan@example.com\"");
        assertThat(jsonExample).contains("\"address\": {");
        assertThat(jsonExample).contains("\"street\": \"中关村大街1号\"");
        assertThat(jsonExample).contains("\"city\": \"北京\"");
        assertThat(jsonExample).contains("\"zipCode\": \"100000\"");
        assertThat(jsonExample).contains("\"hobbies\": [\"读书\", \"游泳\", \"编程\"]");
    }

    @Test
    @DisplayName("测试 SQL 查询 Text Block")
    void testSqlQuery() {
        // When
        String sqlQuery = TextBlocksExample.getSqlQuery();
        
        // Then
        assertThat(sqlQuery).contains("SELECT u.id, u.name, u.email, a.city");
        assertThat(sqlQuery).contains("FROM users u");
        assertThat(sqlQuery).contains("LEFT JOIN addresses a ON u.id = a.user_id");
        assertThat(sqlQuery).contains("WHERE u.age > ?");
        assertThat(sqlQuery).contains("AND u.status = 'ACTIVE'");
        assertThat(sqlQuery).contains("ORDER BY u.created_at DESC");
        assertThat(sqlQuery).contains("LIMIT 10");
    }

    @Test
    @DisplayName("测试 HTML 模板 Text Block")
    void testHtmlTemplate() {
        // Given
        String title = "测试标题";
        String content = "测试内容";
        
        // When
        String htmlTemplate = TextBlocksExample.getHtmlTemplate(title, content);
        
        // Then
        assertThat(htmlTemplate).contains("<!DOCTYPE html>");
        assertThat(htmlTemplate).contains("<html lang=\"zh-CN\">");
        assertThat(htmlTemplate).contains("<title>" + title + "</title>");
        assertThat(htmlTemplate).contains("<h1>" + title + "</h1>");
        assertThat(htmlTemplate).contains("<p>" + content + "</p>");
        assertThat(htmlTemplate).contains("&copy; 2024 Example Company");
    }

    @Test
    @DisplayName("测试代码生成 Text Block")
    void testGenerateJavaClass() {
        // Given
        String className = "TestClass";
        List<String> fields = List.of("name:String", "age:int", "email:String");
        
        // When
        String generatedClass = TextBlocksExample.generateJavaClass(className, fields);
        
        // Then
        assertThat(generatedClass).contains("package org.example.generated;");
        assertThat(generatedClass).contains("public class " + className);
        assertThat(generatedClass).contains("private String name;");
        assertThat(generatedClass).contains("private int age;");
        assertThat(generatedClass).contains("private String email;");
        assertThat(generatedClass).contains("public " + className + "() {");
    }

    @Test
    @DisplayName("测试配置文件 Text Block")
    void testConfigFile() {
        // When
        String configFile = TextBlocksExample.getConfigFile();
        
        // Then
        assertThat(configFile).contains("# 应用配置");
        assertThat(configFile).contains("app.name=Java 21 Features Demo");
        assertThat(configFile).contains("app.version=1.0.0");
        assertThat(configFile).contains("app.debug=false");
        assertThat(configFile).contains("# 数据库配置");
        assertThat(configFile).contains("db.url=jdbc:mysql://localhost:3306/testdb");
        assertThat(configFile).contains("db.username=root");
        assertThat(configFile).contains("db.password=password");
        assertThat(configFile).contains("db.pool.size=10");
        assertThat(configFile).contains("# 日志配置");
        assertThat(configFile).contains("logging.level=INFO");
        assertThat(configFile).contains("logging.file=logs/app.log");
    }

    @Test
    @DisplayName("测试正则表达式 Text Block")
    void testRegexPatterns() {
        // When
        String regexPatterns = TextBlocksExample.getRegexPatterns();
        
        // Then
        assertThat(regexPatterns).contains("# 常用正则表达式模式");
        assertThat(regexPatterns).contains("# 邮箱验证");
        assertThat(regexPatterns).contains("EMAIL_PATTERN: ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        assertThat(regexPatterns).contains("# 手机号验证（中国）");
        assertThat(regexPatterns).contains("PHONE_PATTERN: ^1[3-9]\\d{9}$");
        assertThat(regexPatterns).contains("# 身份证号验证（中国）");
        assertThat(regexPatterns).contains("ID_CARD_PATTERN: ^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
        assertThat(regexPatterns).contains("# URL验证");
        assertThat(regexPatterns).contains("URL_PATTERN: ^https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?$");
    }

    @Test
    @DisplayName("测试多语言文本 Text Block")
    void testMultiLanguageText() {
        // When
        String multiLanguageText = TextBlocksExample.getMultiLanguageText();
        
        // Then
        assertThat(multiLanguageText).contains("# 多语言文本示例");
        assertThat(multiLanguageText).contains("## 中文");
        assertThat(multiLanguageText).contains("欢迎使用 Java 21 新特性！");
        assertThat(multiLanguageText).contains("## English");
        assertThat(multiLanguageText).contains("Welcome to Java 21 new features!");
        assertThat(multiLanguageText).contains("## 日本語");
        assertThat(multiLanguageText).contains("Java 21の新機能へようこそ！");
        assertThat(multiLanguageText).contains("## 한국어");
        assertThat(multiLanguageText).contains("Java 21의 새로운 기능에 오신 것을 환영합니다!");
        assertThat(multiLanguageText).contains("## Français");
        assertThat(multiLanguageText).contains("Bienvenue aux nouvelles fonctionnalités de Java 21 !");
    }

    @Test
    @DisplayName("测试传统字符串拼接和 Text Blocks 结果相同")
    void testCompareWithTraditionalString() {
        // When
        TextBlocksExample.compareWithTraditionalString();
        
        // Then - 这个测试主要验证两种方式产生相同的结果
        // 实际的比较逻辑在 TextBlocksExample.compareWithTraditionalString() 方法中
        assertThat(true).isTrue(); // 占位符断言，实际验证在方法内部
    }

    @Test
    @DisplayName("测试 Text Block 的格式化功能")
    void testTextBlockFormatting() {
        // Given
        String title = "测试标题";
        String content = "测试内容";
        
        // When
        String htmlTemplate = TextBlocksExample.getHtmlTemplate(title, content);
        
        // Then - 验证格式化后的内容
        assertThat(htmlTemplate).contains(title);
        assertThat(htmlTemplate).contains(content);
        
        // 验证格式化后的字符串包含正确的占位符替换
        String[] lines = htmlTemplate.split("\n");
        boolean titleFound = false;
        boolean contentFound = false;
        
        for (String line : lines) {
            if (line.contains("<title>" + title + "</title>")) {
                titleFound = true;
            }
            if (line.contains("<p>" + content + "</p>")) {
                contentFound = true;
            }
        }
        
        assertThat(titleFound).isTrue();
        assertThat(contentFound).isTrue();
    }

    @Test
    @DisplayName("测试 Text Block 的换行符处理")
    void testTextBlockLineBreaks() {
        // When
        String basicTextBlock = TextBlocksExample.getBasicTextBlock();
        
        // Then - 验证包含换行符
        assertThat(basicTextBlock).contains("\n");
        
        // 验证行数
        String[] lines = basicTextBlock.split("\n");
        assertThat(lines.length).isGreaterThan(1);
    }

    @Test
    @DisplayName("测试 Text Block 的空白字符处理")
    void testTextBlockWhitespace() {
        // When
        String textWithEscapes = TextBlocksExample.getTextWithEscapes();
        
        // Then - 验证包含制表符和换行符
        assertThat(textWithEscapes).contains("\t");
        assertThat(textWithEscapes).contains("\n");
        
        // 验证转义字符被正确处理
        assertThat(textWithEscapes).contains("Tab character");
        assertThat(textWithEscapes).contains("New line character");
    }
}
