package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/9/30  9:07
 * @Version 1.0
 */
@Slf4j
@Controller
@RequestMapping(value = "/api/utils")
public class UtilsController {
    @Autowired
    private SpectralReflectanceService spectralReflectanceService;
    @Autowired
    private WaterPhysicochemistryService waterPhysicochemistryService;
    @Autowired
    private AtmosphereService atmosphereService;
    @Autowired
    private TbFlowService tbFlowService;
    @Autowired
    private TbWaterLevelService tbWaterLevelService;
    @Autowired
    private WetlandSoilMonitoringIndicatorsService wetlandSoilMonitoringIndicatorsService;//湿地土壤
    @Autowired
    private VegetationMonitoringIndicatorsService vegetationMonitoringIndicatorsService;//植被监测指标
    @Autowired
    private DroneImageService droneImageService;
    @Autowired
    private SateliteRemoteSensingService sateliteRemoteSensingService;
    @Autowired
    private ModelProductService modelProductService;
    /**
     * 通过类型，查询时间列表
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_utils_getTimesByType')")
    @PostMapping(value = "/getTimesByType")
    @ResponseBody
    public ResultTemplate<Object> getTimesByType(@RequestBody JSONObject jsonObject) {
        String filepath = (String) jsonObject.get("filepath");
        String type = (String) jsonObject.get("type");
        if (type == null || "".equals(type)) {
            return ResultTemplate.fail("参数错误");
        }
        Map map = new HashMap();
        map.put("type", jsonObject.get("type"));
        List<String> stringList = new ArrayList<>();
        if ("guangpu".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = spectralReflectanceService.fetchObservationTimeByYear();
            } else if ("month".equals(searchTimeType)) {
                stringList = spectralReflectanceService.fetchObservationTimeByMonth();
            } else if ("day".equals(searchTimeType)) {
                stringList = spectralReflectanceService.fetchObservationTimeByDay();
            } else {
                stringList = spectralReflectanceService.fetchObservationTime();
            }
        }
        if ("shuitilihua".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = waterPhysicochemistryService.fetchObservationTimeByYear();
            } else if ("month".equals(searchTimeType)) {
                stringList = waterPhysicochemistryService.fetchObservationTimeByMonth();
            } else if ("day".equals(searchTimeType)) {
                stringList = waterPhysicochemistryService.fetchObservationTimeByDay();
            } else {
                stringList = waterPhysicochemistryService.fetchObservationTime();
            }
        }
        if ("qixiang".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = atmosphereService.fetchObservationTimeByYear();
            } else if ("month".equals(searchTimeType)) {
                stringList = atmosphereService.fetchObservationTimeByMonth();
            } else {
                stringList = atmosphereService.fetchObservationTime();
            }
        }
        if ("jingliu".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = tbFlowService.fetchObservationTimeByYear1(filepath);
            } else if ("month".equals(searchTimeType)) {
                stringList = tbFlowService.fetchObservationTimeByMonth1(filepath);
            }
        }
        if ("shuiwei".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = tbWaterLevelService.fetchObservationTimeByYear1(filepath);
            } else if ("month".equals(searchTimeType)) {
                stringList = tbWaterLevelService.fetchObservationTimeByMonth1(filepath);
            }
        }
        if ("shiditurang".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = wetlandSoilMonitoringIndicatorsService.fetchObservationTimeByYear();
            } else if ("month".equals(searchTimeType)) {
                stringList = wetlandSoilMonitoringIndicatorsService.fetchObservationTimeByMonth();
            }else {
                stringList = wetlandSoilMonitoringIndicatorsService.fetchObservationTimeByDay();
            }
        }
        if ("shidizhibei".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = vegetationMonitoringIndicatorsService.fetchObservationTimeByYear();
            } else if ("month".equals(searchTimeType)) {
                stringList = vegetationMonitoringIndicatorsService.fetchObservationTimeByMonth();
            }else {
                stringList = vegetationMonitoringIndicatorsService.fetchObservationTimeByDay();
            }
        }
        if ("weixing".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = sateliteRemoteSensingService.fetchObservationTimeByYear();
            } else if ("month".equals(searchTimeType)) {
                stringList = sateliteRemoteSensingService.fetchObservationTimeByMonth();
            }
        }
        if ("wurenji".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = droneImageService.fetchObservationTimeByYear();
            } else if ("month".equals(searchTimeType)) {
                stringList = droneImageService.fetchObservationTimeByMonth();
            }
        }
        if ("modelproducts".equals(type)) {
            String searchTimeType = (String) jsonObject.get("searchTimeType");
            if ("year".equals(searchTimeType)) {
                stringList = modelProductService.fetchObservationTimeByYear();
            } else if ("month".equals(searchTimeType)) {
                stringList = modelProductService.fetchObservationTimeByMonth();
            }else
            {
                stringList = modelProductService.fetchObservationTime();
            }
        }
        map.put("date", stringList);
        return ResultTemplate.success(map);
    }
}
