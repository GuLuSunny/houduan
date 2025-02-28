package com.ydsw.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydsw.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author zhang
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-08-27 10:44:55
*/
public interface UserService extends IService<User> {

    List<Map<String,Object>> selectUserByCondition(User userClass);

    Boolean isExistsByUsername(String username);

    IPage<Map<String,Object>> getUserListByCondition(int currentPage, int pageSize, User userClass);

    void deleteUserList(List<String> userIds,User userClass);

    void updateUserInfo(User userClass);
}
