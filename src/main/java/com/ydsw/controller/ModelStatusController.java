package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ModelStatus;
import com.ydsw.service.ModelStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ModelStatusController {
    @Autowired
    private ModelStatusService modelStatusService;

    @PostMapping(value = "/api/model/getModelStatusByConditions")
    public ResultTemplate<Object> getModelStatus(@RequestBody JSONObject jsonObject) {
        ModelStatus modelStatus = jsonObject.toBean(ModelStatus.class);
        List<Map<String,Object>> res=modelStatusService.selectModelStatusByConditions(modelStatus);
        return ResultTemplate.success(res);
    }

    @PreAuthorize("hasAnyAuthority('api_model_updateModelStatus')")
    @PostMapping(value = "/api/model/updateModelStatus")
    public ResultTemplate<Object> updateModelStatus(@RequestBody JSONObject jsonObject) {
        ModelStatus modelStatus = jsonObject.toBean(ModelStatus.class);
        try {
            modelStatusService.updateModelStatus(modelStatus);
        }catch (Exception e){
            return ResultTemplate.fail("未知错误!");
        }
        return ResultTemplate.success("状态已更新！");
    }

    @PostMapping(value = "/api/model/dropModelLogs")
    public ResultTemplate<Object> dropModelLogs(@RequestBody JSONObject jsonObject) {
        ModelStatus modelStatus = jsonObject.toBean(ModelStatus.class);
        List<String> idArray = jsonObject.getBeanList("ids", String.class);
        if (idArray != null && idArray.isEmpty())
        {
            try {
                modelStatusService.dropModelLogs(new ArrayList<>(), modelStatus);
            }catch (Exception e){
                ResultTemplate.fail("未知错误");
            }
        }
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        try {
            modelStatusService.dropModelLogs(idList, modelStatus);
        }catch (Exception e){
            ResultTemplate.fail("未知错误");
        }
        return ResultTemplate.success("已遗弃记录");
    }
}
