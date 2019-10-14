package com.lyr.secKill.controller;

import com.alibaba.druid.util.StringUtils;
import com.lyr.secKill.controller.viewobject.UserVO;
import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.error.EmBusinessError;
import com.lyr.secKill.response.CommonReturnType;
import com.lyr.secKill.service.UserService;
import com.lyr.secKill.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.GenericFilter;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.RequestWrapper;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by WIN10 on 2019/10/10.
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam("telephone")String telephone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
    //验证手机号和otpcode相符合
    String inSesssionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telephone);
    if (!com.alibaba.druid.util.StringUtils.equals(otpCode,inSesssionOtpCode)){
        throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符");
    }

    //用户的注册流程
    UserModel userModel = new UserModel();
    userModel.setName(name);
    userModel.setGender((new Byte(String.valueOf(gender.intValue()))));
    userModel.setAge(age);
    userModel.setTelephone(telephone);
    userModel.setRegisterMode("byphone");
    userModel.setEncryptPassword(this.EncodeByMd5(password));

    userService.register(userModel);
        return CommonReturnType.create(null);
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    //用户获取otp短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
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
