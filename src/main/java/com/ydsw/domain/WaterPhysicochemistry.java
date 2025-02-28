package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 水体理化表

 * @TableName water_physicochemistry
 */
@TableName(value ="water_physicochemistry")
@Data
public class WaterPhysicochemistry implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 设备表外键id

     */
    @TableField(value = "device_id")
    private String deviceId;

    /**
     * 水温℃
     */
    @TableField(value = "water_temperature")
    private String waterTemperature;

    /**
     * pH
     */
    @TableField(value = "ph")
    private String ph;

    /**
     * 浊度 mg/L
     */
    @TableField(value = "turbidity")
    private String turbidity;

    /**
     * 电导率 mg/L
     */
    @TableField(value = "conductivity")
    private String conductivity;

    /**
     * 溶解氧 mg/L
     */
    @TableField(value = "dissolved_oxygen")
    private String dissolvedOxygen;

    /**
     * 透明度 m
     */
    @TableField(value = "transparency")
    private String transparency;

    /**
     * 高锰酸盐指数 mg/L
     */
    @TableField(value = "codmn")
    private String codmn;

    /**
     * TSS mg/L
     */
    @TableField(value = "tss")
    private String tss;

    /**
     * TN mg/L
     */
    @TableField(value = "tn")
    private String tn;

    /**
     * TP mg/L
     */
    @TableField(value = "tp")
    private String tp;

    /**
     * 叶绿素 µg/L
     */
    @TableField(value = "chlorophyll")
    private String chlorophyll;

    /**
     * 实际数据观测时间
     */
    @TableField(value = "observation_time")
    private String observationTime;

    /**
     * 数据入库时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 上传文件类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 提交人用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 文件路径
     */
    @TableField(value = "filepath")
    private String filepath;

    /**
     * 文件名
     */
    @TableField(value = "filename")
    private String filename;

    /**
     * 提交用户id
     */
    @TableField(value = "create_userid")
    private String createUserid;

    /**
     *
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 联系地址
     */
    @TableField(value = "contact_address")
    private String contactAddress;

    /**
     * 制作单位
     */
    @TableField(value = "production_unit")
    private String productionUnit;

    /**
     * 联系邮箱
     */
    @TableField(value = "contact_email")
    private String contactEmail;

    /**
     * 是否公开
     */
    @TableField(value = "open")
    private Integer open;

    /*
     * 数据简介
     * */
    @TableField(value = "data_introduction")
    private String dataIntroduction;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    // 无参构造函数
    public WaterPhysicochemistry() {}


    // 全参构造函数
    public void waterPhysicochemistryAdd(String observationTime,Date createTime, String type,
                                         String userName, String filepath,String filename, String createUserid, Integer status, String contactPhone,
                              String contactAddress, String productionUnit, String contactEmail, Integer open, String dataIntroduction) {
        this.observationTime = observationTime;
        this.createTime = createTime;
        this.type = type;
        this.userName = userName;
        this.filepath = filepath;
        this.filename = filename;
        this.createUserid = createUserid;
        this.status = status;
        this.contactPhone = contactPhone;
        this.contactAddress = contactAddress;
        this.productionUnit = productionUnit;
        this.contactEmail = contactEmail;
        this.open = open;
        this.dataIntroduction = dataIntroduction;

    }

    //判空
    public  boolean isFull() {
        if(this.observationTime == null || this.observationTime.isEmpty()) {
            return false;
        }else if (this.deviceId == null || this.deviceId.isEmpty()) {
            return false;
        }else if (this.waterTemperature == null || this.waterTemperature.isEmpty()) {
            return false;
        }else if (this.ph == null || this.ph.isEmpty()) {
            return false;
        }else if (this.turbidity == null || this.turbidity.isEmpty()) {
            return false;
        }else if (this.conductivity == null || this.conductivity.isEmpty()) {
            return false;
        }else if (this.dissolvedOxygen == null || this.dissolvedOxygen.isEmpty()) {
            return false;
        }else if (this.transparency == null || this.transparency.isEmpty()) {
            return false;
        }else if (this.codmn == null || this.codmn.isEmpty()) {
            return false;
        }else if (this.tss == null || this.tss.isEmpty()) {
            return false;
        }else if (this.tn == null || this.tn.isEmpty()) {
            return false;
        }else if (this.tp == null || this.tp.isEmpty()) {
            return false;
        }else return this.chlorophyll != null && !this.chlorophyll.isEmpty();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        WaterPhysicochemistry other = (WaterPhysicochemistry) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getWaterTemperature() == null ? other.getWaterTemperature() == null : this.getWaterTemperature().equals(other.getWaterTemperature()))
                && (this.getPh() == null ? other.getPh() == null : this.getPh().equals(other.getPh()))
                && (this.getTurbidity() == null ? other.getTurbidity() == null : this.getTurbidity().equals(other.getTurbidity()))
                && (this.getConductivity() == null ? other.getConductivity() == null : this.getConductivity().equals(other.getConductivity()))
                && (this.getDissolvedOxygen() == null ? other.getDissolvedOxygen() == null : this.getDissolvedOxygen().equals(other.getDissolvedOxygen()))
                && (this.getTransparency() == null ? other.getTransparency() == null : this.getTransparency().equals(other.getTransparency()))
                && (this.getCodmn() == null ? other.getCodmn() == null : this.getCodmn().equals(other.getCodmn()))
                && (this.getTss() == null ? other.getTss() == null : this.getTss().equals(other.getTss()))
                && (this.getTn() == null ? other.getTn() == null : this.getTn().equals(other.getTn()))
                && (this.getTp() == null ? other.getTp() == null : this.getTp().equals(other.getTp()))
                && (this.getChlorophyll() == null ? other.getChlorophyll() == null : this.getChlorophyll().equals(other.getChlorophyll()))
                && (this.getObservationTime() == null ? other.getObservationTime() == null : this.getObservationTime().equals(other.getObservationTime()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
                && (this.getFilepath() == null ? other.getFilepath() == null : this.getFilepath().equals(other.getFilepath()))
                && (this.getFilename() == null ? other.getFilename() == null : this.getFilename().equals(other.getFilename()))
                && (this.getCreateUserid() == null ? other.getCreateUserid() == null : this.getCreateUserid().equals(other.getCreateUserid()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getContactPhone() == null ? other.getContactPhone() == null : this.getContactPhone().equals(other.getContactPhone()))
                && (this.getContactAddress() == null ? other.getContactAddress() == null : this.getContactAddress().equals(other.getContactAddress()))
                && (this.getProductionUnit() == null ? other.getProductionUnit() == null : this.getProductionUnit().equals(other.getProductionUnit()))
                && (this.getContactEmail() == null ? other.getContactEmail() == null : this.getContactEmail().equals(other.getContactEmail()))
                && (this.getOpen() == null ? other.getOpen() == null : this.getOpen().equals(other.getOpen()))
                && (this.getDataIntroduction() == null ? other.getDataIntroduction() == null : this.getDataIntroduction().equals(other.getDataIntroduction()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getWaterTemperature() == null) ? 0 : getWaterTemperature().hashCode());
        result = prime * result + ((getPh() == null) ? 0 : getPh().hashCode());
        result = prime * result + ((getTurbidity() == null) ? 0 : getTurbidity().hashCode());
        result = prime * result + ((getConductivity() == null) ? 0 : getConductivity().hashCode());
        result = prime * result + ((getDissolvedOxygen() == null) ? 0 : getDissolvedOxygen().hashCode());
        result = prime * result + ((getTransparency() == null) ? 0 : getTransparency().hashCode());
        result = prime * result + ((getCodmn() == null) ? 0 : getCodmn().hashCode());
        result = prime * result + ((getTss() == null) ? 0 : getTss().hashCode());
        result = prime * result + ((getTn() == null) ? 0 : getTn().hashCode());
        result = prime * result + ((getTp() == null) ? 0 : getTp().hashCode());
        result = prime * result + ((getChlorophyll() == null) ? 0 : getChlorophyll().hashCode());
        result = prime * result + ((getObservationTime() == null) ? 0 : getObservationTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getFilepath() == null) ? 0 : getFilepath().hashCode());
        result = prime * result + ((getFilename() == null) ? 0 : getFilename().hashCode());
        result = prime * result + ((getCreateUserid() == null) ? 0 : getCreateUserid().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getContactPhone() == null) ? 0 : getContactPhone().hashCode());
        result = prime * result + ((getContactAddress() == null) ? 0 : getContactAddress().hashCode());
        result = prime * result + ((getProductionUnit() == null) ? 0 : getProductionUnit().hashCode());
        result = prime * result + ((getContactEmail() == null) ? 0 : getContactEmail().hashCode());
        result = prime * result + ((getOpen() == null) ? 0 : getOpen().hashCode());
        result = prime * result + ((getDataIntroduction() == null) ? 0 : getDataIntroduction().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", waterTemperature=").append(waterTemperature);
        sb.append(", ph=").append(ph);
        sb.append(", turbidity=").append(turbidity);
        sb.append(", conductivity=").append(conductivity);
        sb.append(", dissolvedOxygen=").append(dissolvedOxygen);
        sb.append(", transparency=").append(transparency);
        sb.append(", codmn=").append(codmn);
        sb.append(", tss=").append(tss);
        sb.append(", tn=").append(tn);
        sb.append(", tp=").append(tp);
        sb.append(", chlorophyll=").append(chlorophyll);
        sb.append(", observationTime=").append(observationTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", type=").append(type);
        sb.append(", userName=").append(userName);
        sb.append(", filepath=").append(filepath);
        sb.append(", filename=").append(filename);
        sb.append(", createUserid=").append(createUserid);
        sb.append(", status=").append(status);
        sb.append(", contactPhone=").append(contactPhone);
        sb.append(", contactAddress=").append(contactAddress);
        sb.append(", productionUnit=").append(productionUnit);
        sb.append(", contactEmail=").append(contactEmail);
        sb.append(", open=").append(open);
        sb.append(", dataIntroduction=").append(dataIntroduction);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}