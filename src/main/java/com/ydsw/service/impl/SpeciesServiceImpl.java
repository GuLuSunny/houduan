package com.ydsw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydsw.dao.SpeciesMapper;
import com.ydsw.domain.Species;
import com.ydsw.service.SpeciesService;
import org.springframework.stereotype.Service;

/**
* @author zhang
* @description 针对表【species(种表)】的数据库操作Service实现
* @createDate 2024-11-21 16:48:47
*/
@Service
public class SpeciesServiceImpl extends ServiceImpl<SpeciesMapper, Species>
    implements SpeciesService{

}




