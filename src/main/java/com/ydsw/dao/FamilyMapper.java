package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydsw.domain.Family;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhang
* @description 针对表【family(科表)】的数据库操作Mapper
* @createDate 2024-11-21 16:49:08
* @Entity generator.domain.Family
*/
@Mapper
public interface FamilyMapper extends BaseMapper<Family> {

}




