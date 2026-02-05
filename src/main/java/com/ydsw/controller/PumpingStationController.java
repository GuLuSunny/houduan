package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.ydsw.domain.Channel;
import com.ydsw.domain.PumpingStation;
import com.ydsw.service.PumpingStationService;
import com.ydsw.utils.ResultTemplate;
import com.ydsw.utils.ShpUploadHelper;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeography;
import net.postgis.jdbc.geometry.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ydsw.controller.SluiceController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
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

    @PreAuthorize("hasAnyAuthority('api_pumpingStation_uploadByShpfiles')")
    @PostMapping(value = "/api/pumpingStation/uploadByShpfiles")
    public ResultTemplate<Object> uploadByShpfiles(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        try {
            ResultTemplate<List<PumpingStation>> result = ShpUploadHelper.handleShpUpload(
                    fileGroup,
                    PumpingStation.class,
                    "geog",  // 注意：PumpingStation使用geog字段
                    null
            );

            if (!result.isSuccess()) {
                return ResultTemplate.fail(result.getMessage());
            }

            List<PumpingStation> pumpingStationList = result.getData();
            System.out.println(pumpingStationList);

            return ResultTemplate.success("提交成功！");

        } catch (IOException e) {
            return ResultTemplate.fail("文件处理失败: " + e.getMessage());
        }
    }
}
