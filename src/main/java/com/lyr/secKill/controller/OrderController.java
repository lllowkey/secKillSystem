package com.lyr.secKill.controller;

import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.error.EmBusinessError;
import com.lyr.secKill.mq.MqProducer;
import com.lyr.secKill.response.CommonReturnType;
import com.lyr.secKill.service.ItemService;
import com.lyr.secKill.service.OrderService;
import com.lyr.secKill.service.model.OrderModel;
import com.lyr.secKill.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("order")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    ItemService itemService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MqProducer mqProducer;

    //封装下单请求
    @RequestMapping(value = "/createOrder",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name ="itemId")Integer itemId,
                                        @RequestParam("amount")Integer amount,
                                        @RequestParam(value = "promoId",required = false)Integer promoId) throws BusinessException {
//        Boolean isLogin = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录无法下单");

        }
        //获取用户的登录信息
        UserModel userModel = (UserModel)redisTemplate.opsForValue().get(token);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录无法下单");
        }
        //获取用户信息
//        UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");

//        OrderModel orderModel = orderService.createOrder(userModel.getId(),itemId,promoId ,amount);

        //加入库存流水init状态
        String stockLogId = itemService.initStockLog(itemId, amount);

        //完成下单事务性消息
        if(!mqProducer.transactionAsyncReduceStock(userModel.getId(),itemId,promoId,amount,stockLogId)){
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"下单失败");
        }
        return CommonReturnType.create(null);
    }

}
