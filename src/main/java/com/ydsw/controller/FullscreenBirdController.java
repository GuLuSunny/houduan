package com.ydsw.controller;

import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.FullscreenBird;
import com.ydsw.service.FullscreenBirdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/12/2  14:48
 * @Version 1.0
 */
@Slf4j
@RestController
public class FullscreenBirdController {
    @Autowired
    private FullscreenBirdService fullscreenBirdService;

    /**
     * 获取大屏水鸟科普信息
     *
     * @return
     */
        @PreAuthorize("hasAnyAuthority('api_fullscreenBird_getBirdInfo')")
    @PostMapping(value = "/api/fullscreenBird/getBirdInfo")
    public ResultTemplate<Object> getBirdInfo() {
        List<FullscreenBird> list = fullscreenBirdService.list();
        return ResultTemplate.success(list);
    }
}
