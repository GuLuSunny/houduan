package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName bird
 */
@TableName(value ="bird")
@Data
public class Bird implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField(value = "species_id")
    private Integer speciesId;

    /**
     * 科名
     */
    @TableField(value = "family_id")
    private Integer familyId;

    /**
     * 目名
     */
    @TableField(value = "order_id")
    private Integer orderId;

    /**
     *
     */
    @TableField(value = "bird_latin")
    private String birdLatin;

    /**
     *
     */
    @TableField(value = "bird_feature")
    private String birdFeature;

    /**
     *
     */
    @TableField(value = "bird_live")
    private String birdLive;

    /**
     *
     */
    @TableField(value = "bird_type")
    private String birdType;

    /**
     *
     */
    @TableField(value = "bird_EPI")
    private String birdEpi;

    /**
     * 图片保存路径
     */
    @TableField(value = "filepath")
    private String filepath;

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
        Bird other = (Bird) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getSpeciesId() == null ? other.getSpeciesId() == null : this.getSpeciesId().equals(other.getSpeciesId()))
                && (this.getFamilyId() == null ? other.getFamilyId() == null : this.getFamilyId().equals(other.getFamilyId()))
                && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
                && (this.getBirdLatin() == null ? other.getBirdLatin() == null : this.getBirdLatin().equals(other.getBirdLatin()))
                && (this.getBirdFeature() == null ? other.getBirdFeature() == null : this.getBirdFeature().equals(other.getBirdFeature()))
                && (this.getBirdLive() == null ? other.getBirdLive() == null : this.getBirdLive().equals(other.getBirdLive()))
                && (this.getBirdType() == null ? other.getBirdType() == null : this.getBirdType().equals(other.getBirdType()))
                && (this.getBirdEpi() == null ? other.getBirdEpi() == null : this.getBirdEpi().equals(other.getBirdEpi()))
                && (this.getFilepath() == null ? other.getFilepath() == null : this.getFilepath().equals(other.getFilepath()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSpeciesId() == null) ? 0 : getSpeciesId().hashCode());
        result = prime * result + ((getFamilyId() == null) ? 0 : getFamilyId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getBirdLatin() == null) ? 0 : getBirdLatin().hashCode());
        result = prime * result + ((getBirdFeature() == null) ? 0 : getBirdFeature().hashCode());
        result = prime * result + ((getBirdLive() == null) ? 0 : getBirdLive().hashCode());
        result = prime * result + ((getBirdType() == null) ? 0 : getBirdType().hashCode());
        result = prime * result + ((getBirdEpi() == null) ? 0 : getBirdEpi().hashCode());
        result = prime * result + ((getFilepath() == null) ? 0 : getFilepath().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", speciesId=").append(speciesId);
        sb.append(", familyId=").append(familyId);
        sb.append(", orderId=").append(orderId);
        sb.append(", birdLatin=").append(birdLatin);
        sb.append(", birdFeature=").append(birdFeature);
        sb.append(", birdLive=").append(birdLive);
        sb.append(", birdType=").append(birdType);
        sb.append(", birdEpi=").append(birdEpi);
        sb.append(", filepath=").append(filepath);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}