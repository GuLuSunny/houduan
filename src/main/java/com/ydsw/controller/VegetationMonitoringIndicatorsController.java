package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.VegetationMonitoringIndicators;
import com.ydsw.service.DeviceService;
import com.ydsw.service.VegetationMonitoringIndicatorsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ydsw.dao.VegetationMonitoringIndicatorsMapper;

@Slf4j
@RestController
public class VegetationMonitoringIndicatorsController {

    // 注入Mapper（纯MyBatis使用）
    @Autowired
    private VegetationMonitoringIndicatorsMapper vegetationMonitoringIndicatorsMapper;

    @Autowired
    private DeviceService deviceService;

    // 修复：补充@Autowired注解，否则会空指针
    @Autowired
    private VegetationMonitoringIndicatorsService vegetationMonitoringIndicatorsService;

    /**
     * 获取湿地监测表中所有不重复的植被种类（纯 MyBatis 实现）
     * @return 逗号分隔的唯一植被种类字符串
     */
    //@PreAuthorize("hasAnyAuthority('api_monitoring_plantGetDistinctSpecies')")
    @PostMapping(value = "/api/monitoring/plantGetDistinctSpecies")
    // 修正：返回类型改为ResultTemplate<String>，匹配逗号分隔字符串
    public ResultTemplate<String> plantGetDistinctSpecies() {
        // 纯MyBatis调用：直接调用Mapper中注解定义的SQL方法，抛弃MyBatis-Plus的QueryWrapper
        List<String> speciesList = vegetationMonitoringIndicatorsMapper.selectDistinctVegetationSpecies();

        // 过滤空值+拼接为逗号分隔字符串（双重保障，避免无效数据）
        String result = speciesList.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.joining(","));

        return ResultTemplate.success(result);
    }

    /*
     * 根据时间查询湿地植被数据
     */
    @PostMapping(value = "/api/monitoring/plantGetData")
    public ResultTemplate<Object> plantGetData(@RequestBody Map<String, String> requestBody) {
        String time = requestBody.get("time");
        String plant = requestBody.get("plant");
        List<VegetationMonitoringIndicators> vegetationMonitoringIndicatorsList = vegetationMonitoringIndicatorsService.fetchDataByObservationTime(time, plant, null);
        return ResultTemplate.success(vegetationMonitoringIndicatorsList);
    }

    /*
     * @param jsonArray
     * 根据日期分页模糊查询
     * 气象
     * */
    @PreAuthorize("hasAnyAuthority('api_plantMonitoring_pageQurey')")
    @PostMapping(value = "/api/plantMonitoring/pageQurey")
    public ResultTemplate<Object> pageQurey(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if (currentPage < 1) {
            return ResultTemplate.fail("无效页码！");
        } else if (pageSize < 1) {
            return ResultTemplate.fail("无效单位！");
        }

        VegetationMonitoringIndicators vegetationMonitoringIndicatorsClass = new VegetationMonitoringIndicators();
        String investigationTime = (String) jsonObject.get("observationTime");
        vegetationMonitoringIndicatorsClass.setInvestigationTime(investigationTime);

        IPage<Map<String, Object>> page = vegetationMonitoringIndicatorsService.fetchDataByObservationTimeAndFilepath(currentPage, pageSize, vegetationMonitoringIndicatorsClass);
        List<Map<String, Object>> records = page.getRecords();

        return ResultTemplate.success(page);
    }

    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @PreAuthorize("hasAnyAuthority('api_plantMonitoring_DelByIds')")
    @RequestMapping(value = "/api/plantMonitoring/DelByIds")
    @Transactional
    public ResultTemplate<Object> SpeDelById(@RequestBody JSONObject jsonObject) {
        List<String> idArray = jsonObject.getBeanList("ids", String.class);//id列表
        String dateSelected = jsonObject.getStr("observationTime");
        if ((idArray == null || idArray.isEmpty()) && (dateSelected == null || "".equals(dateSelected.trim()))) {
            return ResultTemplate.fail("参数出错！");
        }
        if (idArray != null && idArray.isEmpty()) {
            //根据条件删除
            vegetationMonitoringIndicatorsService.delByIdList(new ArrayList<>(), dateSelected, "");
            return ResultTemplate.success("删除成功！");
        }
        List<Integer> idList = new ArrayList<>();
        for (String s : idArray) {
            String[] ssplit = s.split("-");
            for (String s1 : ssplit) {
                idList.add(Integer.parseInt(s1));
            }
        }
        try {
            vegetationMonitoringIndicatorsService.delByIdList(idList, dateSelected, "");
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }

    /*
     * 表单插入
     */
    @PreAuthorize("hasAnyAuthority('api_monitoring_plant_insert')")
    @PostMapping(value = "/api/monitoring/plant/insert")
    @Transactional
    public ResultTemplate<Object> vegetationMonitoringIndicators_insert(@RequestBody JSONArray jsonArray) {
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        List<VegetationMonitoringIndicators> vegetationMonitoringIndicatorsList = new ArrayList<>();
        VegetationMonitoringIndicators vegetationMonitoringIndicators = JSONUtil.toBean(jsonObject.toString(), VegetationMonitoringIndicators.class);
        vegetationMonitoringIndicators.setCreateTime(new Date());
        vegetationMonitoringIndicators.setStatus(0);
        vegetationMonitoringIndicators.setType("form");
        vegetationMonitoringIndicators.setMemo(null);
        vegetationMonitoringIndicatorsList.add(vegetationMonitoringIndicators);
        List<VegetationMonitoringIndicators> vegetationMonitoringIndicators1 =
                vegetationMonitoringIndicatorsService.fetchDataByObservationTime
                        (vegetationMonitoringIndicatorsList.get(0).getInvestigationTime(),
                                vegetationMonitoringIndicatorsList.get(0).getVegetationSpecies(),
                                vegetationMonitoringIndicatorsList.get(0).getDeviceId());
        if (!vegetationMonitoringIndicators1.isEmpty()) {
            return ResultTemplate.fail("数据已存在!");
        }
        boolean flag = vegetationMonitoringIndicatorsService.saveBatch(vegetationMonitoringIndicatorsList);
        if (flag) {
            return ResultTemplate.success();
        }
        return ResultTemplate.fail();
    }

}