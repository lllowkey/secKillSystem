package com.lyr.secKill.service.impl;

import com.lyr.secKill.dao.UserDOMapper;
import com.lyr.secKill.dao.UserPasswordDOMapper;
import com.lyr.secKill.dataobject.UserDO;
import com.lyr.secKill.dataobject.UserPasswordDO;
import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.error.EmBusinessError;
import com.lyr.secKill.service.UserService;
import com.lyr.secKill.service.model.UserModel;
import com.lyr.secKill.validator.ValidationResult;
import com.lyr.secKill.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * Created by WIN10 on 2019/10/10.
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired private UserDOMapper userDOMapper;

    @Autowired private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private ValidatorImpl  validator;

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
    public UserModel getUserByIdInCache(Integer id) {
        UserModel usermodel = (UserModel)redisTemplate.opsForValue().get("user_validate_" + id);
        if(usermodel == null) {
            usermodel = this.getUserById(id);
            redisTemplate.opsForValue().set("user_validate_" + id, usermodel);
            redisTemplate.expire("user_validate_" + id, 10, TimeUnit.MINUTES);
        }
        return usermodel;
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//                ||userModel.getGender() == null
//                ||userModel.getAge() == null
//                ||StringUtils.isEmpty((userModel.getTelephone()))){
//            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }


        //实现model->dataobject方法
        UserDO userDO = convertFromModel(userModel);
        try {
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException ex){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号重复注册");
        }
        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return ;
    }

    @Override
    public UserModel validateLogin(String telephone, String encryptPassword) throws BusinessException{
        //通过用户手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelephone(telephone);
        if(userDO == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO,userPasswordDO);

        //比对用户信息内的密码是否和传输进来的相匹配
        if (!StringUtils.equals(encryptPassword,userModel.getEncryptPassword())){
            throw new BusinessException((EmBusinessError.USER_LOGIN_FAIL));
        }
        return userModel;
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
