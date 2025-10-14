package com.ydsw.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ydsw.domain.Species;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhang
* @description 针对表【species(种表)】的数据库操作Mapper
* @createDate 2024-11-21 16:48:47
* @Entity generator.domain.Species
*/
@Mapper
public interface SpeciesMapper extends BaseMapper<Species> {

}




