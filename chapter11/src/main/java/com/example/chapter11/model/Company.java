package com.example.chapter11.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 公司实体类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {
    
    private String name;
    private String catchPhrase;
    private String bs;

    // 默认构造函数
    public Company() {}

    // 带参数的构造函数
    public Company(String name, String catchPhrase, String bs) {
        this.name = name;
        this.catchPhrase = catchPhrase;
        this.bs = bs;
    }

    // Getter和Setter方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatchPhrase() {
        return catchPhrase;
    }

    public void setCatchPhrase(String catchPhrase) {
        this.catchPhrase = catchPhrase;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", catchPhrase='" + catchPhrase + '\'' +
                ", bs='" + bs + '\'' +
                '}';
    }
}
