package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Lakes;
import com.ydsw.service.LakesService;
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
        List<Map<String,Object>> res=lakesService.selectLakesByConditions(lakes);
        return getRangeFromPGgeometryforMap(res);
    }

    public static ResultTemplate<Object> getRangeFromPGgeometryforMap(List<Map<String, Object>> res) {
        for (Map<String,Object> map : res) {
            Object object=map.get("geom");
            PGgeometry pggeometry = (PGgeometry) object;
            Geometry postgisGeom = pggeometry.getGeometry();
            // 转换为 WKT 字符串，再通过 JTS 解析
            String wkt = postgisGeom.toString();
            map.put("geom",wkt);
        }
        return ResultTemplate.success(res);
    }

    @PostMapping(value = "/api/lakes/getlakesPageByConditions")
    public ResultTemplate<Object> getlakesPageByConditions(@RequestBody JSONObject jsonObject) {
        Lakes lakes = jsonObject.toBean(Lakes.class);
        int currentPage = jsonObject.getInt("currentPage")==null?1:jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize")==null?10:jsonObject.getInt("pageSize");
        IPage<Map<String,Object>> res= lakesService.selectLakesPageByConditions(currentPage,pageSize,lakes);
                return ResultTemplate.success(res);
    }
    @PreAuthorize("hasAnyAuthority('api_lakes_uploadByShpfiles')")
    @PostMapping(value = "/api/lakes/uploadByShpfiles")
    public ResultTemplate<Object> uploadByShpfile(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        if(fileGroup==null||fileGroup.length==0)
        {
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

            List<Lakes> lakesList= ShpfileUtils.parseMultipleShpGroups(fileGroup,Lakes.class);
            for (Lakes lakes:lakesList) {
                lakes.setGid(null);
                lakes.setCity("开封市");
                lakes.setCounty("中国");
                lakes.setProvincial("河南省");
                lakes.setStatus(0);
                lakes.setCreateTime(new Date());
                lakes.getGeom().setSRID(4326);
            }
            System.out.println(lakesList);
            //lakesService.saveBatch(lakesList);
        } catch (IOException e) {
            return ResultTemplate.fail("文件："+fileGroup[0].getOriginalFilename()+"提交格式错误！");
        }

        return ResultTemplate.success("提交成功！");
    }
}
