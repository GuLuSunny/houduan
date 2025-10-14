package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.*;
import com.ydsw.pojo.vo.BirdInfoVo;
import com.ydsw.service.FamilyService;
import com.ydsw.service.OrderService;
import com.ydsw.service.SpeciesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class SpeciesController {
    @Autowired
    SpeciesService speciesService;
    @Autowired
    OrderService orderService;
    @Autowired
    FamilyService familyService;

    /*
     * 级联查询数据库所有目，科，种
     * */
    @PreAuthorize("hasAnyAuthority('api_species_getBirdMKZ')")
    @PostMapping(value = "/api/species/getBirdMKZ")
    public ResultTemplate<Object> getBirdMKZ() {
        List<Species> speciesList = speciesService.list();
        List<Order> orderList = orderService.list();
        List<Family> familyList = familyService.list();

        List<BirdInfoVo> birdInfoVoListOrder = new ArrayList<>();
        for (Order order : orderList) {
            BirdInfoVo birdInfoVoOrder = new BirdInfoVo();
            birdInfoVoOrder.setId(String.valueOf(order.getId()));
            birdInfoVoOrder.setLabel(order.getName());
            birdInfoVoOrder.setLeafBool("0");
            birdInfoVoListOrder.add(birdInfoVoOrder);//目列表
            List<BirdInfoVo> birdInfoVoListFamily = new ArrayList<>();
            for (Family family : familyList) {
                if (order.getId().equals(family.getOrderId())) {//种属于目，加入
                    BirdInfoVo birdInfoVoFamily = new BirdInfoVo();
                    birdInfoVoFamily.setId(String.valueOf(order.getId()) + "-" + String.valueOf(family.getId()));
                    birdInfoVoFamily.setLabel(family.getName());
                    birdInfoVoFamily.setLeafBool("0");
                    birdInfoVoListFamily.add(birdInfoVoFamily);//科列表
                    List<BirdInfoVo> birdInfoVoListSpecies = new ArrayList<>();
                    for (Species species : speciesList) {
                        if (String.valueOf(family.getId()).equals(species.getFamilyId())) {//科属于种，加入
                            BirdInfoVo birdInfoVoSpecies = new BirdInfoVo();
                            birdInfoVoSpecies.setId(String.valueOf(order.getId()) + "-" + String.valueOf(family.getId()) + "-" + String.valueOf(species.getId()));
                            birdInfoVoSpecies.setLabel(species.getName() + " " + species.getNameLatin());
                            birdInfoVoSpecies.setNameLatin(species.getNameLatin());
                            birdInfoVoSpecies.setLeafBool("1");
                            birdInfoVoListSpecies.add(birdInfoVoSpecies);//目列表
                        }
                    }
                    birdInfoVoFamily.setChildren(birdInfoVoListSpecies);
                }
            }
            birdInfoVoOrder.setChildren(birdInfoVoListFamily);
        }
        List<BirdInfoVo> birdInfoVoListRoot = new ArrayList<>();
        BirdInfoVo root = new BirdInfoVo();
        root.setId("0");
        root.setLabel("ROOT");
        root.setLeafBool("0");
        root.setChildren(birdInfoVoListOrder);
        birdInfoVoListRoot.add(root);
        JSONArray jsonArray = JSONUtil.parseArray(birdInfoVoListRoot);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("datas", jsonArray);
        return ResultTemplate.success(jsonObject);
    }

    /**
     * 插入
     * //@param jsonArray
     * //@return
     */
    @PreAuthorize("hasAnyAuthority('api_species_addBirdData')")
    @PostMapping(value = "/api/species/addBirdData")
    @Transactional
    public ResultTemplate<Object> addBirdData(@RequestBody JSONObject jsonObject) {
        log.info(JSONUtil.toJsonStr(jsonObject));
        JSONArray jsonArrayROOTchildren = jsonObject.getJSONArray("birdInfo").getJSONObject(0).getJSONArray("children");
        for (int i = 0; i < jsonArrayROOTchildren.size(); i++) {
            JSONObject jsonObjectOrder = jsonArrayROOTchildren.getJSONObject(i);
            String orderId = jsonObjectOrder.get("id").toString().trim();
            String[] orderIdList = orderId.split("-");
            String orderId1 = orderIdList[0];
            Integer order_id = 0;
            if ("root".equals(orderId1)) {//是否是以root开头，新增的目
                Order o = new Order();
                o.setName(jsonObjectOrder.get("label").toString().trim());
                orderService.save(o);
                order_id = o.getId();
            } else {//非新增
                order_id = Integer.parseInt(orderId1);
            }
            JSONArray jsonArrayOrderchildren = jsonObjectOrder.getJSONArray("children");
            for (int j = 0; j < jsonArrayOrderchildren.size(); j++) {
                JSONObject jsonObjectFamily = jsonArrayOrderchildren.getJSONObject(j);
                String familyId = jsonObjectFamily.get("id").toString().trim();
                String[] familyIdList = familyId.split("-");
                String familyId1 = familyIdList[0];
                Integer family_id = 0;
                if ("root".equals(familyId1)) {//是否是以root开头，新增的科
                    Family f = new Family();
                    f.setName(jsonObjectFamily.get("label").toString().trim());
                    f.setOrderId(order_id);
                    familyService.save(f);
                    family_id = f.getId();
                } else {//非新增
                    family_id = Integer.parseInt(familyIdList[1]);
                }
                JSONArray jsonArrayFamilychildren = jsonObjectFamily.getJSONArray("children");
                for (int k = 0; k < jsonArrayFamilychildren.size(); k++) {
                    JSONObject jsonObjectSpecies = jsonArrayFamilychildren.getJSONObject(k);
                    String speciesId = jsonObjectSpecies.get("id").toString().trim();
                    String[] speciesList = speciesId.split("-");
                    String speciesId1 = speciesList[0];
                    if ("root".equals(speciesId1)) {//是否是以root开头，新增的种
                        Species s = new Species();
                        s.setName(jsonObjectSpecies.get("label").toString().trim());
                        s.setNameLatin(jsonObjectSpecies.get("nameLatin").toString().trim());
                        s.setFamilyId(String.valueOf(family_id));
                        speciesService.save(s);
                    }
                }
            }
        }
        return ResultTemplate.success();
    }
}