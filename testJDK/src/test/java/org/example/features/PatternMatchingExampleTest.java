package org.example.features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

/**
 * Pattern Matching 特性单元测试
 */
public class PatternMatchingExampleTest {

    @Test
    @DisplayName("测试 instanceof 模式匹配 - 圆形")
    void testInstanceofPatternMatchingCircle() {
        // Given
        PatternMatchingExample.Circle circle = new PatternMatchingExample.Circle(5.0);
        
        // When
        String description = PatternMatchingExample.describeShapeNewWay(circle);
        
        // Then
        assertThat(description).isEqualTo("圆形，半径: 5.0");
    }

    @Test
    @DisplayName("测试 instanceof 模式匹配 - 矩形")
    void testInstanceofPatternMatchingRectangle() {
        // Given
        PatternMatchingExample.Rectangle rectangle = new PatternMatchingExample.Rectangle(10.0, 20.0);
        
        // When
        String description = PatternMatchingExample.describeShapeNewWay(rectangle);
        
        // Then
        assertThat(description).isEqualTo("矩形，宽: 10.0, 高: 20.0");
    }

    @Test
    @DisplayName("测试 instanceof 模式匹配 - 三角形")
    void testInstanceofPatternMatchingTriangle() {
        // Given
        PatternMatchingExample.Triangle triangle = new PatternMatchingExample.Triangle(8.0, 6.0);
        
        // When
        String description = PatternMatchingExample.describeShapeNewWay(triangle);
        
        // Then
        assertThat(description).isEqualTo("三角形，底: 8.0, 高: 6.0");
    }

    @Test
    @DisplayName("测试 instanceof 模式匹配 - 未知形状")
    void testInstanceofPatternMatchingUnknownShape() {
        // Given
        String unknownShape = "unknown";
        
        // When
        String description = PatternMatchingExample.describeShapeNewWay(unknownShape);
        
        // Then
        assertThat(description).isEqualTo("未知形状");
    }

    @Test
    @DisplayName("测试 Switch 模式匹配 - 获取形状类型")
    void testSwitchPatternMatchingGetShapeType() {
        // Given
        PatternMatchingExample.Circle circle = new PatternMatchingExample.Circle(5.0);
        PatternMatchingExample.Rectangle rectangle = new PatternMatchingExample.Rectangle(10.0, 20.0);
        PatternMatchingExample.Triangle triangle = new PatternMatchingExample.Triangle(8.0, 6.0);
        
        // When & Then
        assertThat(PatternMatchingExample.getShapeType(circle)).isEqualTo("圆形");
        assertThat(PatternMatchingExample.getShapeType(rectangle)).isEqualTo("矩形");
        assertThat(PatternMatchingExample.getShapeType(triangle)).isEqualTo("三角形");
    }

    @Test
    @DisplayName("测试带条件的模式匹配 - 大圆形")
    void testConditionalPatternMatchingLargeCircle() {
        // Given
        PatternMatchingExample.Circle largeCircle = new PatternMatchingExample.Circle(15.0);
        
        // When
        String description = PatternMatchingExample.getShapeDescription(largeCircle);
        
        // Then
        assertThat(description).isEqualTo("大圆形");
    }

    @Test
    @DisplayName("测试带条件的模式匹配 - 小圆形")
    void testConditionalPatternMatchingSmallCircle() {
        // Given
        PatternMatchingExample.Circle smallCircle = new PatternMatchingExample.Circle(5.0);
        
        // When
        String description = PatternMatchingExample.getShapeDescription(smallCircle);
        
        // Then
        assertThat(description).isEqualTo("小圆形");
    }

    @Test
    @DisplayName("测试带条件的模式匹配 - 正方形")
    void testConditionalPatternMatchingSquare() {
        // Given
        PatternMatchingExample.Rectangle square = new PatternMatchingExample.Rectangle(10.0, 10.0);
        
        // When
        String description = PatternMatchingExample.getShapeDescription(square);
        
        // Then
        assertThat(description).isEqualTo("正方形");
    }

    @Test
    @DisplayName("测试带条件的模式匹配 - 普通矩形")
    void testConditionalPatternMatchingRectangle() {
        // Given
        PatternMatchingExample.Rectangle rectangle = new PatternMatchingExample.Rectangle(10.0, 20.0);
        
        // When
        String description = PatternMatchingExample.getShapeDescription(rectangle);
        
        // Then
        assertThat(description).isEqualTo("矩形");
    }

    @Test
    @DisplayName("测试 Optional 模式匹配 - 有值")
    void testOptionalPatternMatchingWithValue() {
        // Given
        Optional<PatternMatchingExample.Shape> optionalShape = Optional.of(new PatternMatchingExample.Circle(8.0));
        
        // When
        String result = PatternMatchingExample.processOptionalShape(optionalShape);
        
        // Then
        assertThat(result).isEqualTo("大圆形");
    }

    @Test
    @DisplayName("测试 Optional 模式匹配 - 小圆形")
    void testOptionalPatternMatchingSmallCircle() {
        // Given
        Optional<PatternMatchingExample.Shape> optionalShape = Optional.of(new PatternMatchingExample.Circle(3.0));
        
        // When
        String result = PatternMatchingExample.processOptionalShape(optionalShape);
        
        // Then
        assertThat(result).isEqualTo("小圆形");
    }

    @Test
    @DisplayName("测试 Optional 模式匹配 - 空值")
    void testOptionalPatternMatchingEmpty() {
        // Given
        Optional<PatternMatchingExample.Shape> optionalShape = Optional.empty();
        
        // When
        String result = PatternMatchingExample.processOptionalShape(optionalShape);
        
        // Then
        assertThat(result).isEqualTo("没有形状");
    }

    @Test
    @DisplayName("测试记录模式匹配 - 圆形分析")
    void testRecordPatternMatchingCircle() {
        // Given
        PatternMatchingExample.Circle circle = new PatternMatchingExample.Circle(15.0);
        
        // When
        String analysis = PatternMatchingExample.analyzeShape(circle);
        
        // Then
        assertThat(analysis).isEqualTo("大圆形，半径: 15.0");
    }

    @Test
    @DisplayName("测试记录模式匹配 - 正方形分析")
    void testRecordPatternMatchingSquare() {
        // Given
        PatternMatchingExample.Rectangle square = new PatternMatchingExample.Rectangle(10.0, 10.0);
        
        // When
        String analysis = PatternMatchingExample.analyzeShape(square);
        
        // Then
        assertThat(analysis).isEqualTo("正方形，边长: 10.0");
    }

    @Test
    @DisplayName("测试记录模式匹配 - 矩形分析")
    void testRecordPatternMatchingRectangle() {
        // Given
        PatternMatchingExample.Rectangle rectangle = new PatternMatchingExample.Rectangle(10.0, 20.0);
        
        // When
        String analysis = PatternMatchingExample.analyzeShape(rectangle);
        
        // Then
        assertThat(analysis).isEqualTo("矩形，宽: 10.0, 高: 20.0");
    }

    @Test
    @DisplayName("测试数组模式匹配 - 空整数数组")
    void testArrayPatternMatchingEmptyIntArray() {
        // Given
        int[] emptyArray = {};
        
        // When
        String analysis = PatternMatchingExample.analyzeArray(emptyArray);
        
        // Then
        assertThat(analysis).isEqualTo("空整数数组");
    }

    @Test
    @DisplayName("测试数组模式匹配 - 单元素整数数组")
    void testArrayPatternMatchingSingleElementIntArray() {
        // Given
        int[] singleElementArray = {42};
        
        // When
        String analysis = PatternMatchingExample.analyzeArray(singleElementArray);
        
        // Then
        assertThat(analysis).isEqualTo("单元素整数数组: 42");
    }

    @Test
    @DisplayName("测试数组模式匹配 - 多元素整数数组")
    void testArrayPatternMatchingMultiElementIntArray() {
        // Given
        int[] multiElementArray = {1, 2, 3, 4, 5};
        
        // When
        String analysis = PatternMatchingExample.analyzeArray(multiElementArray);
        
        // Then
        assertThat(analysis).isEqualTo("整数数组，长度: 5");
    }

    @Test
    @DisplayName("测试数组模式匹配 - 字符串数组")
    void testArrayPatternMatchingStringArray() {
        // Given
        String[] stringArray = {"Hello", "World"};
        
        // When
        String analysis = PatternMatchingExample.analyzeArray(stringArray);
        
        // Then
        assertThat(analysis).isEqualTo("字符串数组，长度: 2");
    }

    @Test
    @DisplayName("测试数组模式匹配 - 空字符串数组")
    void testArrayPatternMatchingEmptyStringArray() {
        // Given
        String[] emptyStringArray = {};
        
        // When
        String analysis = PatternMatchingExample.analyzeArray(emptyStringArray);
        
        // Then
        assertThat(analysis).isEqualTo("空字符串数组");
    }

    @Test
    @DisplayName("测试数组模式匹配 - null 数组")
    void testArrayPatternMatchingNullArray() {
        // Given
        Object nullArray = null;
        
        // When
        String analysis = PatternMatchingExample.analyzeArray(nullArray);
        
        // Then
        assertThat(analysis).isEqualTo("空数组");
    }

    @Test
    @DisplayName("测试形状面积计算")
    void testShapeAreaCalculation() {
        // Given
        PatternMatchingExample.Circle circle = new PatternMatchingExample.Circle(5.0);
        PatternMatchingExample.Rectangle rectangle = new PatternMatchingExample.Rectangle(10.0, 20.0);
        PatternMatchingExample.Triangle triangle = new PatternMatchingExample.Triangle(8.0, 6.0);
        
        // When & Then
        assertThat(circle.area()).isCloseTo(78.54, within(0.01));
        assertThat(rectangle.area()).isEqualTo(200.0);
        assertThat(triangle.area()).isEqualTo(24.0);
    }
}
