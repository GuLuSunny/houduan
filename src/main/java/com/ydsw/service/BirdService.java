package com.ydsw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.Bird;

import java.util.List;
import java.util.Map;

/**
* @author hhh
* @description 针对表【bird】的数据库操作Service
* @createDate 2024-11-11 12:48:45
*/
public interface BirdService extends IService<Bird> {

    List<Map<String,Object>> getAllBirds();

    Bird getBirdById(Integer id);

//    void deleteById(List<Integer> ids);

    IPage<Map<String,Object>> getBirdsByPage(int offset, int limit, List<Integer> speciesIds, Integer familyName, Integer orderName);
}