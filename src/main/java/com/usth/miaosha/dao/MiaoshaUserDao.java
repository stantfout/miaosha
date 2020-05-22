package com.usth.miaosha.dao;

import com.usth.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id = #{id}")
    MiaoshaUser getById(long id);

    @Insert("insert into miaosha_user(login_count, nickname, register_date, salt, password, id)values(#{loginCount},#{nickname},#{registerDate},#{salt},#{password},#{id})")
    int insertUser(MiaoshaUser user);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void updatePassword(MiaoshaUser updateUser);
}
