package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.service.ModelListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ModelListController {
    @Autowired
    private ModelListService modelListService;

    @PostMapping(value = "/api/model/getClassName")
    public ResultTemplate<Object> getClassName() {
        List<String> res=modelListService.getAllClassName();
        return ResultTemplate.success(res);
    }

    @PostMapping(value = "/api/model/getModelByClassName")
    public ResultTemplate<Object> getModelByClassName(@RequestBody JSONObject jsonObject) {
        String className = jsonObject.getStr("className");
        if(className==null){
            ResultTemplate.fail("非法参数");
        }
        List<Map<String,Object>> res=modelListService.getAllModelByClassName(className);
        return ResultTemplate.success(res);
    }
}
