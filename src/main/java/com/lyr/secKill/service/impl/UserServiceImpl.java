package com.lyr.secKill.service.impl;

import com.lyr.secKill.dao.UserDOMapper;
import com.lyr.secKill.dao.UserPasswordDOMapper;
import com.lyr.secKill.dataobject.UserDO;
import com.lyr.secKill.dataobject.UserPasswordDO;
import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.error.EmBusinessError;
import com.lyr.secKill.service.UserService;
import com.lyr.secKill.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(StringUtils.isEmpty(userModel.getName())
                ||userModel.getGender() == null
                ||userModel.getAge() == null
                ||StringUtils.isEmpty((userModel.getTelephone()))){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //实现model->dataobject方法
        UserDO userDO = convertFromModel(userModel);
        userDOMapper.insertSelective(userDO);
        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return ;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }

        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncryptPassword(userModel.getEncryptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
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
