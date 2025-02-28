package com.ydsw.pojo.excel;

import com.ydsw.domain.TbWaterLevel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author zhangkaifei
 * @description: 附6.陆浑水库水位—模板,上传解析bean
 * @date 2024/7/24  19:50
 * @Version 1.0
 */

public class WaterExcel {
    private String day;
    private String jan;
    private String feb;
    private String mar;
    private String apr;
    private String may;
    private String june;
    private String july;
    private String aug;
    private String sept;
    private String oct;
    private String nov;
    private String dec;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getFeb() {
        return feb;
    }

    public void setFeb(String feb) {
        this.feb = feb;
    }

    public String getMar() {
        return mar;
    }

    public void setMar(String mar) {
        this.mar = mar;
    }

    public String getApr() {
        return apr;
    }

    public void setApr(String apr) {
        this.apr = apr;
    }

    public String getMay() {
        return may;
    }

    public void setMay(String may) {
        this.may = may;
    }

    public String getJune() {
        return june;
    }

    public void setJune(String june) {
        this.june = june;
    }

    public String getJuly() {
        return july;
    }

    public void setJuly(String july) {
        this.july = july;
    }

    public String getAug() {
        return aug;
    }

    public void setAug(String aug) {
        this.aug = aug;
    }

    public String getSept() {
        return sept;
    }

    public void setSept(String sept) {
        this.sept = sept;
    }

    public String getOct() {
        return oct;
    }

    public void setOct(String oct) {
        this.oct = oct;
    }

    public String getNov() {
        return nov;
    }

    public void setNov(String nov) {
        this.nov = nov;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    @Override
    public String toString() {
        return "WaterExcel{" +
                "day='" + day + '\'' +
                ", jan='" + jan + '\'' +
                ", feb='" + feb + '\'' +
                ", mar='" + mar + '\'' +
                ", apr='" + apr + '\'' +
                ", may='" + may + '\'' +
                ", june='" + june + '\'' +
                ", july='" + july + '\'' +
                ", aug='" + aug + '\'' +
                ", sept='" + sept + '\'' +
                ", oct='" + oct + '\'' +
                ", nov='" + nov + '\'' +
                ", dec='" + dec + '\'' +
                '}';
    }

    public void putInArray(String[] Result)
    {
        Result[0]=this.jan;
        Result[1]=this.feb;
        Result[2]=this.mar;
        Result[3]=this.apr;
        Result[4]=this.may;
        Result[5]=this.june;
        Result[6]=this.july;
        Result[7]=this.aug;
        Result[8]=this.sept;
        Result[9]=this.oct;
        Result[10]=this.nov;
        Result[11]=this.dec;
    }

    public void ExcelToTb(String[] Result,List<TbWaterLevel> tbWaterLevelList,String ResultTime,String userName, UUID uid,String filePath)
    {
        for (int i = 0; i < 12; i++) {
            TbWaterLevel tbWaterLevel = new TbWaterLevel();
            tbWaterLevel.setCreateTime(new Date());
            tbWaterLevel.setStatus(1);
            tbWaterLevel.setType("Excel");
            tbWaterLevel.setFilepath(filePath);
            tbWaterLevel.setWaterLevel(Result[i]);
            tbWaterLevel.setUserName(userName);
            tbWaterLevel.setObservationTime(ResultTime);
            tbWaterLevelList.add(tbWaterLevel);
        }
    }
}
