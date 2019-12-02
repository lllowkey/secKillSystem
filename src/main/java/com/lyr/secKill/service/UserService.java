package com.lyr.secKill.service;

import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.service.model.UserModel;

/**
 * Created by WIN10 on 2019/10/10.
 */
public interface UserService{
    //通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);

    //通过缓存获取用户对象
    UserModel getUserByIdInCache(Integer id);

    void register(UserModel userModel) throws BusinessException;


    /*telephone:用户注册手机
    password:用户加密后的密码*/
    UserModel validateLogin(String telephone,String encryptPassword) throws BusinessException;
}
