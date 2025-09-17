package org.example.features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Optional;

/**
 * Switch Expression 特性单元测试
 */
public class SwitchExpressionExampleTest {

    @Test
    @DisplayName("测试基本 Switch 表达式 - 工作日")
    void testGetDayTypeWeekday() {
        // Given
        DayOfWeek monday = DayOfWeek.MONDAY;
        DayOfWeek friday = DayOfWeek.FRIDAY;
        
        // When & Then
        assertThat(SwitchExpressionExample.getDayType(monday)).isEqualTo("工作日");
        assertThat(SwitchExpressionExample.getDayType(friday)).isEqualTo("工作日");
    }

    @Test
    @DisplayName("测试基本 Switch 表达式 - 周末")
    void testGetDayTypeWeekend() {
        // Given
        DayOfWeek saturday = DayOfWeek.SATURDAY;
        DayOfWeek sunday = DayOfWeek.SUNDAY;
        
        // When & Then
        assertThat(SwitchExpressionExample.getDayType(saturday)).isEqualTo("周末");
        assertThat(SwitchExpressionExample.getDayType(sunday)).isEqualTo("周末");
    }

    @Test
    @DisplayName("测试月份天数计算 - 31天月份")
    void testGetDaysInMonth31Days() {
        // Given
        Month january = Month.JANUARY;
        Month march = Month.MARCH;
        Month may = Month.MAY;
        Month july = Month.JULY;
        Month august = Month.AUGUST;
        Month october = Month.OCTOBER;
        Month december = Month.DECEMBER;
        
        // When & Then
        assertThat(SwitchExpressionExample.getDaysInMonth(january, false)).isEqualTo(31);
        assertThat(SwitchExpressionExample.getDaysInMonth(march, false)).isEqualTo(31);
        assertThat(SwitchExpressionExample.getDaysInMonth(may, false)).isEqualTo(31);
        assertThat(SwitchExpressionExample.getDaysInMonth(july, false)).isEqualTo(31);
        assertThat(SwitchExpressionExample.getDaysInMonth(august, false)).isEqualTo(31);
        assertThat(SwitchExpressionExample.getDaysInMonth(october, false)).isEqualTo(31);
        assertThat(SwitchExpressionExample.getDaysInMonth(december, false)).isEqualTo(31);
    }

    @Test
    @DisplayName("测试月份天数计算 - 30天月份")
    void testGetDaysInMonth30Days() {
        // Given
        Month april = Month.APRIL;
        Month june = Month.JUNE;
        Month september = Month.SEPTEMBER;
        Month november = Month.NOVEMBER;
        
        // When & Then
        assertThat(SwitchExpressionExample.getDaysInMonth(april, false)).isEqualTo(30);
        assertThat(SwitchExpressionExample.getDaysInMonth(june, false)).isEqualTo(30);
        assertThat(SwitchExpressionExample.getDaysInMonth(september, false)).isEqualTo(30);
        assertThat(SwitchExpressionExample.getDaysInMonth(november, false)).isEqualTo(30);
    }

    @Test
    @DisplayName("测试月份天数计算 - 二月")
    void testGetDaysInMonthFebruary() {
        // Given
        Month february = Month.FEBRUARY;
        
        // When & Then
        assertThat(SwitchExpressionExample.getDaysInMonth(february, false)).isEqualTo(28);
        assertThat(SwitchExpressionExample.getDaysInMonth(february, true)).isEqualTo(29);
    }

    @Test
    @DisplayName("测试成绩等级描述 - 优秀")
    void testGetGradeDescriptionExcellent() {
        // Given
        int score95 = 95;
        int score100 = 100;
        
        // When & Then
        assertThat(SwitchExpressionExample.getGradeDescription(score95))
                .contains("成绩: 优秀")
                .contains("分数: 95");
        assertThat(SwitchExpressionExample.getGradeDescription(score100))
                .contains("成绩: 优秀")
                .contains("分数: 100");
    }

    @Test
    @DisplayName("测试成绩等级描述 - 良好")
    void testGetGradeDescriptionGood() {
        // Given
        int score90 = 90;
        int score94 = 94;
        
        // When & Then
        assertThat(SwitchExpressionExample.getGradeDescription(score90))
                .contains("成绩: 良好")
                .contains("分数: 90");
        assertThat(SwitchExpressionExample.getGradeDescription(score94))
                .contains("成绩: 良好")
                .contains("分数: 94");
    }

    @Test
    @DisplayName("测试成绩等级描述 - 中等")
    void testGetGradeDescriptionAverage() {
        // Given
        int score80 = 80;
        int score89 = 89;
        
        // When & Then
        assertThat(SwitchExpressionExample.getGradeDescription(score80))
                .contains("成绩: 中等")
                .contains("分数: 80");
        assertThat(SwitchExpressionExample.getGradeDescription(score89))
                .contains("成绩: 中等")
                .contains("分数: 89");
    }

    @Test
    @DisplayName("测试成绩等级描述 - 及格")
    void testGetGradeDescriptionPass() {
        // Given
        int score70 = 70;
        int score79 = 79;
        
        // When & Then
        assertThat(SwitchExpressionExample.getGradeDescription(score70))
                .contains("成绩: 及格")
                .contains("分数: 70");
        assertThat(SwitchExpressionExample.getGradeDescription(score79))
                .contains("成绩: 及格")
                .contains("分数: 79");
    }

    @Test
    @DisplayName("测试成绩等级描述 - 勉强及格")
    void testGetGradeDescriptionBarelyPass() {
        // Given
        int score60 = 60;
        int score69 = 69;
        
        // When & Then
        assertThat(SwitchExpressionExample.getGradeDescription(score60))
                .contains("成绩: 勉强及格")
                .contains("分数: 60");
        assertThat(SwitchExpressionExample.getGradeDescription(score69))
                .contains("成绩: 勉强及格")
                .contains("分数: 69");
    }

    @Test
    @DisplayName("测试成绩等级描述 - 不及格")
    void testGetGradeDescriptionFail() {
        // Given
        int score50 = 50;
        int score0 = 0;
        
        // When & Then
        assertThat(SwitchExpressionExample.getGradeDescription(score50))
                .contains("成绩: 不及格")
                .contains("分数: 50");
        assertThat(SwitchExpressionExample.getGradeDescription(score0))
                .contains("成绩: 不及格")
                .contains("分数: 0");
    }

    @Test
    @DisplayName("测试处理空值 - null")
    void testHandleNullableStringNull() {
        // When
        String result = SwitchExpressionExample.handleNullableString(null);
        
        // Then
        assertThat(result).isEqualTo("输入为空");
    }

    @Test
    @DisplayName("测试处理空值 - 空字符串")
    void testHandleNullableStringEmpty() {
        // When
        String result = SwitchExpressionExample.handleNullableString("");
        
        // Then
        assertThat(result).isEqualTo("输入为空字符串");
    }

    @Test
    @DisplayName("测试处理空值 - 太短")
    void testHandleNullableStringTooShort() {
        // When
        String result = SwitchExpressionExample.handleNullableString("Hi");
        
        // Then
        assertThat(result).isEqualTo("输入太短");
    }

    @Test
    @DisplayName("测试处理空值 - 太长")
    void testHandleNullableStringTooLong() {
        // When
        String longString = "A".repeat(150);
        String result = SwitchExpressionExample.handleNullableString(longString);
        
        // Then
        assertThat(result).isEqualTo("输入太长");
    }

    @Test
    @DisplayName("测试处理空值 - 有效输入")
    void testHandleNullableStringValid() {
        // When
        String result = SwitchExpressionExample.handleNullableString("Hello World");
        
        // Then
        assertThat(result).isEqualTo("输入有效: Hello World");
    }

    @Test
    @DisplayName("测试状态消息")
    void testGetStatusMessage() {
        // When & Then
        assertThat(SwitchExpressionExample.getStatusMessage(SwitchExpressionExample.Status.PENDING))
                .isEqualTo("任务等待中...");
        assertThat(SwitchExpressionExample.getStatusMessage(SwitchExpressionExample.Status.PROCESSING))
                .isEqualTo("任务处理中...");
        assertThat(SwitchExpressionExample.getStatusMessage(SwitchExpressionExample.Status.COMPLETED))
                .isEqualTo("任务已完成");
        assertThat(SwitchExpressionExample.getStatusMessage(SwitchExpressionExample.Status.FAILED))
                .isEqualTo("任务执行失败");
        assertThat(SwitchExpressionExample.getStatusMessage(SwitchExpressionExample.Status.CANCELLED))
                .isEqualTo("任务已取消");
    }

    @Test
    @DisplayName("测试天气建议 - 寒冷天气")
    void testGetWeatherAdviceCold() {
        // When
        String result = SwitchExpressionExample.getWeatherAdvice(-5, false, 10);
        
        // Then
        assertThat(result).isEqualTo("天气很冷，注意保暖");
    }

    @Test
    @DisplayName("测试天气建议 - 凉爽天气")
    void testGetWeatherAdviceCool() {
        // When
        String result = SwitchExpressionExample.getWeatherAdvice(15, true, 5);
        
        // Then
        assertThat(result).isEqualTo("天气凉爽且下雨，带好雨具");
    }

    @Test
    @DisplayName("测试天气建议 - 温暖天气")
    void testGetWeatherAdviceWarm() {
        // When
        String result = SwitchExpressionExample.getWeatherAdvice(25, false, 25);
        
        // Then
        assertThat(result).isEqualTo("天气温暖但风大，注意防风");
    }

    @Test
    @DisplayName("测试天气建议 - 炎热天气")
    void testGetWeatherAdviceHot() {
        // When
        String result = SwitchExpressionExample.getWeatherAdvice(35, false, 5);
        
        // Then
        assertThat(result).isEqualTo("天气炎热，注意防暑降温");
    }

    @Test
    @DisplayName("测试处理 Optional - 有值")
    void testProcessOptionalWithValue() {
        // Given
        Optional<String> optional = Optional.of("Hello World");
        
        // When
        String result = SwitchExpressionExample.processOptional(optional);
        
        // Then
        assertThat(result).isEqualTo("长字符串: Hello World");
    }

    @Test
    @DisplayName("测试处理 Optional - 短字符串")
    void testProcessOptionalShortString() {
        // Given
        Optional<String> optional = Optional.of("Hi");
        
        // When
        String result = SwitchExpressionExample.processOptional(optional);
        
        // Then
        assertThat(result).isEqualTo("短字符串: Hi");
    }

    @Test
    @DisplayName("测试处理 Optional - 空值")
    void testProcessOptionalEmpty() {
        // Given
        Optional<String> optional = Optional.empty();
        
        // When
        String result = SwitchExpressionExample.processOptional(optional);
        
        // Then
        assertThat(result).isEqualTo("没有值");
    }

    @Test
    @DisplayName("测试类型判断 - 字符串")
    void testGetTypeDescriptionString() {
        // When
        String result = SwitchExpressionExample.getTypeDescription("Hello");
        
        // Then
        assertThat(result).isEqualTo("字符串: Hello");
    }

    @Test
    @DisplayName("测试类型判断 - 整数")
    void testGetTypeDescriptionInteger() {
        // When
        String result = SwitchExpressionExample.getTypeDescription(42);
        
        // Then
        assertThat(result).isEqualTo("整数: 42");
    }

    @Test
    @DisplayName("测试类型判断 - 浮点数")
    void testGetTypeDescriptionDouble() {
        // When
        String result = SwitchExpressionExample.getTypeDescription(3.14);
        
        // Then
        assertThat(result).isEqualTo("浮点数: 3.14");
    }

    @Test
    @DisplayName("测试类型判断 - 布尔值")
    void testGetTypeDescriptionBoolean() {
        // When
        String result = SwitchExpressionExample.getTypeDescription(true);
        
        // Then
        assertThat(result).isEqualTo("布尔值: true");
    }

    @Test
    @DisplayName("测试类型判断 - 列表")
    void testGetTypeDescriptionList() {
        // When
        String result = SwitchExpressionExample.getTypeDescription(List.of(1, 2, 3));
        
        // Then
        assertThat(result).isEqualTo("列表，大小: 3");
    }

    @Test
    @DisplayName("测试类型判断 - null")
    void testGetTypeDescriptionNull() {
        // When
        String result = SwitchExpressionExample.getTypeDescription(null);
        
        // Then
        assertThat(result).isEqualTo("空值");
    }

    @Test
    @DisplayName("测试计算器 - 加法")
    void testCalculateAddition() {
        // When
        double result = SwitchExpressionExample.calculate("+", 10, 5);
        
        // Then
        assertThat(result).isEqualTo(15.0);
    }

    @Test
    @DisplayName("测试计算器 - 减法")
    void testCalculateSubtraction() {
        // When
        double result = SwitchExpressionExample.calculate("-", 10, 5);
        
        // Then
        assertThat(result).isEqualTo(5.0);
    }

    @Test
    @DisplayName("测试计算器 - 乘法")
    void testCalculateMultiplication() {
        // When
        double result = SwitchExpressionExample.calculate("*", 10, 5);
        
        // Then
        assertThat(result).isEqualTo(50.0);
    }

    @Test
    @DisplayName("测试计算器 - 除法")
    void testCalculateDivision() {
        // When
        double result = SwitchExpressionExample.calculate("/", 10, 5);
        
        // Then
        assertThat(result).isEqualTo(2.0);
    }

    @Test
    @DisplayName("测试计算器 - 除法除零")
    void testCalculateDivisionByZero() {
        // When & Then
        assertThatThrownBy(() -> SwitchExpressionExample.calculate("/", 10, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("除数不能为零");
    }

    @Test
    @DisplayName("测试计算器 - 取模")
    void testCalculateModulo() {
        // When
        double result = SwitchExpressionExample.calculate("%", 10, 3);
        
        // Then
        assertThat(result).isEqualTo(1.0);
    }

    @Test
    @DisplayName("测试计算器 - 取模除零")
    void testCalculateModuloByZero() {
        // When & Then
        assertThatThrownBy(() -> SwitchExpressionExample.calculate("%", 10, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("除数不能为零");
    }

    @Test
    @DisplayName("测试计算器 - 幂运算")
    void testCalculatePower() {
        // When
        double result = SwitchExpressionExample.calculate("^", 2, 3);
        
        // Then
        assertThat(result).isEqualTo(8.0);
    }

    @Test
    @DisplayName("测试计算器 - 不支持的运算符")
    void testCalculateUnsupportedOperator() {
        // When & Then
        assertThatThrownBy(() -> SwitchExpressionExample.calculate("&", 10, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("不支持的运算符: &");
    }
}
