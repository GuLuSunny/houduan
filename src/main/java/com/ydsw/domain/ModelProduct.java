package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName model_product
 */
@TableName(value ="model_product")
@Data
public class ModelProduct implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属类
     */
    @TableField(value = "class_name")
    private String className;

    /**
     * 文件绝对路径
     */
    @TableField(value = "filepath")
    private String filepath;

    /**
     * 文件名
     */
    @TableField(value = "filename")
    private String filename;

    /**
     * 观测日期
     */
    @TableField(value = "observation_time")
    private String observationTime;

    /**
     * 开始时间(如果有)
     */
    @TableField(value = "start_time")
    private String startTime;

    /**
     * 结束时间(如果有)
     */
    @TableField(value = "end_time")
    private String endTime;

    /**
     * 入库日期
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 待定字段
     */
    @TableField(value = "type")
    private String type;

    /**
     * 持有者(如果需要)
     */
    @TableField(value = "owner")
    private String owner;

    /**
     * 数据状态
     */
    @TableField(value = "status")
    private Integer status;

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
        ModelProduct other = (ModelProduct) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getClassName() == null ? other.getClassName() == null : this.getClassName().equals(other.getClassName()))
            && (this.getFilepath() == null ? other.getFilepath() == null : this.getFilepath().equals(other.getFilepath()))
            && (this.getFilename() == null ? other.getFilename() == null : this.getFilename().equals(other.getFilename()))
            && (this.getObservationTime() == null ? other.getObservationTime() == null : this.getObservationTime().equals(other.getObservationTime()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getOwner() == null ? other.getOwner() == null : this.getOwner().equals(other.getOwner()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getClassName() == null) ? 0 : getClassName().hashCode());
        result = prime * result + ((getFilepath() == null) ? 0 : getFilepath().hashCode());
        result = prime * result + ((getFilename() == null) ? 0 : getFilename().hashCode());
        result = prime * result + ((getObservationTime() == null) ? 0 : getObservationTime().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getOwner() == null) ? 0 : getOwner().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", className=").append(className);
        sb.append(", filepath=").append(filepath);
        sb.append(", filename=").append(filename);
        sb.append(", observationTime=").append(observationTime);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", type=").append(type);
        sb.append(", owner=").append(owner);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}