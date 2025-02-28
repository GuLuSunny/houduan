package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 设备点信息表
 * @TableName device_introduce_table
 */
@TableName(value ="device_introduce_table")
public class DeviceIntroduceTable implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 设备类型
     */
    private Integer deviceType;

    /**
     * 设备点图片地址
     */
    private String deviceImgPath;

    /**
     * 设备点介绍
     */
    private Object deviceIntroduce;

    /**
     * 图标地址
     */
    private String iconPath;

    /**
     * 设备编码
     */
    private Integer code;

    /**
     * 名称
     */
    private String deviceName;

    /**
     * 经度
     */
    private String ddLongitude;

    /**
     * 维度
     */
    private String ddLatitude;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    public Integer getId() {
        return id;
    }

    /**
     * id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 设备类型
     */
    public Integer getDeviceType() {
        return deviceType;
    }

    /**
     * 设备类型
     */
    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * 设备点图片地址
     */
    public String getDeviceImgPath() {
        return deviceImgPath;
    }

    /**
     * 设备点图片地址
     */
    public void setDeviceImgPath(String deviceImgPath) {
        this.deviceImgPath = deviceImgPath;
    }

    /**
     * 设备点介绍
     */
    public Object getDeviceIntroduce() {
        return deviceIntroduce;
    }

    /**
     * 设备点介绍
     */
    public void setDeviceIntroduce(Object deviceIntroduce) {
        this.deviceIntroduce = deviceIntroduce;
    }

    /**
     * 图标地址
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * 图标地址
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    /**
     * 设备编码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设备编码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 名称
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * 名称
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * 经度
     */
    public String getDdLongitude() {
        return ddLongitude;
    }

    /**
     * 经度
     */
    public void setDdLongitude(String ddLongitude) {
        this.ddLongitude = ddLongitude;
    }

    /**
     * 维度
     */
    public String getDdLatitude() {
        return ddLatitude;
    }

    /**
     * 维度
     */
    public void setDdLatitude(String ddLatitude) {
        this.ddLatitude = ddLatitude;
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
        DeviceIntroduceTable other = (DeviceIntroduceTable) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDeviceType() == null ? other.getDeviceType() == null : this.getDeviceType().equals(other.getDeviceType()))
            && (this.getDeviceImgPath() == null ? other.getDeviceImgPath() == null : this.getDeviceImgPath().equals(other.getDeviceImgPath()))
            && (this.getDeviceIntroduce() == null ? other.getDeviceIntroduce() == null : this.getDeviceIntroduce().equals(other.getDeviceIntroduce()))
            && (this.getIconPath() == null ? other.getIconPath() == null : this.getIconPath().equals(other.getIconPath()))
            && (this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode()))
            && (this.getDeviceName() == null ? other.getDeviceName() == null : this.getDeviceName().equals(other.getDeviceName()))
            && (this.getDdLongitude() == null ? other.getDdLongitude() == null : this.getDdLongitude().equals(other.getDdLongitude()))
            && (this.getDdLatitude() == null ? other.getDdLatitude() == null : this.getDdLatitude().equals(other.getDdLatitude()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDeviceType() == null) ? 0 : getDeviceType().hashCode());
        result = prime * result + ((getDeviceImgPath() == null) ? 0 : getDeviceImgPath().hashCode());
        result = prime * result + ((getDeviceIntroduce() == null) ? 0 : getDeviceIntroduce().hashCode());
        result = prime * result + ((getIconPath() == null) ? 0 : getIconPath().hashCode());
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getDeviceName() == null) ? 0 : getDeviceName().hashCode());
        result = prime * result + ((getDdLongitude() == null) ? 0 : getDdLongitude().hashCode());
        result = prime * result + ((getDdLatitude() == null) ? 0 : getDdLatitude().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deviceType=").append(deviceType);
        sb.append(", deviceImgPath=").append(deviceImgPath);
        sb.append(", deviceIntroduce=").append(deviceIntroduce);
        sb.append(", iconPath=").append(iconPath);
        sb.append(", code=").append(code);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", ddLongitude=").append(ddLongitude);
        sb.append(", ddLatitude=").append(ddLatitude);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}