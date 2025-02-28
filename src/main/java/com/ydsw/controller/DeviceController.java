package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Device;
import com.ydsw.domain.DeviceIntroduceTable;
import com.ydsw.service.DeviceIntroduceTableService;
import com.ydsw.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceIntroduceTableService deviceIntroduceTableService;

    @PreAuthorize("hasAnyAuthority('api_device_queryDeviceByMultiWord')")
    @ResponseBody
    @PostMapping(value = "/api/device/queryDeviceByMultiWord")
    public ResultTemplate<Object> getDevice(@RequestBody Map<String, String> requestBody) {
        String idStr = requestBody.get("id");
        Integer id = null;
        if (idStr != null && !"".equals(idStr.trim())) {
            id = Integer.parseInt(idStr);
        }
        String deviceName = requestBody.get("deviceName");
        String type = requestBody.get("type");
        List<Device> deviceList = deviceService.fetchDeviceData(id, deviceName, type);
        return ResultTemplate.success(deviceList);
    }

    /**
     * 插入
     * //@param jsonArray
     * //@return
     */
    @PreAuthorize("hasAnyAuthority('api_device_insertDevice')")
    @PostMapping(value = "/api/device/insertDevice")
    @Transactional
    public ResultTemplate<Object> deviceInsert(@RequestBody JSONObject jsonObject) {
//        log.info(JSONUtil.toJsonStr(jsonArray));
        Device device = JSONUtil.toBean(jsonObject, Device.class);
        Boolean flag = deviceService.save(device);
        if (flag) {
            return ResultTemplate.success();
        }
        return ResultTemplate.fail();
    }

    /**
     * 查询设备分页列表
     *
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_device_getDeviceListPage')")
    @PostMapping(value = "/api/device/getDeviceListPage")
    @ResponseBody
    public ResultTemplate<Object> getDeviceListPage(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        Device device = JSONUtil.toBean(jsonObject, Device.class);
        IPage<Map<String, Object>> rolePage = deviceService.getDeviceListPage(currentPage, pageSize, device);
        return ResultTemplate.success(rolePage);
    }

    /**
     * 根据设备id更新信息
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_device_updateDeviceInfo')")
    @PostMapping(value = "/api/device/updateDeviceInfo")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> updateDeviceInfo(@RequestBody JSONObject jsonObject) {
        Device device = JSONUtil.toBean(jsonObject, Device.class);
        deviceService.updateById(device);
        return ResultTemplate.success();
    }

    /*
     * 根据id列表删除
     * */
    @PreAuthorize("hasAnyAuthority('api_device_deleteDeviceDataById')")
    @RequestMapping(value = "/api/device/deleteDeviceDataById")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> deleteDeviceDataById(@RequestBody JSONObject jsonObject) {
        List<Integer> idArray = jsonObject.getBeanList("ids", Integer.class);//id列表
        deviceService.removeBatchByIds(idArray);
        return ResultTemplate.success("删除成功！");
    }

    /*
     * 大屏，设备观测点信息
     * */
    @PreAuthorize("hasAnyAuthority('api_deviceIntroduceTable_findDevicePositionAndIntroduce')")
    @ResponseBody
    @PostMapping(value = "/api/deviceIntroduceTable/findDevicePositionAndIntroduce")
    public ResultTemplate<Object> findDevicePositionAndIntroduce() {
        log.info("查询设备观测点信息");
        List<DeviceIntroduceTable> devicePositionAndIntroduceList = deviceIntroduceTableService.list();
       for (DeviceIntroduceTable deviceIntroduceTable : devicePositionAndIntroduceList) {
           String deviceIntroduce = (String) deviceIntroduceTable.getDeviceIntroduce();
            // 使用Hutool的JSONUtil解析字符串为JSONArray
           JSONArray jsonArray = JSONUtil.parseArray(deviceIntroduce);
           deviceIntroduceTable.setDeviceIntroduce(jsonArray);
       }
        return ResultTemplate.success(devicePositionAndIntroduceList);
    }
    /**
     * 大屏，设备观测点个数信息
     * */
    @PreAuthorize("hasAnyAuthority('api_device_queryDeviceNums')")
    @ResponseBody
    @PostMapping(value = "/api/device/queryDeviceNums")
    public ResultTemplate<Object> queryDeviceNums(@RequestBody JSONObject jsonObject) {
        List<String >  typeList= (List<String>) jsonObject.get("type");
        List<Map<String ,Object>> deviceNumList = deviceService.queryDeviceNums(typeList);
        return ResultTemplate.success(deviceNumList);
    }
}
