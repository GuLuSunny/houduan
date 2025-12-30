package com.ydsw.controller;

import com.ydsw.domain.Farmland;
import com.ydsw.domain.Forest;
import com.ydsw.service.ForestService;
import com.ydsw.utils.ResultTemplate;
import com.ydsw.utils.ShpUploadHelper;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class ForestController {
    @Autowired
    private ForestService forestService;

    @PostMapping(value = "/api/forest/uploadByShpfiles")
    public ResultTemplate<Object> uploadByShpfiles(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        try {
            ResultTemplate<List<Forest>> result = ShpUploadHelper.handleShpUpload(
                    fileGroup,
                    Forest.class,
                    "geom",
                    null
            );

            if (!result.isSuccess()) {
                return ResultTemplate.fail(result.getMessage());
            }

            List<Forest> forestList = result.getData();

            // 额外处理SRID（如果需要）
            for (Forest forest : forestList) {
                if (forest.getGeom() != null) {
                    forest.getGeom().setSRID(4326);
                }
            }

            forestService.saveBatch(forestList);
            System.out.println("forestList:"+forestList);

            return ResultTemplate.success("提交成功！");

        } catch (IOException e) {
            return ResultTemplate.fail("文件处理失败: " + e.getMessage());
        }
    }
}
