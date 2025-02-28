package com.ydsw.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.PurviewMapper;
import com.ydsw.domain.Purview;
import com.ydsw.service.PurviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhang
 * @description 针对表【right(权限表)】的数据库操作Service实现
 * @createDate 2024-08-29 15:42:14
 */
@Service
public class PurviewServiceImpl extends ServiceImpl<PurviewMapper, Purview>
        implements PurviewService {

    @Autowired
    private PurviewMapper purviewMapper;

    @Override
    public List<String> selectRightENGByUserId(Integer userId) {
        return purviewMapper.selectRightENGByUserId(userId);
    }

    @Override
    public IPage<Map<String, Object>> getPurviewListPage(int currentPage, int pageSize, Purview purview) {
        IPage<Purview> page = new Page<>(currentPage, pageSize);
        return purviewMapper.getPurviewListPage(page, purview);
    }

    @Override
    public void deletePurviewDataById(List<Integer> idArray) {
        purviewMapper.deletePurviewDataById(idArray);
    }
}




