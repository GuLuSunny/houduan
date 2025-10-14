package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.WetlandSoilMonitoringIndicators;
import com.ydsw.service.DeviceService;
import com.ydsw.service.WetlandSoilMonitoringIndicatorsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
public class WetlandSoilMonitoringIndicatorsController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private WetlandSoilMonitoringIndicatorsService wetlandSoilMonitoringIndicatorsService;
    /*
     * 根据时间查询湿地土壤数据
     */
    @PostMapping(value = "/api/monitoring/wetlandSoilGetData")
    public ResultTemplate<Object> wetlandSoilGetData(@RequestBody Map<String, String> requestBody) {
        String time = requestBody.get("time");
        WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicators = new WetlandSoilMonitoringIndicators();
        wetlandSoilMonitoringIndicators.setInvestigationTime(time);
        // Fetch data based on the given time
        List<WetlandSoilMonitoringIndicators> wetlandSoilMonitoringIndicatorsList =
                wetlandSoilMonitoringIndicatorsService.fetchDataByTimeAndPlant(wetlandSoilMonitoringIndicators);

        // Sort the list by depth_range
        wetlandSoilMonitoringIndicatorsList.sort(Comparator.comparing(WetlandSoilMonitoringIndicators::getDepthRange));

        // Return the sorted data in the ResultTemplate format
        return ResultTemplate.success(wetlandSoilMonitoringIndicatorsList);
    }
    /*
     * @param jsonArray
     * 根据日期分页模糊查询
     * 湿地
     * */
    @PostMapping(value = "/api/wetlandSoilMonitoring/pageQurey")
    public ResultTemplate<Object> pageQurey(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if(currentPage < 1){
            return ResultTemplate.fail("无效页码！");
        }else if(pageSize < 1){
            return ResultTemplate.fail("无效单位！");
        }

        WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicatorsClass = new WetlandSoilMonitoringIndicators();
        String investigationTime = (String) jsonObject.get("observationTime");
        wetlandSoilMonitoringIndicatorsClass.setInvestigationTime(investigationTime);

        IPage<Map<String, Object>> page = wetlandSoilMonitoringIndicatorsService.fetchDataByObservationTimeAndFilepath(currentPage, pageSize, wetlandSoilMonitoringIndicatorsClass);
        List<Map<String, Object>> records = page.getRecords();

        return ResultTemplate.success(page);
    }

    /*
     * 根据id删除和查询条件删除，若id列表存在，只根据id列表删除，否则，根据条件删除，必须存在至少一个删除条件
     * */
    @RequestMapping(value = "/api/wetlandSoilMonitoring/DelByIds")
    public ResultTemplate<Object> SpeDelById(@RequestBody JSONObject jsonObject) {
        List<String> idArray = jsonObject.getBeanList("ids", String.class);//id列表
        String dateSelected = jsonObject.getStr("observationTime");
        if ((idArray == null || idArray.isEmpty())  && (dateSelected == null || dateSelected.trim().isEmpty())) {
            return ResultTemplate.fail("参数出错！");
        }
        if (idArray != null && idArray.isEmpty()) {
            //根据条件删除
            wetlandSoilMonitoringIndicatorsService.delByIdList(new ArrayList<>(), dateSelected, "");
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
            wetlandSoilMonitoringIndicatorsService.delByIdList(idList, dateSelected, "");
        } catch (Exception e) {
            return ResultTemplate.fail("删除时出错！");
        }
        return ResultTemplate.success("删除成功！");
    }

    /*
     * 表单插入
     */
    @PostMapping(value = "/api/monitoring/wetlandSoil/insert")
    public ResultTemplate<Object> wetlandSoilMonitoringIndicators_insert(@RequestBody JSONArray jsonArray) {

        List<WetlandSoilMonitoringIndicators> wetlandSoilMonitoringIndicatorsList = null;
        wetlandSoilMonitoringIndicatorsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            WetlandSoilMonitoringIndicators wetlandSoilMonitoringIndicators = new WetlandSoilMonitoringIndicators();
            wetlandSoilMonitoringIndicators=JSONUtil.toBean(jsonObject.toString(), WetlandSoilMonitoringIndicators.class);

            wetlandSoilMonitoringIndicators.setCreateTime(new Date());
            wetlandSoilMonitoringIndicators.setStatus(0);
            wetlandSoilMonitoringIndicators.setType("form");
            wetlandSoilMonitoringIndicators.setMemo(null);
            List<WetlandSoilMonitoringIndicators> wetlist=wetlandSoilMonitoringIndicatorsService.fetchDataByTimeAndPlant(wetlandSoilMonitoringIndicators);
            if(!wetlist.isEmpty())
            {
                return ResultTemplate.fail("数据已存在!");
            }
            wetlandSoilMonitoringIndicatorsList.add(wetlandSoilMonitoringIndicators);
        }
        boolean flag = wetlandSoilMonitoringIndicatorsService.saveBatch(wetlandSoilMonitoringIndicatorsList);
        if (flag) {
            return ResultTemplate.success();
        }
        return ResultTemplate.fail();
    }
}
