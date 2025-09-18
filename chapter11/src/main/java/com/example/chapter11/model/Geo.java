package com.example.chapter11.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 地理位置实体类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Geo {
    
    private String lat;
    private String lng;

    // 默认构造函数
    public Geo() {}

    // 带参数的构造函数
    public Geo(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    // Getter和Setter方法
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Geo{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
