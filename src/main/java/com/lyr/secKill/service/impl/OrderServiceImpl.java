package com.lyr.secKill.service.impl;

import com.lyr.secKill.dao.OrderDOMapper;
import com.lyr.secKill.dao.SequenceDOMapper;
import com.lyr.secKill.dataobject.OrderDO;
import com.lyr.secKill.dataobject.SequenceDO;
import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.error.EmBusinessError;
import com.lyr.secKill.service.ItemService;
import com.lyr.secKill.service.OrderService;
import com.lyr.secKill.service.UserService;
import com.lyr.secKill.service.model.ItemModel;
import com.lyr.secKill.service.model.OrderModel;
import com.lyr.secKill.service.model.UserModel;
import org.omg.CORBA.ORB;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.Sequence;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderDOMapper orderDOMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException {
        //校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
//        ItemModel itemModel = itemService.getItemById(itemId);
        ItemModel itemModel = itemService.getitemByIdInCache(itemId);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }
//        UserModel userModel = userService.getUserById(userId);
        UserModel userModel = userService.getUserByIdInCache(userId);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST,"用户不存在");
        }
        if(amount <= 0 || amount >99){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }

        //校验活动信息
        if(promoId != null){
            //校验对应活动是否存在相应的适用商品
            if(promoId.intValue() != itemModel.getPromoModel().getId()){
                throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
                //校验活动是否正在进行中
            }else if(itemModel.getPromoModel().getStatus().intValue()!= 2){
                throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }

        //落单减库存，支付减库存
        boolean result = itemService.decreaseStock(itemId,amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if (promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            orderModel.setItemPrice(itemModel.getPrice());

        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        //生成交易流水号
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = converFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //加上商品的销量
        itemService.increaseSales(itemId,amount);


        //返回前端
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generateOrderNo(){
        //订单号16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        //中间6位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSquenceByName("order_info");

        sequence = sequenceDO.getCurrentValue();
        //更新下一次的currentValue
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKey(sequenceDO);
        String sequnceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequnceStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequnceStr);

        //最后2位为分库分表位,暂时写死
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

    private OrderDO converFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
