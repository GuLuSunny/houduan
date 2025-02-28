package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ydsw.domain.Purview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * @author zhang
 * @description 针对表【right(权限表)】的数据库操作Mapper
 * @createDate 2024-08-29 15:42:14
 * @Entity com.ydsw.domain.Right
 */
@Mapper
public interface PurviewMapper extends BaseMapper<Purview> {

    /**
     * 根据用户id获取权限列表
     * @param uid
     * @return
     */
    public List<String> selectRightENGByUserId(@Param("userId") Integer uid);

    IPage<Map<String, Object>> getPurviewListPage(IPage<?> page, @Param("purview") Purview purview);

    void deletePurviewDataById(@Param("idList")List<Integer> idArray);
}




