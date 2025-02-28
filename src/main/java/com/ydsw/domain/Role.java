package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 角色表
 * @TableName role
 */
@TableName(value ="role")
public class Role implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色描述
     */
    private String roleinfo;

    /**
     * 角色标识符
     */
    private String roleEng;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 角色描述
     */
    public String getRoleinfo() {
        return roleinfo;
    }

    /**
     * 角色描述
     */
    public void setRoleinfo(String roleinfo) {
        this.roleinfo = roleinfo;
    }

    /**
     * 角色标识符
     */
    public String getRoleEng() {
        return roleEng;
    }

    /**
     * 角色标识符
     */
    public void setRoleEng(String roleEng) {
        this.roleEng = roleEng;
    }

    /**
     * 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
        Role other = (Role) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRoleinfo() == null ? other.getRoleinfo() == null : this.getRoleinfo().equals(other.getRoleinfo()))
            && (this.getRoleEng() == null ? other.getRoleEng() == null : this.getRoleEng().equals(other.getRoleEng()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRoleinfo() == null) ? 0 : getRoleinfo().hashCode());
        result = prime * result + ((getRoleEng() == null) ? 0 : getRoleEng().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", roleinfo=").append(roleinfo);
        sb.append(", roleEng=").append(roleEng);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}