package org.example;

import org.example.features.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Java 21 特性测试套件
 * 运行所有 Java 21 特性的单元测试
 */
@Suite
@SelectClasses({
    org.example.features.RecordExampleTest.class,
    org.example.features.PatternMatchingExampleTest.class,
    org.example.features.TextBlocksExampleTest.class,
    org.example.features.SwitchExpressionExampleTest.class,
    org.example.features.VirtualThreadsExampleTest.class
})
@DisplayName("Java 21 特性完整测试套件")
public class Java21FeaturesTestSuite {
    // 测试套件类，不需要实现任何方法
    // JUnit 5 会自动发现并运行所有选中的测试类
}
