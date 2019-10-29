package com.lyr.secKill.service;

import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId,Integer itemId,Integer amount) throws BusinessException;
}
