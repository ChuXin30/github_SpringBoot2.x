package org.example.features;

import java.util.List;
import java.util.Map;

/**
 * Java 15+ Text Blocks 特性示例
 * Text Blocks 提供了一种更清晰、更易读的方式来处理多行字符串
 */
public class TextBlocksExample {

    // 基本 Text Block 示例
    public static String getBasicTextBlock() {
        return """
            Hello, World!
            这是一个多行字符串
            使用 Text Blocks 语法
            """;
    }

    // 包含引号的 Text Block
    public static String getTextWithQuotes() {
        return """
            She said "Hello, World!" and smiled.
            He replied, "Nice to meet you!"
            """;
    }

    // 包含转义字符的 Text Block
    public static String getTextWithEscapes() {
        return """
            Line 1
            Line 2\tTab character
            Line 3\nNew line character
            """;
    }

    // JSON 格式示例
    public static String getJsonExample() {
        return """
            {
                "name": "张三",
                "age": 25,
                "email": "zhangsan@example.com",
                "address": {
                    "street": "中关村大街1号",
                    "city": "北京",
                    "zipCode": "100000"
                },
                "hobbies": ["读书", "游泳", "编程"]
            }
            """;
    }

    // SQL 查询示例
    public static String getSqlQuery() {
        return """
            SELECT u.id, u.name, u.email, a.city
            FROM users u
            LEFT JOIN addresses a ON u.id = a.user_id
            WHERE u.age > ?
              AND u.status = 'ACTIVE'
            ORDER BY u.created_at DESC
            LIMIT 10
            """;
    }

    // HTML 模板示例
    public static String getHtmlTemplate(String title, String content) {
        return """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
            </head>
            <body>
                <header>
                    <h1>%s</h1>
                </header>
                <main>
                    <p>%s</p>
                </main>
                <footer>
                    <p>&copy; 2024 Example Company</p>
                </footer>
            </body>
            </html>
            """.formatted(title, title, content);
    }

    // 代码生成示例
    public static String generateJavaClass(String className, List<String> fields) {
        StringBuilder fieldsCode = new StringBuilder();
        for (String field : fields) {
            String[] parts = field.split(":");
            String fieldName = parts[0].trim();
            String fieldType = parts[1].trim();
            fieldsCode.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n");
        }

        return """
            package org.example.generated;
            
            public class %s {
            %s
                public %s() {
                }
                
                // Getters and setters would be generated here
            }
            """.formatted(className, fieldsCode.toString(), className);
    }

    // 配置文件示例
    public static String getConfigFile() {
        return """
            # 应用配置
            app.name=Java 21 Features Demo
            app.version=1.0.0
            app.debug=false
            
            # 数据库配置
            db.url=jdbc:mysql://localhost:3306/testdb
            db.username=root
            db.password=password
            db.pool.size=10
            
            # 日志配置
            logging.level=INFO
            logging.file=logs/app.log
            logging.pattern=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
            """;
    }

    // 正则表达式示例
    public static String getRegexPatterns() {
        return """
            # 常用正则表达式模式
            
            # 邮箱验证
            EMAIL_PATTERN: ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$
            
            # 手机号验证（中国）
            PHONE_PATTERN: ^1[3-9]\\d{9}$
            
            # 身份证号验证（中国）
            ID_CARD_PATTERN: ^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$
            
            # URL验证
            URL_PATTERN: ^https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?$
            """;
    }

    // 多语言文本示例
    public static String getMultiLanguageText() {
        return """
            # 多语言文本示例
            
            ## 中文
            欢迎使用 Java 21 新特性！
            
            ## English
            Welcome to Java 21 new features!
            
            ## 日本語
            Java 21の新機能へようこそ！
            
            ## 한국어
            Java 21의 새로운 기능에 오신 것을 환영합니다!
            
            ## Français
            Bienvenue aux nouvelles fonctionnalités de Java 21 !
            """;
    }

    // 演示方法
    public static void demonstrateTextBlocks() {
        System.out.println("=== Text Blocks 特性演示 ===");
        
        System.out.println("\n--- 基本 Text Block ---");
        System.out.println(getBasicTextBlock());
        
        System.out.println("\n--- 包含引号的文本 ---");
        System.out.println(getTextWithQuotes());
        
        System.out.println("\n--- 包含转义字符的文本 ---");
        System.out.println(getTextWithEscapes());
        
        System.out.println("\n--- JSON 示例 ---");
        System.out.println(getJsonExample());
        
        System.out.println("\n--- SQL 查询示例 ---");
        System.out.println(getSqlQuery());
        
        System.out.println("\n--- HTML 模板示例 ---");
        System.out.println(getHtmlTemplate("Java 21 特性", "这是一个展示 Text Blocks 特性的页面"));
        
        System.out.println("\n--- 代码生成示例 ---");
        List<String> fields = List.of("name:String", "age:int", "email:String");
        System.out.println(generateJavaClass("User", fields));
        
        System.out.println("\n--- 配置文件示例 ---");
        System.out.println(getConfigFile());
        
        System.out.println("\n--- 正则表达式示例 ---");
        System.out.println(getRegexPatterns());
        
        System.out.println("\n--- 多语言文本示例 ---");
        System.out.println(getMultiLanguageText());
    }

    // 比较传统字符串拼接和 Text Blocks
    public static void compareWithTraditionalString() {
        System.out.println("\n=== 传统字符串拼接 vs Text Blocks ===");
        
        // 传统方式
        String traditionalJson = "{\n" +
                "    \"name\": \"张三\",\n" +
                "    \"age\": 25,\n" +
                "    \"email\": \"zhangsan@example.com\"\n" +
                "}";
        
        // Text Blocks 方式
        String textBlockJson = """
            {
                "name": "张三",
                "age": 25,
                "email": "zhangsan@example.com"
            }
            """;
        
        System.out.println("传统方式:");
        System.out.println(traditionalJson);
        System.out.println("\nText Blocks 方式:");
        System.out.println(textBlockJson);
        System.out.println("\n两种方式结果相同: " + traditionalJson.equals(textBlockJson));
    }
}
