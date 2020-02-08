package com.usth.miaosha.dao;

import com.usth.miaosha.domain.MiaoshaOrder;
import com.usth.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id,goods_id,goods_name,goods_count,goods_price,order_channel,order_status,create_date,delivery_addr_id) " +
            "values(#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate},#{deliveryAddrId})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = long.class)
    long insertOrder(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id,goods_id,order_id) values(#{userId},#{goodsId},#{orderId})")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);
}
