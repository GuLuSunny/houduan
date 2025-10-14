package com.ydsw.controller;

import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.LandUseArea;
import com.ydsw.service.LandUseAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class LandUseAreaController {
    @Autowired
    private LandUseAreaService landUseAreaService;
    /*
    * 根据地类编码或地类名称返回土地利用状况（暂定，不添加）
    * */
    @PreAuthorize("hasAnyAuthority('api_LandUseArea')")
    @PostMapping(value = "/api/LandUseArea")
    public ResultTemplate<Object> getLandUseAreaById(@RequestBody Map<String, String> requestBody)
    {
        Integer id = Integer.valueOf(requestBody.get("landUseId"));
        String landName=requestBody.get("landName");

        List<LandUseArea> landUseAreaList = landUseAreaService.fetchLandUseAreasByidOrName(id,landName);
        return ResultTemplate.success(landUseAreaList);
    }
}
