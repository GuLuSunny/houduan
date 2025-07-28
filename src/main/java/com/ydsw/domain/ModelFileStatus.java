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
 * 
 * @TableName model_file_status
 */
@TableName(value ="model_file_status")
@Data
public class ModelFileStatus implements Serializable {
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
     * 数据处理状态
     */
    @TableField(value = "deal_status")
    private String dealStatus;

    /**
     * 临时文件保存路径
     */
    @TableField(value = "filepath")
    private String filepath;

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
     * 入库时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 状态更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 
     */
    @TableField(value = "data_introduction")
    private String dataIntroduction;

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

    public ModelFileStatus() {}

    public ModelFileStatus(Map<String,Object> map)
    {
        this.modelName = (String)map.get("modelName");
        this.dealStatus = (String)map.get("dealStatus");
        this.filepath = (String)map.get("filepath");
        this.userName = (String)map.get("userName");
        this.createUserid = (String)map.get("createUserid");
        this.createTime = (Date)map.get("createTime");
        this.updateTime = (Date)map.get("updateTime");
        this.status = (Integer)map.get("status");
        this.dataIntroduction = (String)map.get("dataIntroduction");
        this.observationTime = (String)map.get("observationTime");
        this.type = (String)map.get("type");

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
        ModelFileStatus other = (ModelFileStatus) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getModelName() == null ? other.getModelName() == null : this.getModelName().equals(other.getModelName()))
            && (this.getDealStatus() == null ? other.getDealStatus() == null : this.getDealStatus().equals(other.getDealStatus()))
            && (this.getFilepath() == null ? other.getFilepath() == null : this.getFilepath().equals(other.getFilepath()))
            && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
            && (this.getCreateUserid() == null ? other.getCreateUserid() == null : this.getCreateUserid().equals(other.getCreateUserid()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDataIntroduction() == null ? other.getDataIntroduction() == null : this.getDataIntroduction().equals(other.getDataIntroduction()))
            && (this.getObservationTime() == null ? other.getObservationTime() == null : this.getObservationTime().equals(other.getObservationTime()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getModelName() == null) ? 0 : getModelName().hashCode());
        result = prime * result + ((getDealStatus() == null) ? 0 : getDealStatus().hashCode());
        result = prime * result + ((getFilepath() == null) ? 0 : getFilepath().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getCreateUserid() == null) ? 0 : getCreateUserid().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDataIntroduction() == null) ? 0 : getDataIntroduction().hashCode());
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
        sb.append(", dealStatus=").append(dealStatus);
        sb.append(", filepath=").append(filepath);
        sb.append(", userName=").append(userName);
        sb.append(", createUserid=").append(createUserid);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", status=").append(status);
        sb.append(", dataIntroduction=").append(dataIntroduction);
        sb.append(", observationTime=").append(observationTime);
        sb.append(", type=").append(type);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}