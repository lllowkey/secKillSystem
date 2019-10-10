package com.lyr.secKill.service.impl;

import com.lyr.secKill.dao.UserDOMapper;
import com.lyr.secKill.dao.UserPasswordDOMapper;
import com.lyr.secKill.dataobject.UserDO;
import com.lyr.secKill.dataobject.UserPasswordDO;
import com.lyr.secKill.service.UserService;
import com.lyr.secKill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by WIN10 on 2019/10/10.
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired private UserDOMapper userDOMapper;

    @Autowired private UserPasswordDOMapper userPasswordDOMapper;

    @Override
    public UserModel getUserById(Integer id) {

        UserDO userDo = userDOMapper.selectByPrimaryKey(id);

        if (userDo == null){
            return null;
        }
        //通过用户id获取加密密码
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDo.getId());

        return convertFromDataObject(userDo,userPasswordDO);
    }

    private  UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO != null){
            userModel.setEncryptPassword(userPasswordDO.getEncryptPassword());
        }

        return userModel;
    }
}
