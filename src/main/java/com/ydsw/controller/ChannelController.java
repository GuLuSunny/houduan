package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Channel;
import com.ydsw.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
}
