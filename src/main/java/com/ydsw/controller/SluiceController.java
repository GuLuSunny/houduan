package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Reservoir;
import com.ydsw.domain.Sluice;
import com.ydsw.service.SluiceService;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeography;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            PGgeometry pGgeography=(PGgeometry)map.get("geog");
            Geometry point=pGgeography.getGeometry().getFirstPoint();
            String wkt=point.toString();
            map.put("geog",wkt);
        }
        return ResultTemplate.success(sluiceList);
    }

    @PreAuthorize("hasAnyAuthority('api_sluice_uploadByShpfiles')")
    @PostMapping(value = "/api/sluice/uploadByShpfiles")
    public ResultTemplate<Object> uploadByShpfiles(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        if (fileGroup == null || fileGroup.length == 0) {
            return ResultTemplate.fail("请提交文件！");
        }
        String fileName = fileGroup[0].getOriginalFilename();
        for (int i = 1; i < fileGroup.length; i++) {
            if(!Objects.equals(fileGroup[i].getOriginalFilename(), fileName))
            {
                return ResultTemplate.fail("文件格式错误！");
            }
        }
        try {
            List<Sluice> sluiceList = ShpfileUtils.parseMultipleShpGroups(fileGroup, Sluice.class);
            for (Sluice  sluice: sluiceList) {
                sluice.getGeog().setSRID(4326);
                sluice.setCreateTime(new Date());
                sluice.setStatus(0);
            }
        } catch (IOException e) {
            return ResultTemplate.fail("文件："+fileGroup[0].getOriginalFilename()+"提交格式错误！");
        }

        return ResultTemplate.success("提交成功！");
    }
}
