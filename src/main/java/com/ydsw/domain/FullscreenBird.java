package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 大屏内水鸟科普信息
 * @TableName fullscreen_bird
 */
@TableName(value ="fullscreen_bird")
public class FullscreenBird implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 图片地址
     */
    private String imgsrc;

    /**
     * 中文名称
     */
    private String name;

    /**
     * 拉丁学名
     */
    private String nameLatin;

    /**
     * 居留型
     */
    private String resideType;

    /**
     * 保护级别
     */
    private String protectionLevel;

    /**
     * 形态特征
     */
    private String morphologicalFeature;

    /**
     * 生活习性
     */
    private String livingHabit;

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
     * 图片地址
     */
    public String getImgsrc() {
        return imgsrc;
    }

    /**
     * 图片地址
     */
    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    /**
     * 中文名称
     */
    public String getName() {
        return name;
    }

    /**
     * 中文名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 拉丁学名
     */
    public String getNameLatin() {
        return nameLatin;
    }

    /**
     * 拉丁学名
     */
    public void setNameLatin(String nameLatin) {
        this.nameLatin = nameLatin;
    }

    /**
     * 居留型
     */
    public String getResideType() {
        return resideType;
    }

    /**
     * 居留型
     */
    public void setResideType(String resideType) {
        this.resideType = resideType;
    }

    /**
     * 保护级别
     */
    public String getProtectionLevel() {
        return protectionLevel;
    }

    /**
     * 保护级别
     */
    public void setProtectionLevel(String protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    /**
     * 形态特征
     */
    public String getMorphologicalFeature() {
        return morphologicalFeature;
    }

    /**
     * 形态特征
     */
    public void setMorphologicalFeature(String morphologicalFeature) {
        this.morphologicalFeature = morphologicalFeature;
    }

    /**
     * 生活习性
     */
    public String getLivingHabit() {
        return livingHabit;
    }

    /**
     * 生活习性
     */
    public void setLivingHabit(String livingHabit) {
        this.livingHabit = livingHabit;
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
        FullscreenBird other = (FullscreenBird) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getImgsrc() == null ? other.getImgsrc() == null : this.getImgsrc().equals(other.getImgsrc()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getNameLatin() == null ? other.getNameLatin() == null : this.getNameLatin().equals(other.getNameLatin()))
            && (this.getResideType() == null ? other.getResideType() == null : this.getResideType().equals(other.getResideType()))
            && (this.getProtectionLevel() == null ? other.getProtectionLevel() == null : this.getProtectionLevel().equals(other.getProtectionLevel()))
            && (this.getMorphologicalFeature() == null ? other.getMorphologicalFeature() == null : this.getMorphologicalFeature().equals(other.getMorphologicalFeature()))
            && (this.getLivingHabit() == null ? other.getLivingHabit() == null : this.getLivingHabit().equals(other.getLivingHabit()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getImgsrc() == null) ? 0 : getImgsrc().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getNameLatin() == null) ? 0 : getNameLatin().hashCode());
        result = prime * result + ((getResideType() == null) ? 0 : getResideType().hashCode());
        result = prime * result + ((getProtectionLevel() == null) ? 0 : getProtectionLevel().hashCode());
        result = prime * result + ((getMorphologicalFeature() == null) ? 0 : getMorphologicalFeature().hashCode());
        result = prime * result + ((getLivingHabit() == null) ? 0 : getLivingHabit().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", imgsrc=").append(imgsrc);
        sb.append(", name=").append(name);
        sb.append(", nameLatin=").append(nameLatin);
        sb.append(", resideType=").append(resideType);
        sb.append(", protectionLevel=").append(protectionLevel);
        sb.append(", morphologicalFeature=").append(morphologicalFeature);
        sb.append(", livingHabit=").append(livingHabit);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}