package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 伊河流量登记表
 * @TableName tb_flow
 */
@TableName(value ="tb_flow")
@Data
public class TbFlow implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 设备表外键id
     */
    @TableField(value = "device_id")
    private Integer deviceId;

    /**
     * 流量（m3/s）

     */
    @TableField(value = "flow")
    private String flow;

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
     * 数据来源
     */
    @TableField(value = "type")
    private String type;

    /**
     * 数据导入人用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 对应保存Excel的文件地址(可为空)
     */
    @TableField(value = "filepath")
    private String filepath;

    /**
     * 文件名
     */
    @TableField(value = "filename")
    private String filename;

    /**
     * 保存用户的id
     */
    @TableField(value = "create_userid")
    private String createUserid;

    /**
     * 是否已删除 0:未删除，1:已删除
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
     * 是否公开，0不公开；1公开
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
    public TbFlow() {}
    public TbFlow(Integer deviceId,String flow, String observationTime, Date createTime, String type, String userName,
                  String filepath,String filename, String createUserid, Integer status, String contactPhone,
                  String contactAddress, String productionUnit, String contactEmail, Integer open,String dataIntroduction) {
        this.deviceId = deviceId;
        this.flow = flow;
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
        TbFlow other = (TbFlow) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getFlow() == null ? other.getFlow() == null : this.getFlow().equals(other.getFlow()))
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
        result = prime * result + ((getFlow() == null) ? 0 : getFlow().hashCode());
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
        sb.append(", flow=").append(flow);
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