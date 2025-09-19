package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;


/**
 *
 * @TableName model_list
 */
@TableName(value ="model_list")
@Data
public class ModelList implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField(value = "class_name")
    private String className;

    /**
     *
     */
    @TableField(value = "model_name")
    private String modelName;

    /**
     *
     */
    @TableField(value = "model_info")
    private String modelInfo;

    /**
     *
     */
    @TableField(value = "status")
    private Integer status;

    /**
     *
     */
    @TableField(value = "functions")
    private String functions;

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
        ModelList other = (ModelList) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getClassName() == null ? other.getClassName() == null : this.getClassName().equals(other.getClassName()))
                && (this.getModelName() == null ? other.getModelName() == null : this.getModelName().equals(other.getModelName()))
                && (this.getModelInfo() == null ? other.getModelInfo() == null : this.getModelInfo().equals(other.getModelInfo()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getFunctions() == null ? other.getFunctions() == null : this.getFunctions().equals(other.getFunctions()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getClassName() == null) ? 0 : getClassName().hashCode());
        result = prime * result + ((getModelName() == null) ? 0 : getModelName().hashCode());
        result = prime * result + ((getModelInfo() == null) ? 0 : getModelInfo().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getFunctions() == null) ? 0 : getFunctions().hashCode());
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
        sb.append(", modelName=").append(modelName);
        sb.append(", modelInfo=").append(modelInfo);
        sb.append(", status=").append(status);
        sb.append(", functions=").append(functions);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}