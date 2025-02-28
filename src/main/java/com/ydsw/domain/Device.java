package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 观测站的设备位置表
 * @TableName device
 */
@TableName(value ="device")
@Data
public class Device implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 观测设备名称
     */
    @TableField(value = "device_name")
    private String deviceName;

    /**
     * dms经度，度分秒
     */
    @TableField(value = "dms_longitude")
    private String dmsLongitude;

    /**
     * dms纬度，度分秒
     */
    @TableField(value = "dms_latitude")
    private String dmsLatitude;

    /**
     * dd经度，小数
     */
    @TableField(value = "dd_longitude")
    private String ddLongitude;

    /**
     * dd纬度，小数
     */
    @TableField(value = "dd_latitude")
    private String ddLatitude;

    /**
     * 观测设备种类分类
     */
    @TableField(value = "type")
    private String type;

    /**
     * 备注
     */
    @TableField(value = "memo")
    private String memo;



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
        Device other = (Device) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDeviceName() == null ? other.getDeviceName() == null : this.getDeviceName().equals(other.getDeviceName()))
            && (this.getDmsLongitude() == null ? other.getDmsLongitude() == null : this.getDmsLongitude().equals(other.getDmsLongitude()))
            && (this.getDmsLatitude() == null ? other.getDmsLatitude() == null : this.getDmsLatitude().equals(other.getDmsLatitude()))
            && (this.getDdLongitude() == null ? other.getDdLongitude() == null : this.getDdLongitude().equals(other.getDdLongitude()))
            && (this.getDdLatitude() == null ? other.getDdLatitude() == null : this.getDdLatitude().equals(other.getDdLatitude()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDeviceName() == null) ? 0 : getDeviceName().hashCode());
        result = prime * result + ((getDmsLongitude() == null) ? 0 : getDmsLongitude().hashCode());
        result = prime * result + ((getDmsLatitude() == null) ? 0 : getDmsLatitude().hashCode());
        result = prime * result + ((getDdLongitude() == null) ? 0 : getDdLongitude().hashCode());
        result = prime * result + ((getDdLatitude() == null) ? 0 : getDdLatitude().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", dmsLongitude=").append(dmsLongitude);
        sb.append(", dmsLatitude=").append(dmsLatitude);
        sb.append(", ddLongitude=").append(ddLongitude);
        sb.append(", ddLatitude=").append(ddLatitude);
        sb.append(", type=").append(type);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}