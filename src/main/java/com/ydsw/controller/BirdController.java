package com.ydsw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.service.BirdService;
import com.ydsw.service.ObservationBirdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description 用于处理鸟类相关请求的控制器
 */
@Slf4j
@RestController
public class BirdController {

    @Autowired
    private BirdService birdService;

    @Autowired
    private ObservationBirdService observationBirdService;
    /*
    * 获取所有鸟类科普信息？
    * */
    @PreAuthorize("hasAnyAuthority('api_birds_getAllBirds')")
    @PostMapping(value = "/api/birds/getAllBirds")
    public ResultTemplate<Object> getAllBirds() {
        List<Map<String,Object>> maps=birdService.getAllBirds();
        for (Map<String,Object> map : maps) {
            if(groupIdToname(map))
            {
                return ResultTemplate.fail("视图错误！");
            }
        }
        return ResultTemplate.success(maps);
    }


    /*
    * 鸟类科普信息分页查询
    * */
    @PreAuthorize("hasAnyAuthority('api_birds_pageQuery')")
    @PostMapping("/api/birds/pageQuery")
    public ResultTemplate<Object> getBirdsByPage(@RequestBody Map<String, Object> requestBody) {
        int currentPage = requestBody.get("currentPage") == null ? 1 :(Integer)requestBody.get("currentPage");
        int pageSize = requestBody.get("pageSize") == null ? 10 : (Integer)requestBody.get("pageSize");
        String speciesName= null;
        if (requestBody.get("speciesName")!=null) {
            speciesName = requestBody.get("speciesName").toString();
        }
        List<Integer> speciesId= new ArrayList<>();
        if (speciesName!=null) {
            if (!speciesName.isEmpty()) {
                List<Map<String,Object>> data=observationBirdService.selectIdBySpecies(speciesName,null,null);
                if(data!=null&& !data.isEmpty())
                {
                    for (Map<String,Object> map : data) {
                        speciesId.add((Integer) map.get("sid"));
                    }

                }else{
                    IPage<Map<String,Object>> res =birdService.getBirdsByPage(currentPage, pageSize,speciesId,9999,9999);
                    return  ResultTemplate.success(res);
                }
            }
        }

        Integer familyId= (Integer)requestBody.get("familyId");
        Integer orderId=(Integer) requestBody.get("orderId");
        IPage<Map<String,Object>> res=birdService.getBirdsByPage(currentPage, pageSize,speciesId,familyId,orderId);
        for (Map<String,Object> map : res.getRecords()) {
            if (groupIdToname(map))
            {
                return ResultTemplate.success(map);
            }
        }
        return ResultTemplate.success(res);
    }
    /*
    * 链表查询，由外键查询对应的目科种名称
    * */
    private boolean groupIdToname(Map<String, Object> map) {
        Integer sid = (Integer) map.get("speciesId");
        Integer fid = (Integer) map.get("familyId");
        Integer oid = (Integer) map.get("orderId");
        List<Map<String, Object>> res = observationBirdService.selectNameById(sid, fid, oid);
        if (res.isEmpty()) {
            return true;
        }
        map.put("speciesName", res.get(0).get("sname"));
        map.put("familyName", res.get(0).get("fname"));
        map.put("orderName", res.get(0).get("oname"));
        if(sid==null)
        {
            map.remove("speciesName");
        }
        if (fid==null)
        {
            map.remove("familyName");
        }
        if (oid==null)
        {
            map.remove("orderName");
        }
        return false;
    }

//    @RequestMapping("/api/birds/deleteByidsorConditions")
//    public ResultTemplate<Object> updateBird(@RequestBody JSONObject jsonObject) {
//        List<Integer> idArray = jsonObject.getBeanList("ids", Integer.class);//id列表
//        try {
//            birdService.deleteById(idArray);
//        } catch (Exception e) {
//            return ResultTemplate.fail(e.getMessage());
//        }
//        return ResultTemplate.success();
//    }

}