package com.lyr.secKill.service.impl;

import com.lyr.secKill.service.ItemService;
import com.lyr.secKill.service.model.ItemModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by WIN10 on 2019/10/28.
 */
@Service
public class ItemServiceImpl implements ItemService{

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) {
        return null;
    }

    @Override
    public List<ItemModel> listItem() {
        return null;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        return null;
    }
}
