package com.ydsw.pojo.excel;

public class FlowExcel {

    // 日期字段（天数）
    private String day;

    // 存储不同站点的流量数据
    private String data1;
    private String data2;

    // 获取日期字段
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    // 获取流量数据列表
    public String getData1() {return data1;}
    public void setData1(String data1) {this.data1 = data1;}
    public String getData2() {return data2;}
    public void setData2(String data2) {this.data2 = data2;}
    public FlowExcel(){};
    public FlowExcel(String day, String data1,String data2) {
        this.day = day;
        this.data1=data1;
        this.data2=data2;
    }

    @Override
    public String toString() {
        return "FlowExcel{" +
                "day='" + day + '\'' +
                ", data1='" + data1 + '\'' +
                ", data2='" + data2 + '\'' +
                '}';
    }
}
