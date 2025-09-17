#!/bin/bash

echo "=== Java 21 Features Demo 运行脚本 ==="
echo ""

# 检查Java版本
echo "1. 检查Java版本："
java -version
echo ""

# 检查javac版本
echo "2. 检查javac版本："
javac -version
echo ""

# 使用Maven编译
echo "3. 使用Maven编译项目："
mvn clean compile -q
echo ""

# 使用Maven运行演示程序
echo "4. 使用Maven运行演示程序："
mvn exec:java -Dexec.mainClass="org.example.Java21FeaturesDemo" -q
echo ""

echo "=== 运行完成 ==="
