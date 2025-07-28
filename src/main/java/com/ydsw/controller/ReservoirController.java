package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Reservoir;
import com.ydsw.service.ReservoirService;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
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
public class ReservoirController {
    @Autowired
    private ReservoirService reservoirService;

    @PostMapping(value = "/api/reservoir/getAllByConditions")
    public ResultTemplate<Object> getAllByConditions(@RequestBody JSONObject jsonObject) {
        Reservoir reservoir=jsonObject.toBean(Reservoir.class);
        List<Map<String,Object>> res=reservoirService.selectReservoirByConditons(reservoir);
        return LakesController.getRangeFromPGgeometryforMap(res);
    }

    @PreAuthorize("hasAnyAuthority('api_reservoir_uploadByShpfiles')")
    @PostMapping(value = "/api/reservoir/uploadByShpfiles")
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
        List<Reservoir> reservoirList = ShpfileUtils.parseMultipleShpGroups(fileGroup, Reservoir.class);
        for (Reservoir reservoir : reservoirList) {
            reservoir.getGeom().setSRID(4326);
            reservoir.setCreateTime(new Date());
            reservoir.setStatus(0);
        }
    } catch (IOException e) {
        return ResultTemplate.fail("文件："+fileGroup[0].getOriginalFilename()+"提交格式错误！");
    }

        return ResultTemplate.success("提交成功！");
    }
}
