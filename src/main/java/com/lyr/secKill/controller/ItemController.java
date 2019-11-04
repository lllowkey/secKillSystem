package com.lyr.secKill.controller;

import com.lyr.secKill.controller.viewobject.ItemVO;
import com.lyr.secKill.error.BusinessException;
import com.lyr.secKill.response.CommonReturnType;
import com.lyr.secKill.service.ItemService;
import com.lyr.secKill.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by WIN10 on 2019/10/29.
 */
@Controller("item")
@RequestMapping ("item")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;
    //创建商品
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException{
        //封装service请求来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);


        return CommonReturnType.create(itemVO);
    }

    /*商品详情页浏览*/
    @RequestMapping(value = "/getItem",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id){
        ItemModel itemModel = itemService.getItemById(id);

        ItemVO itemVO = convertVOFromModel(itemModel);

        return CommonReturnType.create(itemVO);

    }

    /*商品列表页*/
    @RequestMapping(value = "/getItemList",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType itemList(){
        List<ItemModel> itemModelList = itemService.listItem();

        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel ->{
            ItemVO itemVO = convertVOFromModel(itemModel);

            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    private ItemVO convertVOFromModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        if(itemModel.getPromoModel() != null){
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate());

        }else{
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}
