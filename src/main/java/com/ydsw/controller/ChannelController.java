package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.ydsw.domain.Channel;
import com.ydsw.service.ChannelService;
import com.ydsw.utils.ResultTemplate;
import com.ydsw.utils.ShpUploadHelper;
import com.ydsw.utils.ShpfileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.ydsw.controller.LakesController.getRangeFromPGgeometryforMap;

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

    @PreAuthorize("hasAnyAuthority('api_Channel_uploadByShpfiles')")
    @PostMapping(value = "/api/Channel/uploadByShpfile")
    public ResultTemplate<Object> uploadByShpfile(@RequestParam("shpfiles") MultipartFile[] fileGroup) {
        try {
            ResultTemplate<List<Channel>> result = ShpUploadHelper.handleShpUpload(
                    fileGroup,
                    Channel.class,
                    "geom",
                    null // 没有专属处理步骤
            );

            if (!result.isSuccess()) {
                return ResultTemplate.fail(result.getMessage());
            }

            List<Channel> channelList = result.getData();
            System.out.println(channelList);
            //channelService.saveBatch(channelList);

            return ResultTemplate.success("提交成功！");

        } catch (IOException e) {
            return ResultTemplate.fail("文件处理失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/api/DataManagement/deleteByIdListAndTypes")
    public ResultTemplate<Object> deleteByIdListAndTypes(@RequestBody JSONObject jsonObject) {
        List<Integer> idArray = jsonObject.getBeanList("ClassIdList", Integer.class);//id列表
        Channel channel= JSONUtil.toBean(jsonObject, Channel.class);
        Set<String> classNames=Set.of("lakes","channel","pumping_station","reservoir","sluice");
        if(jsonObject.get("classType") == null||!classNames.contains(jsonObject.get("classType").toString()))
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
