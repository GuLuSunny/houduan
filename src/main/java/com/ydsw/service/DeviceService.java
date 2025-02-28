package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.Device;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【device(观测站的设备位置表)】的数据库操作Service
* @createDate 2024-09-11 19:42:45
*/
public interface DeviceService extends IService<Device> {

    List<Device> fetchDeviceData(Integer id, String deviceName,String type);

    IPage<Map<String, Object>> getDeviceListPage(int currentPage, int pageSize, Device device);

    List<Map<String, Object>> queryDeviceNums(List<String> typeList);
}
