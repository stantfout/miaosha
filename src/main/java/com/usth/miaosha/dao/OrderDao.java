package com.usth.miaosha.dao;

import com.usth.miaosha.domain.MiaoshaOrder;
import com.usth.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id,goods_id,goods_name,goods_count,goods_price,order_channel,order_status,create_date,delivery_addr_id) " +
            "values(#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{orderStatus},#{createDate},#{deliveryAddrId})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = long.class)
    long insertOrder(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id,goods_id,order_id) values(#{userId},#{goodsId},#{orderId})")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);

    @Delete("delete from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    void deleteMiaoshaOrder(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Update("update order_info set order_status = 1 where user_id = #{userId} and id = #{orderId}")
    void deleteOrder(@Param("userId") long userId, @Param("orderId") long orderId);
}
