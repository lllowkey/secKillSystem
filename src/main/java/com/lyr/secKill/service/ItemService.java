package com.lyr.secKill.service;

import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.service.model.ItemModel;

import java.util.List;

/**
 * Created by WIN10 on 2019/10/28.
 */
public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //验证item以及promo缓存模型
    ItemModel getitemByIdInCache(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId, Integer amount)throws BusinessException;

    //库存回补
    boolean increaseStock(Integer itemId, Integer amount)throws BusinessException;

    //销量增加
    void increaseSales(Integer itemId,Integer amount)throws BusinessException;

    //异步更新库存
    boolean asyncDecreaseStock(Integer itemId,Integer amount);
}
