package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Channel;
import com.ydsw.service.ChannelService;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.ydsw.controller.LakesController.getRangeFromPGgeometryforMap;
import static com.ydsw.utils.ShpfileUtils.parseShpFile;

@Slf4j
@RestController
public class ChannelController {
    @Autowired
    private ChannelService channelService;
    @PostMapping(value = "/api/Channel/selectAllByconditions")
    public ResultTemplate<Object> selectAllByconditions(@RequestBody JSONObject jsonObject) {
        Channel channel = JSONUtil.toBean(jsonObject, Channel.class);
        List<Map<String,Object>> channelList=channelService.selectAllChannelByConditions(channel);
        return getRangeFromPGgeometryforMap(channelList);
    }
    @PostMapping(value = "/api/Channel/uploadByShpfile")
    public ResultTemplate<Object> uploadByShpfile(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        if(fileGroup==null||fileGroup.length==0)
        {
            return ResultTemplate.fail("请提交文件！");
        }
        try {

            List<Channel> channelList= ShpfileUtils.parseMultipleShpGroups(fileGroup,Channel.class);
            for (Channel channel : channelList) {
                channel.getGeom().setSRID(4326);
                channel.setCreateTime(new Date());
                channel.setStatus(0);
            }
            //System.out.println(channelList);
            channelService.saveBatch(channelList);
        } catch (IOException e) {
            return ResultTemplate.fail("文件："+fileGroup[0].getOriginalFilename()+"提交格式错误！");
        }

        return ResultTemplate.success("提交成功！");
    }

    @PostMapping(value = "/api/DataManagement/deleteByIdListAndTypes")
    public ResultTemplate<Object> deleteByIdListAndTypes(@RequestBody JSONObject jsonObject) {
        List<Integer> idArray = jsonObject.getBeanList("ClassIdList", Integer.class);//id列表
        Channel channel= JSONUtil.toBean(jsonObject, Channel.class);
        if(jsonObject.get("classType") == null)
        {
            return ResultTemplate.fail("非法的数据类型！");
        }
        String classType= jsonObject.get("classType").toString();
        String idType=jsonObject.get("idType").toString();
        if(classType.isEmpty() || (!Objects.equals(idType, "gid") && !Objects.equals(idType, "id")
                &&!idType.equals("objectid")) )
        {
            return ResultTemplate.fail("参数错误！");
        }
        if(idArray==null|| idArray.isEmpty())
        {
            try {
                channelService.updataTablesByTypes(classType,idType,new ArrayList<>(),channel);
            }catch (Exception e) {
                ResultTemplate.fail("删除失败!");
            }
        }else {
            try {
                channelService.updataTablesByTypes(classType, idType, idArray, channel);
            } catch (Exception e) {
                ResultTemplate.fail("删除失败!");
            }
        }
        return ResultTemplate.success("删除成功！");
    }
}
