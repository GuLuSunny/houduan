package com.ydsw.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.River0Arc;
import com.ydsw.service.River0ArcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class River0ArcController {
    @Autowired
    private River0ArcService river0ArcService;
    @PreAuthorize("hasAnyAuthority('api_getAllRival')")
    @PostMapping(value = "/api/getAllRival")
    public ResultTemplate<Object> getAllRivel(@RequestBody JSONObject jsonObject) {
        /*
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        if (currentPage < 1) {
            return ResultTemplate.fail("无效页码！");
        } else if (pageSize < 1) {
            return ResultTemplate.fail("无效单位！");
        }
        */
        River0Arc river0Arc = jsonObject.toBean(River0Arc.class);
        List page =river0ArcService.selectPagesByRiverClass(river0Arc);
        return ResultTemplate.success(page);
    }
}
