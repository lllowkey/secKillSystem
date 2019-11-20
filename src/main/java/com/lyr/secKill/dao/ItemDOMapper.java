package com.lyr.secKill.dao;

import com.lyr.secKill.dataobject.ItemDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Mon Oct 28 17:35:02 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    List<ItemDO> listItem();
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Mon Oct 28 17:35:02 CST 2019
     */
    int insert(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Mon Oct 28 17:35:02 CST 2019
     */
    int insertSelective(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Mon Oct 28 17:35:02 CST 2019
     */
    ItemDO selectByPrimaryKey(Integer id);



    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Mon Oct 28 17:35:02 CST 2019
     */
    int updateByPrimaryKeySelective(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Mon Oct 28 17:35:02 CST 2019
     */
    int updateByPrimaryKey(ItemDO record);

    int increaseSale(@Param("id")Integer id,@Param("amount")Integer amount);
}