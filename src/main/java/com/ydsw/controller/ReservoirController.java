package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.ydsw.domain.Reservoir;
import com.ydsw.service.ReservoirService;
import com.ydsw.utils.ResultTemplate;
import com.ydsw.utils.ShpUploadHelper;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        try {
            ResultTemplate<List<Reservoir>> result = ShpUploadHelper.handleShpUpload(
                    fileGroup,
                    Reservoir.class,
                    "geom",
                    null
            );

            if (!result.isSuccess()) {
                return ResultTemplate.fail(result.getMessage());
            }

            List<Reservoir> reservoirList = result.getData();
            System.out.println(reservoirList);

            return ResultTemplate.success("提交成功！");

        } catch (IOException e) {
            return ResultTemplate.fail("文件处理失败: " + e.getMessage());
        }
    }
}
