package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Purview;
import com.ydsw.service.PurviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/10/29  16:20
 * @Version 1.0
 */
@Slf4j
@Controller
@RequestMapping(value = "/api/purview")
public class PurviewController {
    @Autowired
    private PurviewService purviewService;

    /**
     * 查询权限列表
     *
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_purview_getPurviewList')")
    @PostMapping(value = "/getPurviewList")
    @ResponseBody
    public ResultTemplate<Object> getPurviewList() {
        // 使用 QueryWrapper
        QueryWrapper<Purview> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("rightinfo"); // 根据 propertyName 列升序排序
        List<Purview> purviewList = purviewService.list(queryWrapper);
        return ResultTemplate.success(purviewList);
    }

    /**
     * 根据条件查询权限分页列表
     *
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_purview_getPurviewListPage')")
    @PostMapping(value = "/getPurviewListPage")
    @ResponseBody
    public ResultTemplate<Object> getPurviewListPage(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        Purview purview = JSONUtil.toBean(jsonObject, Purview.class);
        IPage<Map<String, Object>> purviewListPage = purviewService.getPurviewListPage(currentPage, pageSize, purview);
        return ResultTemplate.success(purviewListPage);
    }

    /**
     * 根据权限id进行更新信息
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_purview_updatePurviewInfo')")
    @PostMapping(value = "/updatePurviewInfo")
    @ResponseBody
    @Transactional//事务
    public ResultTemplate<Object> updatePurviewInfo(@RequestBody JSONObject jsonObject) {
        Purview purview = JSONUtil.toBean(jsonObject, Purview.class);
        purviewService.updateById(purview);//更新权限信息
        return ResultTemplate.success();
    }

    /*
     * 根据权限id列表删除
     * */
    @PreAuthorize("hasAnyAuthority('api_purview_deletePurviewDataById')")
    @RequestMapping(value = "/deletePurviewDataById")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> deletePurviewDataById(@RequestBody JSONObject jsonObject) {
        List<Integer> idArray = jsonObject.getBeanList("ids", Integer.class);//id列表
        purviewService.deletePurviewDataById(idArray);
        return ResultTemplate.success("删除成功！");
    }

    /**
     * 新增权限信息
     *
     * @param jsonObject
     * @return
     */
    @PreAuthorize("hasAnyAuthority('api_purview_addPurviewInfo')")
    @PostMapping(value = "/addPurviewInfo")
    @ResponseBody
    @Transactional
    public ResultTemplate<Object> addPurviewInfo(@RequestBody JSONObject jsonObject) {
        Purview purview = JSONUtil.toBean(jsonObject, Purview.class);
        //权限名称校验，唯一性
        Map map1=new HashMap();
        map1.put("rightinfo",purview.getRightinfo());
        List<Purview> purviewList1=purviewService.listByMap(map1);
        if(purviewList1!=null&&purviewList1.size()>0){
            return ResultTemplate.fail("权限名称已存在，请重新输入！");
        }
        //权限标识符校验，唯一性
        Map map2=new HashMap();
        map2.put("right_eng",purview.getRightEng());
        List<Purview> purviewList2=purviewService.listByMap(map2);
        if(purviewList2!=null&&purviewList2.size()>0){
            return ResultTemplate.fail("权限标识符已存在，请重新输入！");
        }
        purviewService.save(purview);//保存权限信息
        return ResultTemplate.success();
    }
}
