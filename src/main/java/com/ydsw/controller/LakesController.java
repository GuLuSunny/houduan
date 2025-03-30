package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Lakes;
import com.ydsw.service.LakesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class LakesController {
    @Autowired
    private LakesService lakesService;

    @PostMapping(value = "/api/lakes/getAlllakesByConditions")
    public ResultTemplate<Object> getAlllakesByConditions(@RequestBody JSONObject jsonObject) {
        Lakes lakes = jsonObject.toBean(Lakes.class);
        List<Lakes> res=lakesService.selectLakesByConditions(lakes);
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
}
