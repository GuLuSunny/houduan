package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 科表
 * @TableName family
 */
@TableName(value ="family")
public class Family implements Serializable {
    /**
     * 科id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 科名称
     */
    private String name;

    /**
     * 科英文名称
     */
    private String nameEng;

    /**
     * 关联目id
     */
    private Integer orderId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 科id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 科id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 科名称
     */
    public String getName() {
        return name;
    }

    /**
     * 科名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 科英文名称
     */
    public String getNameEng() {
        return nameEng;
    }

    /**
     * 科英文名称
     */
    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    /**
     * 关联目id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 关联目id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
        Family other = (Family) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getNameEng() == null ? other.getNameEng() == null : this.getNameEng().equals(other.getNameEng()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getNameEng() == null) ? 0 : getNameEng().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
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
        sb.append(", nameEng=").append(nameEng);
        sb.append(", orderId=").append(orderId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}