package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Data;

/**
 * 模型使用状态 每人/每模型 对应一条
 * @TableName model_status
 */
@TableName(value ="model_status")
@Data
public class ModelStatus implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所支持的模型
     */
    @TableField(value = "model_name")
    private String modelName;

    /**
     * 模型使用状态
     */
    @TableField(value = "usage_status")
    private String usageStatus;

    /**
     * 
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 入库时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 提交人的用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 提交人的用户id
     */
    @TableField(value = "create_userid")
    private String createUserid;

    /**
     * 
     */
    @TableField(value = "data_introduction")
    private String dataIntroduction;


    /**
     * 选择的功能
     */
    @TableField(value = "function_selected")
    private String functionSelected;

    /**
     *
     */
    @TableField(value = "class_name")
    private String className;
    /**
     * 数据观测日期
     */
    @TableField(value = "observation_time")
    private String observationTime;

    /**
     * 待定字段
     */
    @TableField(value = "type")
    private String type;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public ModelStatus() {}

    public ModelStatus(Map<String,Object> map) {
        this.setId((Integer)map.get("id"));
        this.setModelName((String)map.get("model_name"));
        this.setUsageStatus((String)map.get("usage_status"));
        this.setStatus((Integer)map.get("status"));
        this.setCreateTime((Date)map.get("create_time"));
        this.setUpdateTime((Date)map.get("update_time"));
        this.setUserName((String)map.get("user_name"));
        this.setCreateUserid((String)map.get("create_userid"));
        this.setDataIntroduction((String)map.get("data_introduction"));
        this.setFunctionSelected((String)map.get("function_selected"));
        this.setClassName((String)map.get("class_name"));
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
        ModelStatus other = (ModelStatus) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getModelName() == null ? other.getModelName() == null : this.getModelName().equals(other.getModelName()))
                && (this.getUsageStatus() == null ? other.getUsageStatus() == null : this.getUsageStatus().equals(other.getUsageStatus()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
                && (this.getCreateUserid() == null ? other.getCreateUserid() == null : this.getCreateUserid().equals(other.getCreateUserid()))
                && (this.getDataIntroduction() == null ? other.getDataIntroduction() == null : this.getDataIntroduction().equals(other.getDataIntroduction()))
                && (this.getFunctionSelected() == null ? other.getFunctionSelected() == null : this.getFunctionSelected().equals(other.getFunctionSelected()))
                && (this.getClassName() == null ? other.getClassName() == null : this.getClassName().equals(other.getClassName()))
                && (this.getObservationTime() == null ? other.getObservationTime() == null : this.getObservationTime().equals(other.getObservationTime()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getModelName() == null) ? 0 : getModelName().hashCode());
        result = prime * result + ((getUsageStatus() == null) ? 0 : getUsageStatus().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getCreateUserid() == null) ? 0 : getCreateUserid().hashCode());
        result = prime * result + ((getDataIntroduction() == null) ? 0 : getDataIntroduction().hashCode());
        result = prime * result + ((getFunctionSelected() == null) ? 0 : getFunctionSelected().hashCode());
        result = prime * result + ((getClassName() == null) ? 0 : getClassName().hashCode());
        result = prime * result + ((getObservationTime() == null) ? 0 : getObservationTime().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", modelName=").append(modelName);
        sb.append(", usageStatus=").append(usageStatus);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", userName=").append(userName);
        sb.append(", createUserid=").append(createUserid);
        sb.append(", dataIntroduction=").append(dataIntroduction);
        sb.append(", functionSelected=").append(functionSelected);
        sb.append(", className=").append(className);
        sb.append(", observationTime=").append(observationTime);
        sb.append(", type=").append(type);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}