package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 鸟类观察调查数据表

 * @TableName observation_bird
 */
@TableName(value ="observation_bird")
@Data
public class ObservationBird implements Serializable {
    /**
     * 主键

     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 目表外键
     */
    @TableField(value = "order_id")
    private Integer orderId;

    /**
     * 科表外键
     */
    @TableField(value = "family_id")
    private Integer familyId;

    /**
     * 种表外键id
     */
    @TableField(value = "species_id")
    private Integer speciesId;

    /**
     * 观测人员路径信息外键id
     */
    @TableField(value = "watch_pi_id")
        private Integer watchPiId;
    /*
    * 观测日期
    * */
    @TableField(value = "observation_time")
    private String observationTime;

    /**
     * 观察时段起始
     */
    @TableField(value = "observation_period_begin")
    private String observationPeriodBegin;

    /**
     * 观测时段末尾
     */
    @TableField(value = "observation_period_end")
    private String observationPeriodEnd;

    /**
     * 观察地点
     */
    @TableField(value = "observation_address")
    private String observationAddress;

    /**
     * 生境类型
     */
    @TableField(value = "habitat_type")
    private String habitatType;

    /**
     * 数量
     */
    @TableField(value = "number")
    private Integer number;

    /**
     * 数据入库时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 天气
     */
    @TableField(value = "weather")
    private String weather;

    /**
     * 备注
     */
    @TableField(value = "memo")
    private String memo;

    /*
    * 是否删除
    * */
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
        ObservationBird other = (ObservationBird) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
                && (this.getFamilyId() == null ? other.getFamilyId() == null : this.getFamilyId().equals(other.getFamilyId()))
                && (this.getSpeciesId() == null ? other.getSpeciesId() == null : this.getSpeciesId().equals(other.getSpeciesId()))
                && (this.getWatchPiId() == null ? other.getWatchPiId() == null : this.getWatchPiId().equals(other.getWatchPiId()))
                && (this.getObservationTime() == null ? other.getObservationTime() == null : this.getObservationTime().equals(other.getObservationTime()))
                && (this.getObservationPeriodBegin() == null ? other.getObservationPeriodBegin() == null : this.getObservationPeriodBegin().equals(other.getObservationPeriodBegin()))
                && (this.getObservationPeriodEnd() == null ? other.getObservationPeriodEnd() == null : this.getObservationPeriodEnd().equals(other.getObservationPeriodEnd()))
                && (this.getObservationAddress() == null ? other.getObservationAddress() == null : this.getObservationAddress().equals(other.getObservationAddress()))
                && (this.getHabitatType() == null ? other.getHabitatType() == null : this.getHabitatType().equals(other.getHabitatType()))
                && (this.getNumber() == null ? other.getNumber() == null : this.getNumber().equals(other.getNumber()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getWeather() == null ? other.getWeather() == null : this.getWeather().equals(other.getWeather()))
                && (this.getMemo() == null ? other.getMemo() == null : this.getMemo().equals(other.getMemo()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getFamilyId() == null) ? 0 : getFamilyId().hashCode());
        result = prime * result + ((getSpeciesId() == null) ? 0 : getSpeciesId().hashCode());
        result = prime * result + ((getWatchPiId() == null) ? 0 : getWatchPiId().hashCode());
        result = prime * result + ((getObservationTime() == null) ? 0 : getObservationTime().hashCode());
        result = prime * result + ((getObservationPeriodBegin() == null) ? 0 : getObservationPeriodBegin().hashCode());
        result = prime * result + ((getObservationPeriodEnd() == null) ? 0 : getObservationPeriodEnd().hashCode());
        result = prime * result + ((getObservationAddress() == null) ? 0 : getObservationAddress().hashCode());
        result = prime * result + ((getHabitatType() == null) ? 0 : getHabitatType().hashCode());
        result = prime * result + ((getNumber() == null) ? 0 : getNumber().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getWeather() == null) ? 0 : getWeather().hashCode());
        result = prime * result + ((getMemo() == null) ? 0 : getMemo().hashCode());
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
        sb.append(", orderId=").append(orderId);
        sb.append(", familyId=").append(familyId);
        sb.append(", speciesId=").append(speciesId);
        sb.append(", watchPiId=").append(watchPiId);
        sb.append(", observationTime=").append(observationTime);
        sb.append(", observationPeriodBegin=").append(observationPeriodBegin);
        sb.append(", observationPeriodEnd=").append(observationPeriodEnd);
        sb.append(", observationAddress=").append(observationAddress);
        sb.append(", habitatType=").append(habitatType);
        sb.append(", number=").append(number);
        sb.append(", createTime=").append(createTime);
        sb.append(", weather=").append(weather);
        sb.append(", memo=").append(memo);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}