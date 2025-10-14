package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 目表
 * @TableName order
 */
@TableName(value ="biological_order")
public class Order implements Serializable {
    /**
     * 目id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 目名称
     */
    private String name;

    /**
     * 目英文
     */
    private String nameEng;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 目id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 目id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 目名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 目英文
     */
    public String getNameEng() {
        return nameEng;
    }

    /**
     * 目英文
     */
    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
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
        Order other = (Order) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getNameEng() == null ? other.getNameEng() == null : this.getNameEng().equals(other.getNameEng()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getNameEng() == null) ? 0 : getNameEng().hashCode());
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
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}