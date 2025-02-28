package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class DataManagementController {
    @Autowired
    private TbFlowService tbFlowService;
    @Autowired
    private TbWaterLevelService tbWaterLevelService;
    @Autowired
    private AtmosphereService atmosphereService;
    @Autowired
    private SpectralReflectanceService spectralReflectanceService;
    @Autowired
    private WaterPhysicochemistryService waterPhysicochemistryService;
    @Autowired
    private SateliteRemoteSensingService sateliteRemoteSensingService;
    @Autowired
    private DroneImageService droneImageService;

    /**
     * 根据type查询不同数据表，
     * 径流-01
     * 水位-02
     * 气象-03
     * 光谱-04
     * 理化-05
     * 卫星遥感-06
     * 无人机-07
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_dataManagement_findDataPageByCondition')")
    @PostMapping(value = "/api/dataManagement/findDataPageByCondition")
    public ResultTemplate<Object> findDataPageByCondition(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if (currentPage < 1) {
            return ResultTemplate.fail("无效页码！");
        } else if (pageSize < 1) {
            return ResultTemplate.fail("无效单位！");
        }
        String type = jsonObject.getStr("type");
        String time = jsonObject.getStr("time");
        String filepath = jsonObject.getStr("filepath");
        Integer opens = jsonObject.getInt("opens");

        IPage<Map<String, Object>> page = new Page<>();
        if ("01".equals(type)) {
            page = tbFlowService.findDataPageByCondition(currentPage, pageSize, time, filepath, opens);
        }
        if ("02".equals(type)) {
            page = tbWaterLevelService.findDataPageByCondition(currentPage, pageSize, time, filepath, opens);
        }
        if ("03".equals(type)) {
            page = atmosphereService.findDataPageByCondition(currentPage, pageSize, time, filepath, opens);
        }
        if ("04".equals(type)) {
            page = spectralReflectanceService.findDataPageByCondition(currentPage, pageSize, time, filepath, opens);
        }
        if ("05".equals(type)) {
            page = waterPhysicochemistryService.findDataPageByCondition(currentPage, pageSize, time, filepath, opens);
        }
        if ("06".equals(type)) {
            page = sateliteRemoteSensingService.findDataPageByCondition(currentPage, pageSize, time, filepath, opens);
        }
        if ("07".equals(type)) {
            page = droneImageService.findDataPageByCondition(currentPage, pageSize, time, filepath, opens);
        }
        List<Map<String, Object>> records = page.getRecords();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> record = records.get(i);
            AtmosphereController.renameFilepath(record);
        }
        return ResultTemplate.success(page);
    }


    //处理数据公开与否
    @PreAuthorize("hasAnyAuthority('api_dataOpen')")
    @RequestMapping(value = "/api/dataOpen")
    @Transactional
    public ResultTemplate<Object> openHandle(@RequestBody JSONObject jsonObject) {
        List<String> idArray = jsonObject.getBeanList("ids", String.class); // 获取id列表
        String filepath = jsonObject.getStr("filepath");
        String dateSelected = jsonObject.getStr("dateSelected"); // 获取选中的日期
        String handle = jsonObject.getStr("handle"); // 获取操作类型（publish 或 unpublish）
        String type = jsonObject.getStr("type");

        if ((idArray == null || idArray.isEmpty()) &&
                (filepath == null || filepath.trim().isEmpty()) &&
                (dateSelected == null || "".equals(dateSelected.trim()))) {
            return ResultTemplate.fail("参数出错！");
        }

        // 设置 open 的目标值
        int openValue = "publish".equals(handle) ? 1 : 0;

        // 构建idList
        List<Integer> idList = new ArrayList<>();
        if (idArray != null && !idArray.isEmpty()) {
            for (String s : idArray) {
                String[] idStrArray = s.split(",");
                for (String idStr : idStrArray) {
                    try {
                        idList.add(Integer.parseInt(idStr.trim()));
                    } catch (NumberFormatException e) {
                        // 如果字符串无法转换为整数,则跳过该值
                        continue;
                    }
                }
            }
        }

        try {
            // 判断type值并调用相应服务
            switch (type) {
                case "01":
                    tbFlowService.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue, filepath);
                    return ResultTemplate.success("操作成功!");
                case "02":
                    tbWaterLevelService.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue, filepath);
                    return ResultTemplate.success("操作成功!");
                case "03":
                    atmosphereService.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue, filepath);
                    return ResultTemplate.success("操作成功!");
                case "04":
                    spectralReflectanceService.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue, filepath);
                    return ResultTemplate.success("操作成功!");
                case "05":
                    waterPhysicochemistryService.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue, filepath);
                    return ResultTemplate.success("操作成功!");
                case "06":
                    sateliteRemoteSensingService.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue, filepath);
                    return ResultTemplate.success("操作成功!");
                case "07":
                    droneImageService.updateOpenStatusByFilepathsAndDate(idList, dateSelected, openValue, filepath);
                    return ResultTemplate.success("操作成功!");
                default:
                    return ResultTemplate.fail("未知的数据类型！");
            }
        } catch (Exception e) {
            return ResultTemplate.fail("操作失败！");
        }
    }
}
