package org.example;

import org.example.features.*;

/**
 * Java 21 特性演示主类
 * 展示 Java 21 中的主要新特性
 */
public class Java21FeaturesDemo {
    
    public static void main(String[] args) {
        System.out.println("🚀 Java 21 新特性演示程序");
        System.out.println("=" .repeat(50));
        
        try {
            // 1. Record 特性演示
            System.out.println("\n📋 1. Record 特性演示");
            System.out.println("-".repeat(30));
            RecordExample.demonstrateRecords();
            
            // 2. Pattern Matching 特性演示
            System.out.println("\n🔍 2. Pattern Matching 特性演示");
            System.out.println("-".repeat(30));
            PatternMatchingExample.demonstratePatternMatching();
            
            // 3. Text Blocks 特性演示
            System.out.println("\n📝 3. Text Blocks 特性演示");
            System.out.println("-".repeat(30));
            TextBlocksExample.demonstrateTextBlocks();
            TextBlocksExample.compareWithTraditionalString();
            
            // 4. Switch Expression 特性演示
            System.out.println("\n🔄 4. Switch Expression 特性演示");
            System.out.println("-".repeat(30));
            SwitchExpressionExample.demonstrateSwitchExpressions();
            SwitchExpressionExample.compareWithTraditionalSwitch();
            
            // 5. Virtual Threads 特性演示
            System.out.println("\n🧵 5. Virtual Threads 特性演示");
            System.out.println("-".repeat(30));
            VirtualThreadsExample.demonstrateVirtualThreads();
            VirtualThreadsExample.virtualThreadBestPractices();
            
            System.out.println("\n✅ 所有 Java 21 特性演示完成！");
            System.out.println("=" .repeat(50));
            
        } catch (Exception e) {
            System.err.println("❌ 演示过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
