package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.Device;
import com.ydsw.domain.Purview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【device(观测站的设备位置表)】的数据库操作Mapper
* @createDate 2024-09-11 19:42:45
* @Entity com.ydsw.domain.Device
*/
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    List<Device> selectDeviceData(@Param("id") Integer id, @Param("deviceName") String deviceName, @Param("type") String type);

    IPage<Map<String, Object>> getDeviceListPage(IPage<Purview> page, @Param("device") Device device);

    List<Map<String, Object>> queryDeviceNums(@Param("typeList")List<String> typeList);
}




