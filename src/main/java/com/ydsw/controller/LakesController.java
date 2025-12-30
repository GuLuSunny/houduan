package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.ydsw.domain.Lakes;
import com.ydsw.service.LakesService;
import com.ydsw.utils.ResultTemplate;
import com.ydsw.utils.ShpUploadHelper;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.Geometry;
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

@RestController
@Slf4j
public class LakesController {
    @Autowired
    private LakesService lakesService;

    @PostMapping(value = "/api/lakes/getAlllakesByConditions")
    public ResultTemplate<Object> getAlllakesByConditions(@RequestBody JSONObject jsonObject) {
        Lakes lakes = jsonObject.toBean(Lakes.class);
        List<Map<String,Object>> res = lakesService.selectLakesByConditions(lakes);
        return getRangeFromPGgeometryforMap(res);
    }

    public static ResultTemplate<Object> getRangeFromPGgeometryforMap(List<Map<String, Object>> res) {
        for (Map<String,Object> map : res) {
            Object object = map.get("geom");
            PGgeometry pggeometry = (PGgeometry) object;
            Geometry postgisGeom = pggeometry.getGeometry();
            String wkt = postgisGeom.toString();
            map.put("geom", wkt);
        }
        return ResultTemplate.success(res);
    }

    @PostMapping(value = "/api/lakes/getlakesPageByConditions")
    public ResultTemplate<Object> getlakesPageByConditions(@RequestBody JSONObject jsonObject) {
        Lakes lakes = jsonObject.toBean(Lakes.class);
        int currentPage = jsonObject.getInt("currentPage") == null ? 1 : jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize") == null ? 10 : jsonObject.getInt("pageSize");
        IPage<Map<String,Object>> res = lakesService.selectLakesPageByConditions(currentPage, pageSize, lakes);
        return ResultTemplate.success(res);
    }

    @PreAuthorize("hasAnyAuthority('api_lakes_uploadByShpfiles')")
    @PostMapping(value = "/api/lakes/uploadByShpfiles")
    public ResultTemplate<Object> uploadByShpfile(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        try {
            // 使用辅助工具类处理
            ResultTemplate<List<Lakes>> result = ShpUploadHelper.handleShpUpload(
                    fileGroup,
                    Lakes.class,
                    "geom",
                    lakes -> {
                        // 专属处理步骤
                        lakes.setGid(null);
                        lakes.setCity("开封市");
                        lakes.setCounty("中国");
                        lakes.setProvincial("河南省");
                    }
            );

            if (!result.isSuccess()) {
                return ResultTemplate.fail(result.getMessage());
            }

            List<Lakes> lakesList = result.getData();
            System.out.println(lakesList);
            //lakesService.saveBatch(lakesList);

            return ResultTemplate.success("提交成功！");

        } catch (IOException e) {
            return ResultTemplate.fail("文件处理失败: " + e.getMessage());
        }
    }
}