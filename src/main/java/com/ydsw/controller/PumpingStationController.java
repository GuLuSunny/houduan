package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Channel;
import com.ydsw.domain.PumpingStation;
import com.ydsw.service.PumpingStationService;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeography;
import net.postgis.jdbc.geometry.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ydsw.controller.SluiceController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class PumpingStationController {
    @Autowired
    private PumpingStationService pumpingStationService;

    @PostMapping(value = "/api/pumpingStation/getAllPumpingStationByCondition")
    public ResultTemplate<Object> getAllPumpingStationByCondition(@RequestBody JSONObject jsonObject) {
        PumpingStation pumpingStation = jsonObject.toBean(PumpingStation.class);
        List<Map<String,Object>> res=pumpingStationService.selectAllPumpingStationByCondition(pumpingStation);
        return SluiceController.getPointFromPGgeographyFromMap(res);
    }

    @PostMapping(value = "/api/pumpingStation/uploadByShpfiles")
    public ResultTemplate<Object> uploadByShpfiles(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        if(fileGroup==null||fileGroup.length==0)
        {
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

            List<PumpingStation> pumpingStationList= ShpfileUtils.parseMultipleShpGroups(fileGroup,PumpingStation.class);
            for (PumpingStation pumpingStation : pumpingStationList) {
                pumpingStation.getGeog().setSRID(4326);
                pumpingStation.setCreateTime(new Date());
                pumpingStation.setStatus(0);
            }
            System.out.println(pumpingStationList);

        } catch (IOException e) {
            return ResultTemplate.fail("文件："+fileGroup[0].getOriginalFilename()+"提交格式错误！");
        }

        return ResultTemplate.success("提交成功！");
    }
}
