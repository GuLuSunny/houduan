package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 泵站表
 * @TableName pumping_station
 */
@TableName(value ="pumping_station")
@Data
public class PumpingStation implements Serializable {
    /**
     * 
     */
    @TableId(value = "gid")
    private Integer gid;

    /**
     * 
     */
    @TableField(value = "objectid")
    private Double objectid;

    /**
     * 省
     */
    @TableField(value = "provincial")
    private String provincial;

    /**
     * 城
     */
    @TableField(value = "city")
    private String city;

    /**
     * 国
     */
    @TableField(value = "county")
    private String county;

    /**
     * 所属地区
     */
    @TableField(value = "irrname")
    private String irrname;

    /**
     * 所属灌区类型
     */
    @TableField(value = "irrtype")
    private String irrtype;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 
     */
    @TableField(value = "angle")
    private BigDecimal angle;

    /**
     * 
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 点位信息
     */
    @TableField(value = "geog")
    private Object geog;

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
        PumpingStation other = (PumpingStation) that;
        return (this.getGid() == null ? other.getGid() == null : this.getGid().equals(other.getGid()))
            && (this.getObjectid() == null ? other.getObjectid() == null : this.getObjectid().equals(other.getObjectid()))
            && (this.getProvincial() == null ? other.getProvincial() == null : this.getProvincial().equals(other.getProvincial()))
            && (this.getCity() == null ? other.getCity() == null : this.getCity().equals(other.getCity()))
            && (this.getCounty() == null ? other.getCounty() == null : this.getCounty().equals(other.getCounty()))
            && (this.getIrrname() == null ? other.getIrrname() == null : this.getIrrname().equals(other.getIrrname()))
            && (this.getIrrtype() == null ? other.getIrrtype() == null : this.getIrrtype().equals(other.getIrrtype()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getAngle() == null ? other.getAngle() == null : this.getAngle().equals(other.getAngle()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getGeog() == null ? other.getGeog() == null : this.getGeog().equals(other.getGeog()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getGid() == null) ? 0 : getGid().hashCode());
        result = prime * result + ((getObjectid() == null) ? 0 : getObjectid().hashCode());
        result = prime * result + ((getProvincial() == null) ? 0 : getProvincial().hashCode());
        result = prime * result + ((getCity() == null) ? 0 : getCity().hashCode());
        result = prime * result + ((getCounty() == null) ? 0 : getCounty().hashCode());
        result = prime * result + ((getIrrname() == null) ? 0 : getIrrname().hashCode());
        result = prime * result + ((getIrrtype() == null) ? 0 : getIrrtype().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getAngle() == null) ? 0 : getAngle().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getGeog() == null) ? 0 : getGeog().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", gid=").append(gid);
        sb.append(", objectid=").append(objectid);
        sb.append(", provincial=").append(provincial);
        sb.append(", city=").append(city);
        sb.append(", county=").append(county);
        sb.append(", irrname=").append(irrname);
        sb.append(", irrtype=").append(irrtype);
        sb.append(", name=").append(name);
        sb.append(", angle=").append(angle);
        sb.append(", remark=").append(remark);
        sb.append(", geog=").append(geog);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}