package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 植被监测指标表
 * @TableName vegetation_monitoring_indicators
 */
@TableName(value ="vegetation_monitoring_indicators")
@Data
public class VegetationMonitoringIndicators implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 植被物种
     */
    @TableField(value = "vegetation_species")
    private String vegetationSpecies;

    /**
     * 叶面积指数
     */
    @TableField(value = "leaf_area_index")
    private String leafAreaIndex;
    /**
     * 物候指数
     */
    @TableField(value = "phenological_index")
    private String phenologicalIndex;

    /**
     * 植被含水量（%）
     */
    @TableField(value = "vegetation_moisture_content")
    private String vegetationMoistureContent;

    /**
     * 叶绿素含量（mg/g）
     */
    @TableField(value = "chlorophyll_content")
    private String chlorophyllContent;

    /**
     * 生物量（g/m2）
     */
    @TableField(value = "biomass")
    private String biomass;

    /**
     * 碳氮比
     */
    @TableField(value = "carbon_nitrogen_ratio")
    private String carbonNitrogenRatio;

    /**
     * 设备表-id
     */
    @TableField(value = "device_id")
    private Integer deviceId;

    /**
     * 调查时间
     */
    @TableField(value = "investigation_time")
    private String investigationTime;

    /**
     * 数据导入人用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 数据来源：excel；form
     */
    @TableField(value = "type")
    private String type;

    /**
     * 数据导入用户id
     */
    @TableField(value = "create_userid")
    private String createUserid;

    /**
     * 是否已删除，0：未删除，1：已删除
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 数据入库时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 备注
     */
    @TableField(value = "memo")
    private String memo;

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
        VegetationMonitoringIndicators other = (VegetationMonitoringIndicators) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getVegetationSpecies() == null ? other.getVegetationSpecies() == null : this.getVegetationSpecies().equals(other.getVegetationSpecies()))
                && (this.getLeafAreaIndex() == null ? other.getLeafAreaIndex() == null : this.getLeafAreaIndex().equals(other.getLeafAreaIndex()))
                && (this.getPhenologicalIndex() == null ? other.getPhenologicalIndex() == null : this.getPhenologicalIndex().equals(other.getPhenologicalIndex()))
                && (this.getVegetationMoistureContent() == null ? other.getVegetationMoistureContent() == null : this.getVegetationMoistureContent().equals(other.getVegetationMoistureContent()))
                && (this.getChlorophyllContent() == null ? other.getChlorophyllContent() == null : this.getChlorophyllContent().equals(other.getChlorophyllContent()))
                && (this.getBiomass() == null ? other.getBiomass() == null : this.getBiomass().equals(other.getBiomass()))
                && (this.getCarbonNitrogenRatio() == null ? other.getCarbonNitrogenRatio() == null : this.getCarbonNitrogenRatio().equals(other.getCarbonNitrogenRatio()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getInvestigationTime() == null ? other.getInvestigationTime() == null : this.getInvestigationTime().equals(other.getInvestigationTime()))
                && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getCreateUserid() == null ? other.getCreateUserid() == null : this.getCreateUserid().equals(other.getCreateUserid()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getMemo() == null ? other.getMemo() == null : this.getMemo().equals(other.getMemo()))
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
        result = prime * result + ((getVegetationSpecies() == null) ? 0 : getVegetationSpecies().hashCode());
        result = prime * result + ((getLeafAreaIndex() == null) ? 0 : getLeafAreaIndex().hashCode());
        result = prime * result + ((getPhenologicalIndex() == null) ? 0 : getPhenologicalIndex().hashCode());
        result = prime * result + ((getVegetationMoistureContent() == null) ? 0 : getVegetationMoistureContent().hashCode());
        result = prime * result + ((getChlorophyllContent() == null) ? 0 : getChlorophyllContent().hashCode());
        result = prime * result + ((getBiomass() == null) ? 0 : getBiomass().hashCode());
        result = prime * result + ((getCarbonNitrogenRatio() == null) ? 0 : getCarbonNitrogenRatio().hashCode());
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getInvestigationTime() == null) ? 0 : getInvestigationTime().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getCreateUserid() == null) ? 0 : getCreateUserid().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getMemo() == null) ? 0 : getMemo().hashCode());
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
        sb.append(", vegetationSpecies=").append(vegetationSpecies);
        sb.append(", leafAreaIndex=").append(leafAreaIndex);
        sb.append(", phenologicalIndex=").append(phenologicalIndex);
        sb.append(", vegetationMoistureContent=").append(vegetationMoistureContent);
        sb.append(", chlorophyllContent=").append(chlorophyllContent);
        sb.append(", biomass=").append(biomass);
        sb.append(", carbonNitrogenRatio=").append(carbonNitrogenRatio);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", investigationTime=").append(investigationTime);
        sb.append(", userName=").append(userName);
        sb.append(", type=").append(type);
        sb.append(", createUserid=").append(createUserid);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", memo=").append(memo);
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