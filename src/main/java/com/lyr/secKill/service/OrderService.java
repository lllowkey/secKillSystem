package com.lyr.secKill.service;

import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.service.model.OrderModel;

public interface OrderService {
    //使用1.通过前端url传过来的秒杀活动id,然后下单接口内校验对应id是否属于对应商品且活动是否开始(可以记录多个途径的秒杀来源,性能较好
    //2.直接在下单接口内判断对应的商品是否存在秒杀活动,若存在进行中的则以秒杀价格下单(平销的商品也需要判断,性能下降
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount,String stockLogId) throws BusinessException;
}
