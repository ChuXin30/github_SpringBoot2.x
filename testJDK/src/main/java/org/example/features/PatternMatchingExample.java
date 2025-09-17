package org.example.features;

import java.util.List;
import java.util.Optional;

/**
 * Java 21 Pattern Matching 特性示例
 * 包括 instanceof 模式匹配、switch 模式匹配等
 */
public class PatternMatchingExample {

    // 基础类型层次结构
    public sealed interface Shape permits Circle, Rectangle, Triangle {
        double area();
    }

    public record Circle(double radius) implements Shape {
        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    public record Rectangle(double width, double height) implements Shape {
        @Override
        public double area() {
            return width * height;
        }
    }

    public record Triangle(double base, double height) implements Shape {
        @Override
        public double area() {
            return 0.5 * base * height;
        }
    }

    // instanceof 模式匹配示例 (Java 16+)
    public static String describeShapeOldWay(Object shape) {
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            return "圆形，半径: " + circle.radius();
        } else if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            return "矩形，宽: " + rectangle.width() + ", 高: " + rectangle.height();
        } else if (shape instanceof Triangle) {
            Triangle triangle = (Triangle) shape;
            return "三角形，底: " + triangle.base() + ", 高: " + triangle.height();
        }
        return "未知形状";
    }

    public static String describeShapeNewWay(Object shape) {
        if (shape instanceof Circle circle) {
            return "圆形，半径: " + circle.radius();
        } else if (shape instanceof Rectangle rectangle) {
            return "矩形，宽: " + rectangle.width() + ", 高: " + rectangle.height();
        } else if (shape instanceof Triangle triangle) {
            return "三角形，底: " + triangle.base() + ", 高: " + triangle.height();
        }
        return "未知形状";
    }

    // Switch 模式匹配示例 (Java 17+)
    public static String getShapeType(Shape shape) {
        return switch (shape) {
            case Circle c -> "圆形";
            case Rectangle r -> "矩形";
            case Triangle t -> "三角形";
        };
    }

    // 带条件的模式匹配
    public static String getShapeDescription(Shape shape) {
        return switch (shape) {
            case Circle c when c.radius() > 10 -> "大圆形";
            case Circle c -> "小圆形";
            case Rectangle r when r.width() == r.height() -> "正方形";
            case Rectangle r -> "矩形";
            case Triangle t when t.base() > 10 -> "大三角形";
            case Triangle t -> "小三角形";
        };
    }

    // 嵌套模式匹配
    public static String processOptionalShape(Optional<Shape> optionalShape) {
        if (optionalShape.isEmpty()) {
            return "没有形状";
        }
        
        Shape shape = optionalShape.get();
        if (shape instanceof Circle circle) {
            return circle.radius() > 5 ? "大圆形" : "小圆形";
        } else if (shape instanceof Rectangle rectangle) {
            return "矩形";
        } else if (shape instanceof Triangle triangle) {
            return "三角形";
        }
        return "未知形状";
    }

    // 记录模式匹配 (Java 19+)
    public static String analyzeShape(Shape shape) {
        return switch (shape) {
            case Circle(var radius) when radius > 10 -> "大圆形，半径: " + radius;
            case Circle(var radius) -> "小圆形，半径: " + radius;
            case Rectangle(var width, var height) when width == height -> 
                "正方形，边长: " + width;
            case Rectangle(var width, var height) -> 
                "矩形，宽: " + width + ", 高: " + height;
            case Triangle(var base, var height) -> 
                "三角形，底: " + base + ", 高: " + height;
        };
    }

    // 数组模式匹配 (Java 20+)
    public static String analyzeArray(Object array) {
        return switch (array) {
            case int[] ints when ints.length == 0 -> "空整数数组";
            case int[] ints when ints.length == 1 -> "单元素整数数组: " + ints[0];
            case int[] ints -> "整数数组，长度: " + ints.length;
            case String[] strings when strings.length == 0 -> "空字符串数组";
            case String[] strings -> "字符串数组，长度: " + strings.length;
            case null -> "空数组";
            default -> "其他类型数组";
        };
    }

    // 演示方法
    public static void demonstratePatternMatching() {
        System.out.println("=== Pattern Matching 特性演示 ===");
        
        // 创建不同类型的形状
        List<Shape> shapes = List.of(
            new Circle(5.0),
            new Circle(15.0),
            new Rectangle(10.0, 20.0),
            new Rectangle(10.0, 10.0),
            new Triangle(8.0, 6.0)
        );

        System.out.println("\n--- instanceof 模式匹配 ---");
        for (Shape shape : shapes) {
            System.out.println("旧方式: " + describeShapeOldWay(shape));
            System.out.println("新方式: " + describeShapeNewWay(shape));
        }

        System.out.println("\n--- Switch 模式匹配 ---");
        for (Shape shape : shapes) {
            System.out.println("形状类型: " + getShapeType(shape));
            System.out.println("形状描述: " + getShapeDescription(shape));
            System.out.println("形状分析: " + analyzeShape(shape));
        }

        System.out.println("\n--- Optional 模式匹配 ---");
        List<Optional<Shape>> optionalShapes = List.of(
            Optional.of(new Circle(3.0)),
            Optional.of(new Circle(8.0)),
            Optional.of(new Rectangle(5.0, 5.0)),
            Optional.empty()
        );

        for (Optional<Shape> optionalShape : optionalShapes) {
            System.out.println("Optional 形状: " + processOptionalShape(optionalShape));
        }

        System.out.println("\n--- 数组模式匹配 ---");
        Object[] arrays = {
            new int[]{},
            new int[]{42},
            new int[]{1, 2, 3, 4, 5},
            new String[]{"Hello", "World"},
            new String[]{},
            null
        };

        for (Object array : arrays) {
            System.out.println("数组分析: " + analyzeArray(array));
        }
    }
}
