package com.ydsw.controller;

import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Farmland;
import com.ydsw.domain.Forest;
import com.ydsw.service.ForestService;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        if (fileGroup == null || fileGroup.length == 0) {
            return ResultTemplate.fail("请提交文件！");
        }
        String fileName = fileGroup[0].getOriginalFilename();
        for (int i = 1; i < fileGroup.length; i++) {
            String finename = fileGroup[i].getOriginalFilename();
            if(!Objects.equals(finename.substring(0,finename.indexOf('.')), fileName.substring(0,finename.indexOf('.'))))
            {
                return ResultTemplate.fail("文件格式错误！");
            }
        }
        try {
            List<Forest> forestList = ShpfileUtils.parseMultipleShpGroups(fileGroup, Forest.class);
            for (Forest  forest: forestList) {
                forest.getGeom().setSRID(4326);
                forest.setCreateTime(new Date());
                forest.setStatus(0);
            }
            forestService.saveBatch(forestList);
            System.out.println("forestList:"+forestList);
        } catch (IOException e) {
            return ResultTemplate.fail("文件："+fileGroup[0].getOriginalFilename()+"提交格式错误！");
        }

        return ResultTemplate.success("提交成功！");
    }
}
