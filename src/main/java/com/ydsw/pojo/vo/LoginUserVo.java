package com.ydsw.pojo.vo;

import com.ydsw.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/7/16  10:05
 * @Version 1.0
 */
public class LoginUserVo implements UserDetails {

    private List<String> list;//传入的权限列表参数
    private List<SimpleGrantedAuthority> listSimpleGrantedAuthority;//自定义一个权限列表集合
    private User user;

    public LoginUserVo(User user, List<String> list) {
        this.user = user;
        this.list = list;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoginUserVo(User user) {
        this.user = user;
    }

    public LoginUserVo() {
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    //用户权限列表
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (listSimpleGrantedAuthority != null) {
            return listSimpleGrantedAuthority;
        }
        listSimpleGrantedAuthority = new ArrayList<>();
        list.forEach((e) -> {
            listSimpleGrantedAuthority.add(new SimpleGrantedAuthority(e));
        });
        return listSimpleGrantedAuthority;
    }

    //用户密码
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    //用户名
    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    //用户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //用户是否未锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //用户是否超时
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //用户是否启用
    @Override
    public boolean isEnabled() {
        return true;
    }
}
