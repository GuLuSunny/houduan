package com.ydsw.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 气象数据表
 * @TableName atmosphere
 */
@TableName(value ="atmosphere")
@Data
public class Atmosphere implements Serializable {
    /**
     * 气象观测地id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 气象观测地id外键
     */
    @TableField(value = "device_id")
    private Integer deviceId;

    /**
     * 风速（m/s）
     */
    @TableField(value = "wind_speed")
    private String windSpeed;

    /**
     * 雨量（mm）
     */
    @TableField(value = "rainfall")
    private String rainfall;

    /**
     * 大气温度（℃）
     */
    @TableField(value = "atmosphere_temperature")
    private String atmosphereTemperature;

    /**
     * 土壤温度（℃）
     */
    @TableField(value = "soil_temperature")
    private String soilTemperature;

    /**
     * 数字气压（hPa）
     */
    @TableField(value = "digital_pressure")
    private String digitalPressure;

    /**
     * 简易总辐射（W/m2）

     */
    @TableField(value = "simple_total_radiation")
    private String simpleTotalRadiation;

    /**
     * 风向（°）
     */
    @TableField(value = "wind_direction")
    private String windDirection;

    /**
     * 土壤湿度（%PH)
     */
    @TableField(value = "soil_humidity")
    private String soilHumidity;

    /**
     * 大气湿度（%PH）
     */
    @TableField(value = "atmosphere_humidity")
    private String atmosphereHumidity;

    /**
     * PM2.5
     */
    @TableField(value = "pm25")
    private String pm25;

    /**
     * 盐分（mg/L）
     */
    @TableField(value = "salinity")
    private String salinity;

    /**
     * 负氧离子（个）
     */
    @TableField(value = "negative_oxygen_ion")
    private String negativeOxygenIon;

    /**
     * 雨量累计（mm）
     */
    @TableField(value = "rainfall_accumulation")
    private String rainfallAccumulation;

    /**
     * 辐射累计（MJ/m2）
     */
    @TableField(value = "radiation_accumulation")
    private String radiationAccumulation;

    /**
     * PM10(ugm3）
     */
    @TableField(value = "pm10")
    private String pm10;

    /**
     * 实际数据观测时间
     */
    @TableField(value = "observation_time")
    private String observationTime;

    /**
     * 数据入库时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 数据来源
     */
    @TableField(value = "type")
    private String type;

    /**
     * 数据导入人用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 对应保存Excel的文件地址(可为空)
     */
    @TableField(value = "filepath")
    private String filepath;

    /**
     * 文件名
     */
    @TableField(value = "filename")
    private String filename;

    /**
     * 保存用户的id
     */
    @TableField(value = "create_userid")
    private String createUserid;

    /**
     * 是否已删除 0:未删除，1:已删除
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 联系地址
     */
    @TableField(value = "contact_address")
    private String contactAddress;

    /**
     * 制作单位
     */
    @TableField(value = "production_unit")
    private String productionUnit;

    /**
     * 联系邮箱
     */
    @TableField(value = "contact_email")
    private String contactEmail;

    /**
     * 是否公开
     */
    @TableField(value = "open")
    private Integer open;

    /**
    * 数据简介
    * */
    @TableField(value = "data_introduction")
    private String dataIntroduction;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    public Atmosphere() {}
    public void atmosphereAdd(Integer deviceId, Date createTime, String type, String userName, String filepath,String filename, String createUserid, Integer status, String contactPhone,
                              String contactAddress, String productionUnit, String contactEmail, Integer open, String dataIntroduction) {
        this.deviceId = deviceId;
        this.createTime = createTime;
        this.type = type;
        this.userName = userName;
        this.filepath = filepath;
        this.filename = filename;
        this.createUserid = createUserid;
        this.status = status;
        this.contactPhone = contactPhone;
        this.contactAddress = contactAddress;
        this.productionUnit = productionUnit;
        this.contactEmail = contactEmail;
        this.open = open;
        this.dataIntroduction = dataIntroduction;

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
        Atmosphere other = (Atmosphere) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getWindSpeed() == null ? other.getWindSpeed() == null : this.getWindSpeed().equals(other.getWindSpeed()))
                && (this.getRainfall() == null ? other.getRainfall() == null : this.getRainfall().equals(other.getRainfall()))
                && (this.getAtmosphereTemperature() == null ? other.getAtmosphereTemperature() == null : this.getAtmosphereTemperature().equals(other.getAtmosphereTemperature()))
                && (this.getSoilTemperature() == null ? other.getSoilTemperature() == null : this.getSoilTemperature().equals(other.getSoilTemperature()))
                && (this.getDigitalPressure() == null ? other.getDigitalPressure() == null : this.getDigitalPressure().equals(other.getDigitalPressure()))
                && (this.getSimpleTotalRadiation() == null ? other.getSimpleTotalRadiation() == null : this.getSimpleTotalRadiation().equals(other.getSimpleTotalRadiation()))
                && (this.getWindDirection() == null ? other.getWindDirection() == null : this.getWindDirection().equals(other.getWindDirection()))
                && (this.getSoilHumidity() == null ? other.getSoilHumidity() == null : this.getSoilHumidity().equals(other.getSoilHumidity()))
                && (this.getAtmosphereHumidity() == null ? other.getAtmosphereHumidity() == null : this.getAtmosphereHumidity().equals(other.getAtmosphereHumidity()))
                && (this.getPm25() == null ? other.getPm25() == null : this.getPm25().equals(other.getPm25()))
                && (this.getSalinity() == null ? other.getSalinity() == null : this.getSalinity().equals(other.getSalinity()))
                && (this.getNegativeOxygenIon() == null ? other.getNegativeOxygenIon() == null : this.getNegativeOxygenIon().equals(other.getNegativeOxygenIon()))
                && (this.getRainfallAccumulation() == null ? other.getRainfallAccumulation() == null : this.getRainfallAccumulation().equals(other.getRainfallAccumulation()))
                && (this.getRadiationAccumulation() == null ? other.getRadiationAccumulation() == null : this.getRadiationAccumulation().equals(other.getRadiationAccumulation()))
                && (this.getPm10() == null ? other.getPm10() == null : this.getPm10().equals(other.getPm10()))
                && (this.getObservationTime() == null ? other.getObservationTime() == null : this.getObservationTime().equals(other.getObservationTime()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
                && (this.getFilepath() == null ? other.getFilepath() == null : this.getFilepath().equals(other.getFilepath()))
                && (this.getFilename() == null ? other.getFilename() == null : this.getFilename().equals(other.getFilename()))
                && (this.getCreateUserid() == null ? other.getCreateUserid() == null : this.getCreateUserid().equals(other.getCreateUserid()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getContactPhone() == null ? other.getContactPhone() == null : this.getContactPhone().equals(other.getContactPhone()))
                && (this.getContactAddress() == null ? other.getContactAddress() == null : this.getContactAddress().equals(other.getContactAddress()))
                && (this.getProductionUnit() == null ? other.getProductionUnit() == null : this.getProductionUnit().equals(other.getProductionUnit()))
                && (this.getContactEmail() == null ? other.getContactEmail() == null : this.getContactEmail().equals(other.getContactEmail()))
                && (this.getOpen() == null ? other.getOpen() == null : this.getOpen().equals(other.getOpen()))
                && (this.getDataIntroduction() == null ? other.getDataIntroduction() == null : this.getDataIntroduction().equals(other.getDataIntroduction()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getWindSpeed() == null) ? 0 : getWindSpeed().hashCode());
        result = prime * result + ((getRainfall() == null) ? 0 : getRainfall().hashCode());
        result = prime * result + ((getAtmosphereTemperature() == null) ? 0 : getAtmosphereTemperature().hashCode());
        result = prime * result + ((getSoilTemperature() == null) ? 0 : getSoilTemperature().hashCode());
        result = prime * result + ((getDigitalPressure() == null) ? 0 : getDigitalPressure().hashCode());
        result = prime * result + ((getSimpleTotalRadiation() == null) ? 0 : getSimpleTotalRadiation().hashCode());
        result = prime * result + ((getWindDirection() == null) ? 0 : getWindDirection().hashCode());
        result = prime * result + ((getSoilHumidity() == null) ? 0 : getSoilHumidity().hashCode());
        result = prime * result + ((getAtmosphereHumidity() == null) ? 0 : getAtmosphereHumidity().hashCode());
        result = prime * result + ((getPm25() == null) ? 0 : getPm25().hashCode());
        result = prime * result + ((getSalinity() == null) ? 0 : getSalinity().hashCode());
        result = prime * result + ((getNegativeOxygenIon() == null) ? 0 : getNegativeOxygenIon().hashCode());
        result = prime * result + ((getRainfallAccumulation() == null) ? 0 : getRainfallAccumulation().hashCode());
        result = prime * result + ((getRadiationAccumulation() == null) ? 0 : getRadiationAccumulation().hashCode());
        result = prime * result + ((getPm10() == null) ? 0 : getPm10().hashCode());
        result = prime * result + ((getObservationTime() == null) ? 0 : getObservationTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getFilepath() == null) ? 0 : getFilepath().hashCode());
        result = prime * result + ((getFilename() == null) ? 0 : getFilename().hashCode());
        result = prime * result + ((getCreateUserid() == null) ? 0 : getCreateUserid().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getContactPhone() == null) ? 0 : getContactPhone().hashCode());
        result = prime * result + ((getContactAddress() == null) ? 0 : getContactAddress().hashCode());
        result = prime * result + ((getProductionUnit() == null) ? 0 : getProductionUnit().hashCode());
        result = prime * result + ((getContactEmail() == null) ? 0 : getContactEmail().hashCode());
        result = prime * result + ((getOpen() == null) ? 0 : getOpen().hashCode());
        result = prime * result + ((getDataIntroduction() == null) ? 0 : getDataIntroduction().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", windSpeed=").append(windSpeed);
        sb.append(", rainfall=").append(rainfall);
        sb.append(", atmosphereTemperature=").append(atmosphereTemperature);
        sb.append(", soilTemperature=").append(soilTemperature);
        sb.append(", digitalPressure=").append(digitalPressure);
        sb.append(", simpleTotalRadiation=").append(simpleTotalRadiation);
        sb.append(", windDirection=").append(windDirection);
        sb.append(", soilHumidity=").append(soilHumidity);
        sb.append(", atmosphereHumidity=").append(atmosphereHumidity);
        sb.append(", pm25=").append(pm25);
        sb.append(", salinity=").append(salinity);
        sb.append(", negativeOxygenIon=").append(negativeOxygenIon);
        sb.append(", rainfallAccumulation=").append(rainfallAccumulation);
        sb.append(", radiationAccumulation=").append(radiationAccumulation);
        sb.append(", pm10=").append(pm10);
        sb.append(", observationTime=").append(observationTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", type=").append(type);
        sb.append(", userName=").append(userName);
        sb.append(", filepath=").append(filepath);
        sb.append(", filename=").append(filename);
        sb.append(", createUserid=").append(createUserid);
        sb.append(", status=").append(status);
        sb.append(", contactPhone=").append(contactPhone);
        sb.append(", contactAddress=").append(contactAddress);
        sb.append(", productionUnit=").append(productionUnit);
        sb.append(", contactEmail=").append(contactEmail);
        sb.append(", open=").append(open);
        sb.append(", dataIntroduction=").append(dataIntroduction);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}