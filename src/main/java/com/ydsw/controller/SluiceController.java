package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Sluice;
import com.ydsw.service.SluiceService;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeography;
import net.postgis.jdbc.geometry.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class SluiceController {
    @Autowired
    SluiceService sluiceService;

    @PostMapping("/api/sluice/selectAllByConditions")
    public ResultTemplate<Object> selectAllByConditions(@RequestBody JSONObject JSONObject)
    {
        Sluice sluice = JSONUtil.toBean(JSONObject,Sluice.class);
        List<Map<String,Object>> sluiceList=sluiceService.selectBySluice(sluice);
        return getPointFromPGgeographyFromMap(sluiceList);
    }

    public static ResultTemplate<Object> getPointFromPGgeographyFromMap(List<Map<String, Object>> sluiceList) {
        for(Map<String,Object> map:sluiceList)
        {
            PGgeography pGgeography=(PGgeography)map.get("geog");
            Geometry point=pGgeography.getGeometry().getFirstPoint();
            String wkt=point.toString();
            map.put("geog",wkt);
        }
        return ResultTemplate.success(sluiceList);
    }
}
