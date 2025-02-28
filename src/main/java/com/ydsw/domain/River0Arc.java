package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @TableName river0_arc
 */
@TableName(value ="river0_arc")
@Data
public class River0Arc implements Serializable {
    /**
     * 
     */
    @TableId(value = "gid")
    private Integer gid;

    /**
     * 
     */
    @TableField(value = "fnode_")
    private Double fnode;

    /**
     * 
     */
    @TableField(value = "tnode_")
    private Double tnode;

    /**
     * 
     */
    @TableField(value = "lpoly_")
    private Double lpoly;

    /**
     * 
     */
    @TableField(value = "rpoly_")
    private Double rpoly;

    /**
     * 
     */
    @TableField(value = "length")
    private Double length;

    /**
     * 
     */
    @TableField(value = "river0_")
    private Double river0;

    /**
     * 
     */
    @TableField(value = "river0_id")
    private Double river0Id;

    /**
     * 
     */
    @TableField(value = "type")
    private Integer type;

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
        River0Arc other = (River0Arc) that;
        return (this.getGid() == null ? other.getGid() == null : this.getGid().equals(other.getGid()))
            && (this.getFnode() == null ? other.getFnode() == null : this.getFnode().equals(other.getFnode()))
            && (this.getTnode() == null ? other.getTnode() == null : this.getTnode().equals(other.getTnode()))
            && (this.getLpoly() == null ? other.getLpoly() == null : this.getLpoly().equals(other.getLpoly()))
            && (this.getRpoly() == null ? other.getRpoly() == null : this.getRpoly().equals(other.getRpoly()))
            && (this.getLength() == null ? other.getLength() == null : this.getLength().equals(other.getLength()))
            && (this.getRiver0() == null ? other.getRiver0() == null : this.getRiver0().equals(other.getRiver0()))
            && (this.getRiver0Id() == null ? other.getRiver0Id() == null : this.getRiver0Id().equals(other.getRiver0Id()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getGeom() == null ? other.getGeom() == null : this.getGeom().equals(other.getGeom()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getGid() == null) ? 0 : getGid().hashCode());
        result = prime * result + ((getFnode() == null) ? 0 : getFnode().hashCode());
        result = prime * result + ((getTnode() == null) ? 0 : getTnode().hashCode());
        result = prime * result + ((getLpoly() == null) ? 0 : getLpoly().hashCode());
        result = prime * result + ((getRpoly() == null) ? 0 : getRpoly().hashCode());
        result = prime * result + ((getLength() == null) ? 0 : getLength().hashCode());
        result = prime * result + ((getRiver0() == null) ? 0 : getRiver0().hashCode());
        result = prime * result + ((getRiver0Id() == null) ? 0 : getRiver0Id().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
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
        sb.append(", fnode=").append(fnode);
        sb.append(", tnode=").append(tnode);
        sb.append(", lpoly=").append(lpoly);
        sb.append(", rpoly=").append(rpoly);
        sb.append(", length=").append(length);
        sb.append(", river0=").append(river0);
        sb.append(", river0Id=").append(river0Id);
        sb.append(", type=").append(type);
        sb.append(", geom=").append(geom);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}