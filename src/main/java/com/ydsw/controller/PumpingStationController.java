package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.PumpingStation;
import com.ydsw.service.PumpingStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PumpingStationController {
    @Autowired
    private PumpingStationService pumpingStationService;

    @PostMapping(value = "/api/pumpingStation/getAllPumpingStationByCondition")
    public ResultTemplate<Object> getAllPumpingStationByCondition(@RequestBody JSONObject jsonObject) {
        PumpingStation pumpingStation = jsonObject.toBean(PumpingStation.class);
        List<PumpingStation> res=pumpingStationService.selectAllPumpingStationByCondition(pumpingStation);
        return ResultTemplate.success(res);
    }
}
