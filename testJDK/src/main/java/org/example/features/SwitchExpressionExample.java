package org.example.features;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Optional;

/**
 * Java 14+ Switch Expression 特性示例
 * Switch 表达式提供了更简洁、更安全的 switch 语句语法
 */
public class SwitchExpressionExample {

    // 基本 Switch 表达式示例
    public static String getDayType(DayOfWeek day) {
        return switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
            case SATURDAY, SUNDAY -> "周末";
        };
    }

    // 带返回值的 Switch 表达式
    public static int getDaysInMonth(Month month, boolean isLeapYear) {
        return switch (month) {
            case JANUARY, MARCH, MAY, JULY, AUGUST, OCTOBER, DECEMBER -> 31;
            case APRIL, JUNE, SEPTEMBER, NOVEMBER -> 30;
            case FEBRUARY -> isLeapYear ? 29 : 28;
        };
    }

    // 使用 yield 关键字的 Switch 表达式
    public static String getGradeDescription(int score) {
        return switch (score / 10) {
            case 10, 9 -> {
                String grade = score >= 95 ? "优秀" : "良好";
                yield "成绩: " + grade + " (分数: " + score + ")";
            }
            case 8 -> {
                yield "成绩: 中等 (分数: " + score + ")";
            }
            case 7 -> {
                yield "成绩: 及格 (分数: " + score + ")";
            }
            case 6 -> {
                yield "成绩: 勉强及格 (分数: " + score + ")";
            }
            default -> {
                yield "成绩: 不及格 (分数: " + score + ")";
            }
        };
    }

    // 处理 null 值的 Switch 表达式
    public static String handleNullableString(String input) {
        return switch (input) {
            case null -> "输入为空";
            case "" -> "输入为空字符串";
            case String s when s.length() < 3 -> "输入太短";
            case String s when s.length() > 100 -> "输入太长";
            default -> "输入有效: " + input;
        };
    }

    // 枚举 Switch 表达式
    public enum Status {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
    }

    public static String getStatusMessage(Status status) {
        return switch (status) {
            case PENDING -> "任务等待中...";
            case PROCESSING -> "任务处理中...";
            case COMPLETED -> "任务已完成";
            case FAILED -> "任务执行失败";
            case CANCELLED -> "任务已取消";
        };
    }

    // 复杂条件 Switch 表达式
    public static String getWeatherAdvice(int temperature, boolean isRaining, int windSpeed) {
        if (temperature < 0) {
            return "天气很冷，注意保暖";
        } else if (temperature < 10) {
            return isRaining ? "天气寒冷且下雨，建议室内活动" : "天气较冷，注意添衣";
        } else if (temperature < 20) {
            return isRaining ? "天气凉爽且下雨，带好雨具" : "天气凉爽，适合外出";
        } else if (temperature < 30) {
            if (isRaining) {
                return "天气温暖且下雨，带好雨具";
            } else if (windSpeed > 20) {
                return "天气温暖但风大，注意防风";
            } else {
                return "天气温暖，适合户外活动";
            }
        } else if (temperature < 40) {
            return "天气炎热，注意防暑降温";
        } else {
            return "天气极热，避免户外活动";
        }
    }

    // 处理 Optional 的 Switch 表达式
    public static String processOptional(Optional<String> optional) {
        if (optional.isEmpty()) {
            return "没有值";
        }
        
        String value = optional.get();
        return value.length() > 5 ? "长字符串: " + value : "短字符串: " + value;
    }

    // 类型判断 Switch 表达式
    public static String getTypeDescription(Object obj) {
        return switch (obj) {
            case String s -> "字符串: " + s;
            case Integer i -> "整数: " + i;
            case Double d -> "浮点数: " + d;
            case Boolean b -> "布尔值: " + b;
            case List<?> list -> "列表，大小: " + list.size();
            case null -> "空值";
            default -> "其他类型: " + obj.getClass().getSimpleName();
        };
    }

    // 嵌套 Switch 表达式
    public static String getComplexDescription(Object obj1, Object obj2) {
        return switch (obj1) {
            case String s1 -> switch (obj2) {
                case String s2 -> "两个字符串: " + s1 + " 和 " + s2;
                case Integer i2 -> "字符串和整数: " + s1 + " 和 " + i2;
                default -> "第一个是字符串，第二个是其他类型";
            };
            case Integer i1 -> switch (obj2) {
                case String s2 -> "整数和字符串: " + i1 + " 和 " + s2;
                case Integer i2 -> "两个整数: " + i1 + " 和 " + i2;
                default -> "第一个是整数，第二个是其他类型";
            };
            default -> "第一个参数是其他类型";
        };
    }

    // 计算器示例
    public static double calculate(String operator, double a, double b) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) {
                    throw new IllegalArgumentException("除数不能为零");
                }
                yield a / b;
            }
            case "%" -> {
                if (b == 0) {
                    throw new IllegalArgumentException("除数不能为零");
                }
                yield a % b;
            }
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("不支持的运算符: " + operator);
        };
    }

    // 演示方法
    public static void demonstrateSwitchExpressions() {
        System.out.println("=== Switch Expression 特性演示 ===");
        
        System.out.println("\n--- 基本 Switch 表达式 ---");
        for (DayOfWeek day : DayOfWeek.values()) {
            System.out.println(day + ": " + getDayType(day));
        }
        
        System.out.println("\n--- 月份天数计算 ---");
        for (Month month : Month.values()) {
            int days = getDaysInMonth(month, true); // 闰年
            System.out.println(month + " (闰年): " + days + " 天");
        }
        
        System.out.println("\n--- 成绩等级 ---");
        int[] scores = {95, 85, 75, 65, 55, 45};
        for (int score : scores) {
            System.out.println(getGradeDescription(score));
        }
        
        System.out.println("\n--- 处理空值 ---");
        String[] inputs = {null, "", "Hi", "Hello World", "A".repeat(150)};
        for (String input : inputs) {
            System.out.println(handleNullableString(input));
        }
        
        System.out.println("\n--- 状态消息 ---");
        for (Status status : Status.values()) {
            System.out.println(status + ": " + getStatusMessage(status));
        }
        
        System.out.println("\n--- 天气建议 ---");
        System.out.println(getWeatherAdvice(-5, false, 10));
        System.out.println(getWeatherAdvice(15, true, 5));
        System.out.println(getWeatherAdvice(25, false, 25));
        System.out.println(getWeatherAdvice(35, false, 5));
        
        System.out.println("\n--- Optional 处理 ---");
        List<Optional<String>> optionals = List.of(
            Optional.of("Hello"),
            Optional.of("Hi"),
            Optional.empty()
        );
        for (Optional<String> optional : optionals) {
            System.out.println(processOptional(optional));
        }
        
        System.out.println("\n--- 类型判断 ---");
        Object[] objects = {"Hello", 42, 3.14, true, List.of(1, 2, 3), null};
        for (Object obj : objects) {
            System.out.println(getTypeDescription(obj));
        }
        
        System.out.println("\n--- 嵌套 Switch ---");
        System.out.println(getComplexDescription("Hello", "World"));
        System.out.println(getComplexDescription("Hello", 42));
        System.out.println(getComplexDescription(10, 20));
        
        System.out.println("\n--- 计算器示例 ---");
        String[] operators = {"+", "-", "*", "/", "%", "^"};
        double a = 10, b = 3;
        for (String op : operators) {
            try {
                double result = calculate(op, a, b);
                System.out.println(a + " " + op + " " + b + " = " + result);
            } catch (IllegalArgumentException e) {
                System.out.println("错误: " + e.getMessage());
            }
        }
    }

    // 比较传统 switch 和 switch 表达式
    public static void compareWithTraditionalSwitch() {
        System.out.println("\n=== 传统 Switch vs Switch 表达式 ===");
        
        DayOfWeek day = DayOfWeek.MONDAY;
        
        // 传统方式
        String traditionalResult;
        switch (day) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                traditionalResult = "工作日";
                break;
            case SATURDAY:
            case SUNDAY:
                traditionalResult = "周末";
                break;
            default:
                traditionalResult = "未知";
        }
        
        // Switch 表达式方式
        String expressionResult = switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
            case SATURDAY, SUNDAY -> "周末";
        };
        
        System.out.println("传统方式结果: " + traditionalResult);
        System.out.println("Switch 表达式结果: " + expressionResult);
        System.out.println("结果相同: " + traditionalResult.equals(expressionResult));
    }
}
