package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 种表
 * @TableName species
 */
@TableName(value ="species")
public class Species implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 拉丁名称
     */
    private String nameLatin;

    /**
     * 科id
     */
    private String familyId;

    /**
     * 常量id-级别
     */
    private Integer constantId;

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
     * 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 拉丁名称
     */
    public String getNameLatin() {
        return nameLatin;
    }

    /**
     * 拉丁名称
     */
    public void setNameLatin(String nameLatin) {
        this.nameLatin = nameLatin;
    }

    /**
     * 科id
     */
    public String getFamilyId() {
        return familyId;
    }

    /**
     * 科id
     */
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    /**
     * 常量id-级别
     */
    public Integer getConstantId() {
        return constantId;
    }

    /**
     * 常量id-级别
     */
    public void setConstantId(Integer constantId) {
        this.constantId = constantId;
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
        Species other = (Species) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getNameLatin() == null ? other.getNameLatin() == null : this.getNameLatin().equals(other.getNameLatin()))
            && (this.getFamilyId() == null ? other.getFamilyId() == null : this.getFamilyId().equals(other.getFamilyId()))
            && (this.getConstantId() == null ? other.getConstantId() == null : this.getConstantId().equals(other.getConstantId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getNameLatin() == null) ? 0 : getNameLatin().hashCode());
        result = prime * result + ((getFamilyId() == null) ? 0 : getFamilyId().hashCode());
        result = prime * result + ((getConstantId() == null) ? 0 : getConstantId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", nameLatin=").append(nameLatin);
        sb.append(", familyId=").append(familyId);
        sb.append(", constantId=").append(constantId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}