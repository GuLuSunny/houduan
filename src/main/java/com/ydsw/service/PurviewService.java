package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.Purview;

import java.util.List;
import java.util.Map;


/**
 * @author zhang
 * @description 针对表【right(权限表)】的数据库操作Service
 * @createDate 2024-08-29 15:42:14
 */
public interface PurviewService extends IService<Purview> {
     List<String> selectRightENGByUserId(Integer userId);

    IPage<Map<String, Object>> getPurviewListPage(int currentPage, int pageSize, Purview purview);

    void deletePurviewDataById(List<Integer> idArray);
}
