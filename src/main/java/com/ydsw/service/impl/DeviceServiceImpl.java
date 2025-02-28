package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.DeviceMapper;
import com.ydsw.domain.Device;
import com.ydsw.domain.Purview;
import com.ydsw.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author 俞笙
* @description 针对表【device(观测站的设备位置表)】的数据库操作Service实现
* @createDate 2024-09-11 19:42:45
*/
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
    implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public List<Device> fetchDeviceData(Integer id, String deviceName,String type) {
        return deviceMapper.selectDeviceData(id,deviceName,type);
    }

    @Override
    public IPage<Map<String, Object>> getDeviceListPage(int currentPage, int pageSize, Device device) {
        IPage<Purview> page = new Page<>(currentPage, pageSize);
        return deviceMapper.getDeviceListPage(page, device);
    }

    @Override
    public List<Map<String, Object>> queryDeviceNums(List<String> typeList) {
        return deviceMapper.queryDeviceNums(typeList);
    }
}




