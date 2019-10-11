package com.lyr.secKill.controller;

import com.lyr.secKill.controller.viewobject.UserVO;
import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.error.EmBusinessError;
import com.lyr.secKill.response.CommonReturnType;
import com.lyr.secKill.service.UserService;
import com.lyr.secKill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.RequestWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by WIN10 on 2019/10/10.
 */
@Controller("user")
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户获取otp短信接口
    @RequestMapping("/getotp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone")String telephone){
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //使用httpSession绑定用户手机号和otpCode
        httpServletRequest.getSession().setAttribute(telephone,otpCode);

        System.out.println("telephone = "+telephone+" & otpCode = "+otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id")Integer id)throws BusinessException{
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        if(userModel == null){

//            userModel.setEncryptPassword("123");
            throw new BusinessException((EmBusinessError.USER_NOT_EXIST));
        }

        UserVO userVO = convertFromModel(userModel);

        return CommonReturnType.create(userVO);
    }



    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }

}
