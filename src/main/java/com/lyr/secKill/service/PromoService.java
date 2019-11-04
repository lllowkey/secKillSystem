package com.lyr.secKill.service;

import com.lyr.secKill.service.model.PromoModel;

/**
 * Created by WIN10 on 2019/11/4.
 */
public interface PromoService {

    //根据itemID获取即将进行或正在进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);


}
