package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @TableName lakes
 */
@TableName(value ="lakes")
@Data
public class Lakes implements Serializable {
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
     * 
     */
    @TableField(value = "provincial")
    private String provincial;

    /**
     * 
     */
    @TableField(value = "city")
    private String city;

    /**
     * 
     */
    @TableField(value = "county")
    private String county;

    /**
     * 
     */
    @TableField(value = "irrname")
    private String irrname;

    /**
     * 
     */
    @TableField(value = "irrtype")
    private String irrtype;

    /**
     * 
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
     * 
     */
    @TableField(value = "shape_leng")
    private Integer shapeLeng;

    /**
     * 
     */
    @TableField(value = "shape_area")
    private Integer shapeArea;

    /**
     * 
     */
    @TableField(value = "geom")
    private Object geom;

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
        Lakes other = (Lakes) that;
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
            && (this.getShapeLeng() == null ? other.getShapeLeng() == null : this.getShapeLeng().equals(other.getShapeLeng()))
            && (this.getShapeArea() == null ? other.getShapeArea() == null : this.getShapeArea().equals(other.getShapeArea()))
            && (this.getGeom() == null ? other.getGeom() == null : this.getGeom().equals(other.getGeom()));
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
        result = prime * result + ((getShapeLeng() == null) ? 0 : getShapeLeng().hashCode());
        result = prime * result + ((getShapeArea() == null) ? 0 : getShapeArea().hashCode());
        result = prime * result + ((getGeom() == null) ? 0 : getGeom().hashCode());
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
        sb.append(", shapeLeng=").append(shapeLeng);
        sb.append(", shapeArea=").append(shapeArea);
        sb.append(", geom=").append(geom);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}