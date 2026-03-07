package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ModelStatus;
import com.ydsw.service.ModelStatusService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 遥感产品生产记录/状态 控制类
 */
@Slf4j
@RestController
public class ModelStatusController {
    @Autowired
    private ModelStatusService modelStatusService;

    //获取目标程序状态
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

    // 分页查询遥感产品生产记录/状态
    @PostMapping(value = "/api/model/getModelStatusPages")
    public ResultTemplate<Object> getModelStatusPageByConditionsFuc(@RequestBody JSONObject jsonObject) {
        // 当前页码，默认值1
        int pageNum = jsonObject.get("pageNum") == null ? 1 : jsonObject.getInt("pageNum");
        // 每页展示的数据条数，默认值10
        int pageSize = jsonObject.get("pageSize") == null ? 10 : jsonObject.getInt("pageSize");
        // JSON转实体对象，包含查询语句判断条件
        ModelStatus modelStatus = jsonObject.toBean(ModelStatus.class);
        List<Map<String,Object>> result;
        try {
            result = modelStatusService.getModelStatusPageByConditions(pageNum, pageSize,modelStatus);
        } catch (Exception e) {
            return ResultTemplate.fail(e.getMessage());
        }
        return ResultTemplate.success(result);
    }

    /**
     * 根据id数组或者其他判断条件进行删除
     * @param jsonObject
     * @return
     */
    @PostMapping(value = "/api/model/deleteModelByIdsOrCondition")
    public ResultTemplate<Object> deleteModelByIdsOrCondition(@RequestBody JSONObject jsonObject) {
        try{
            String deleteType = jsonObject.getStr("deleteType");
            boolean result;
            // 获取当前系统时间为修改时间
            Date updateTime = new Date(System.currentTimeMillis());

            if ("ids".equals(deleteType)) {
                // 按ID列表批量删除
                List<String> idArray = jsonObject.getBeanList("ids", String.class);
                if (idArray == null || idArray.isEmpty()) {
                    return ResultTemplate.fail("批量删除失败：ids 不能为空");
                }
                List<Integer> idList = new ArrayList<>();
                ModelFileStatusController.ArrayStrToInt(idArray, idList); // 复用已有字符串转整数方法
                result = modelStatusService.deleteModelStatusByIdList(idList,updateTime);
            } else if ("condition".equals(deleteType)) {
                // 按条件批量删除
                ModelStatus condition = jsonObject.getBean("condition", ModelStatus.class);
                if (condition == null) {
                    return ResultTemplate.fail("按条件删除失败：condition 不能为空");
                }
                condition.setUpdateTime(updateTime);
                result = modelStatusService.deleteModelStatusByCondition(condition);
            } else {
                return ResultTemplate.fail("删除失败：deleteType 仅支持 ids/condition");
            }
            return result ? ResultTemplate.success("批量删除成功") : ResultTemplate.fail("批量删除失败：未找到记录");
        }catch (Exception e){
            log.error("批量删除异常", e);
            return ResultTemplate.fail("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 根据id或者其他判断条件进行修改
     * @param jsonObject
     * @return
     */
    @PostMapping(value = "/api/model/updateModelByIdsOrCondition")
    public ResultTemplate<Object> updateModelByIdsOrCondition(@RequestBody JSONObject jsonObject) {
        try {
            String updateType = jsonObject.getStr("updateType");
            ModelStatus updateEntity = jsonObject.getBean("updateEntity", ModelStatus.class);

            if (updateEntity == null) {
                return ResultTemplate.fail("更新失败：updateEntity 不能为空");
            }
            // 获取当前系统时间为修改时间
            Date updateTime = new Date(System.currentTimeMillis());
            updateEntity.setUpdateTime(updateTime);

            boolean result;
            if ("id".equals(updateType)) {
                Integer id = jsonObject.getInt("id");
                if (id == null) {
                    return ResultTemplate.fail("按ID更新失败：id 不能为空");
                }
                updateEntity.setId(id);
                result = modelStatusService.updateModelStatusById(updateEntity);
            } else if ("condition".equals(updateType)) {
                ModelStatus condition = jsonObject.getBean("condition", ModelStatus.class);
                if (condition == null) {
                    return ResultTemplate.fail("按条件更新失败：condition 不能为空");
                }
                result = modelStatusService.updateModelStatusByCondition(updateEntity, condition);
            } else {
                return ResultTemplate.fail("更新失败：updateType 仅支持 id/condition");
            }

            return result ? ResultTemplate.success("更新成功") : ResultTemplate.fail("更新失败：未找到记录");
        } catch (Exception e) {
            log.error("更新异常", e);
            return ResultTemplate.fail("更新失败：" + e.getMessage());
        }
    }
}
