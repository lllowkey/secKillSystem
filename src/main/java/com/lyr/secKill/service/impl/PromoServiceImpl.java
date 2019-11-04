package com.lyr.secKill.service.impl;

import com.lyr.secKill.dao.PromoDOMapper;
import com.lyr.secKill.dataobject.PromoDO;
import com.lyr.secKill.service.PromoService;
import com.lyr.secKill.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by WIN10 on 2019/11/4.
 */
@Service
public class PromoServiceImpl implements PromoService {
@Autowired private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        //dataobject->model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if(promoModel == null){
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或正在进行
        DateTime now = new DateTime();
        if(promoModel.getStartDate().isAfter(now)){
            promoModel.setStatus(1);
        }else if (promoModel.getEndDate().isBefore(now)){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }
        return promoModel;
    }
    private PromoModel convertFromDataObject(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
