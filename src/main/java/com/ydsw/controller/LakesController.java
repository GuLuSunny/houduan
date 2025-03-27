package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Lakes;
import com.ydsw.service.LakesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
