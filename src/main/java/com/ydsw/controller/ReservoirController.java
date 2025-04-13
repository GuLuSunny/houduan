package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Reservoir;
import com.ydsw.service.ReservoirService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ReservoirController {
    @Autowired
    private ReservoirService reservoirService;

    @PostMapping(value = "/api/reservoir/getAllByConditions")
    public ResultTemplate<Object> getAllByConditions(@RequestBody JSONObject jsonObject) {
        Reservoir reservoir=jsonObject.toBean(Reservoir.class);
        List<Map<String,Object>> res=reservoirService.selectReservoirByConditons(reservoir);
        return LakesController.getRangeFromPGgeometryforMap(res);
    }
}
