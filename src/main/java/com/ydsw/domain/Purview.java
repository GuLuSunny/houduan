package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 权限表
 * @TableName right
 */
@TableName(value ="purview")
public class Purview implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 权限描述
     */
    private String rightinfo;

    /**
     * 权限标识符
     */
    private String rightEng;

    /**
     * 权限接口URL
     */
    private String rightApi;

    /**
     * 状态（0显示，1停用）
     */
    private String status;

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
     * 权限描述
     */
    public String getRightinfo() {
        return rightinfo;
    }

    /**
     * 权限描述
     */
    public void setRightinfo(String rightinfo) {
        this.rightinfo = rightinfo;
    }

    /**
     * 权限标识符
     */
    public String getRightEng() {
        return rightEng;
    }

    /**
     * 权限标识符
     */
    public void setRightEng(String rightEng) {
        this.rightEng = rightEng;
    }

    /**
     * 权限接口URL
     */
    public String getRightApi() {
        return rightApi;
    }

    /**
     * 权限接口URL
     */
    public void setRightApi(String rightApi) {
        this.rightApi = rightApi;
    }

    /**
     * 状态（0显示，1停用）
     */
    public String getStatus() {
        return status;
    }

    /**
     * 状态（0显示，1停用）
     */
    public void setStatus(String status) {
        this.status = status;
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
        Purview other = (Purview) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRightinfo() == null ? other.getRightinfo() == null : this.getRightinfo().equals(other.getRightinfo()))
            && (this.getRightEng() == null ? other.getRightEng() == null : this.getRightEng().equals(other.getRightEng()))
            && (this.getRightApi() == null ? other.getRightApi() == null : this.getRightApi().equals(other.getRightApi()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRightinfo() == null) ? 0 : getRightinfo().hashCode());
        result = prime * result + ((getRightEng() == null) ? 0 : getRightEng().hashCode());
        result = prime * result + ((getRightApi() == null) ? 0 : getRightApi().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
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
        sb.append(", rightinfo=").append(rightinfo);
        sb.append(", rightEng=").append(rightEng);
        sb.append(", rightApi=").append(rightApi);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}