package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.Channel;
import com.ydsw.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ChannelController {
    @Autowired
    private ChannelService channelService;
    @PostMapping(value = "/api/Channel/selectAllByconditions")
    public ResultTemplate<Object> selectAllByconditions(@RequestBody JSONObject jsonObject) {
        Channel channel = JSONUtil.toBean(jsonObject, Channel.class);
        List<Channel> channelList=channelService.selectAllChannelByConditions(channel);
        return ResultTemplate.success(channelList);
    }
}
