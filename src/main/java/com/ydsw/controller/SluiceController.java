package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Sluice;
import com.ydsw.service.SluiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class SluiceController {
    @Autowired
    SluiceService sluiceService;

    @PostMapping("/api/sluice/selectAllByConditions")
    public ResultTemplate<Object> selectAllByConditions(@RequestBody JSONObject JSONObject)
    {
        Sluice sluice = JSONUtil.toBean(JSONObject,Sluice.class);
        List<Sluice> sluiceList=sluiceService.selectBySluice(sluice);
        return ResultTemplate.success(sluiceList);
    }
}
